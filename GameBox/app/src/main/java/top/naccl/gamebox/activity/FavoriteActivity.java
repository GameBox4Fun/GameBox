package top.naccl.gamebox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.bean.Article;
import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.service.ImageService;
import top.naccl.gamebox.service.impl.ImageServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.ImageUtils;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

public class FavoriteActivity extends AppCompatActivity {
	private ImageService imageService = new ImageServiceImpl(this);
	private List<Article> data;
	private RecyclerView recyclerView;
	private MyRecyclerviewAdapter adapter;
	private Toolbar toolbar;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);

		toolbar = findViewById(R.id.toolbar_favorite);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> finish());

		setRefreshLayout();
		SharedPreferences sharedPreferences = getSharedPreferences("jwt", Context.MODE_PRIVATE);
		this.token = sharedPreferences.getString("token", "");
		getFavoriteArticleList();
	}

	private void setRefreshLayout() {
		RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
		refreshLayout.setRefreshHeader(new BezierRadarHeader(this));
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				getFavoriteArticleList();
				refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
			}
		});
	}

	private void getFavoriteArticleList() {
		OkHttpUtils.getRequest(ApiConfig.FAVORITE_LIST_URL, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				String data = jsonResult.getString("data");

				if (code == 200) {
					List<Article> articles = JSON.parseArray(data, Article.class);

					handleData(articles);
				} else {
					ToastUtils.showToast(FavoriteActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(FavoriteActivity.this, "请求失败");
			}
		});
	}

	private void handleData(List<Article> articles) {
		if (articles.size() == 0) {
			ToastUtils.showToast(this, "没有收藏文章");
			return;
		}
		this.data = articles;
		FavoriteActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				init();
			}
		});
	}

	private void init() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				recyclerView = findViewById(R.id.articleList);
				//设置布局管理器
				recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));
				//设置RecycleView的Adapter
				adapter = new MyRecyclerviewAdapter(FavoriteActivity.this, data);
				recyclerView.setAdapter(adapter);
				//设置分割线，非必须
				recyclerView.addItemDecoration(new DividerItemDecoration(FavoriteActivity.this, DividerItemDecoration.VERTICAL));
				//设置item的增删动画，非必须
				recyclerView.setItemAnimator(new DefaultItemAnimator());
			}
		});
	}

	class MyRecyclerviewAdapter extends RecyclerView.Adapter<MyRecyclerviewAdapter.MyRecyclerViewHolder> {
		private Context context;
		private List<Article> data;

		public MyRecyclerviewAdapter(Context context, List<Article> data) {
			this.context = context;
			this.data = data;
		}

		@Override
		public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(context).inflate(R.layout.recycleview_item, parent, false);
			return new MyRecyclerViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull final MyRecyclerViewHolder holder, final int position) {
			holder.title.setText(data.get(position).getTitle());
			holder.date.setText(data.get(position).getDate());
			holder.views.setText(String.valueOf(data.get(position).getViews()));
			FavoriteActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					loadImage(holder, position);
				}
			});

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showArticle(data.get(position).getId());
				}
			});
		}

		@Override
		public int getItemCount() {
			if (data != null) {
				return data.size();
			}
			return 0;
		}

		private class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
			private TextView title;
			private TextView date;
			private TextView views;
			private ImageView firstPicture;

			public MyRecyclerViewHolder(View itemView) {
				super(itemView);
				title = itemView.findViewById(R.id.textView_title);
				date = itemView.findViewById(R.id.textView_date);
				views = itemView.findViewById(R.id.textView_views);
				firstPicture = itemView.findViewById(R.id.imageView_firstPicture);
			}
		}
	}

	private void loadImage(@NonNull final MyRecyclerviewAdapter.MyRecyclerViewHolder holder, final int position) {
		Image image = imageService.getImage("\'" + data.get(position).getFirstPicture() + "\'");
		if (image != null) {
			final Bitmap bitmap = Base64Utils.base64ToBitmap(image.getBase64());
			FavoriteActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					holder.firstPicture.setImageBitmap(bitmap);
				}
			});
		} else {
			//异步加载网络图片
			ImageUtils.setImageViewFromNetwork(holder.firstPicture, (String) data.get(position).getFirstPicture(), this);
		}
	}

	private void showArticle(Long articleId) {
		Intent intent = new Intent(this, ArticleActivity.class);
		intent.putExtra("articleId", articleId);
		startActivity(intent);
	}
}