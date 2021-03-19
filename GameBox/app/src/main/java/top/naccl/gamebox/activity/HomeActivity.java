package top.naccl.gamebox.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.bean.Article;
import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.service.ImageService;
import top.naccl.gamebox.service.impl.ImageServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.ImageUtils;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

public class HomeActivity extends AppCompatActivity {
	ImageService imageService = new ImageServiceImpl(this);
	List<Article> data;
	RecyclerView recyclerView;
	MyRecyclerviewAdapter adapter;
	Integer start = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_home);

		setRefreshLayout();
		getArticleList(start, false);
	}

	private void setRefreshLayout() {
		RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
		refreshLayout.setRefreshHeader(new BezierRadarHeader(this));
		refreshLayout.setRefreshFooter(new BallPulseFooter(this));
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				start = 1;
				getArticleList(start, false);
				refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
			}
		});
		refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore(RefreshLayout refreshlayout) {
				start += 1;
				getArticleList(start, true);
				refreshlayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
			}
		});
	}

	private void getArticleList(Integer start, final boolean loadMore) {
		OkHttpUtils.getRequest(ApiConfig.ARTICLE_LIST_URL + start, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				Integer status = jsonResult.getInteger("status");
				if (status != null) {
					if (status == 1) {
						List<Article> articles = JSON.parseObject(jsonResult.getString("articles"), new TypeReference<List<Article>>() {
						});
						handleData(articles, loadMore);
					} else if (status == 0) {//服务端异常，操作失败
						ToastUtils.showToast(HomeActivity.this, jsonResult.getString("msg"));
					} else {//404或其它，请求失败
						ToastUtils.showToast(HomeActivity.this, "请求失败");
					}
				} else {
					ToastUtils.showToast(HomeActivity.this, "请求失败");
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(HomeActivity.this, "请求失败");
			}
		});
	}

	private void handleData(List<Article> articles, boolean loadMore) {
		if (articles.size() == 0) {
			ToastUtils.showToast(this, "没有更多文章了");
			return;
		}
		if (loadMore) {
			this.data.addAll(articles);
			HomeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					adapter.addData(data.size());
				}
			});
		} else {
			this.data = articles;
			HomeActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					init();
				}
			});
		}
	}

	private void init() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				recyclerView = findViewById(R.id.articleList);
				//设置布局管理器
				recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
				//设置RecycleView的Adapter
				adapter = new MyRecyclerviewAdapter(HomeActivity.this, data);
				recyclerView.setAdapter(adapter);
				//设置分割线，非必须
				recyclerView.addItemDecoration(new DividerItemDecoration(HomeActivity.this, DividerItemDecoration.VERTICAL));
				//设置item的增删动画，非必须
				recyclerView.setItemAnimator(new DefaultItemAnimator());
			}
		});
	}

	class MyRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

		public static final int TYPE_IMAGE = 0;
		public static final int TYPE_LIST = 1;
		private Context context;
		private List<Article> data;

		public MyRecyclerviewAdapter(Context context, List<Article> data) {
			this.context = context;
			this.data = data;
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			if (viewType == TYPE_IMAGE) {
				View view = LayoutInflater.from(context).inflate(R.layout.recycleview_item_image, parent, false);
				return new MyFirstPictureRecyclerViewHolder(view);
			} else {
				View view = LayoutInflater.from(context).inflate(R.layout.recycleview_item, parent, false);
				return new MyRecyclerViewHolder(view);
			}
		}

		@Override
		public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
			if (holder instanceof MyFirstPictureRecyclerViewHolder) {
				setFirstPicture(((MyFirstPictureRecyclerViewHolder) holder).firstPicture, data.get(position));
				((MyFirstPictureRecyclerViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showArticle(data.get(position).getId());
					}
				});
			} else {
				((MyRecyclerViewHolder) holder).title.setText(data.get(position).getTitle());
				((MyRecyclerViewHolder) holder).date.setText(data.get(position).getDate());
				((MyRecyclerViewHolder) holder).views.setText(String.valueOf(data.get(position).getViews()));
				HomeActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						loadImage((MyRecyclerViewHolder) holder, position);
					}
				});

				((MyRecyclerViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showArticle(data.get(position).getId());
					}
				});
			}
		}

		private void setFirstPicture(ImageView imageView, final Article firstArticle) {
			Image image = imageService.getImage("\'" + firstArticle.getFirstPicture() + "\'");
			String firstTitle;
			if (firstArticle.getTitle().length() > 16) {
				firstTitle = firstArticle.getTitle().substring(0, 16);
			} else {
				firstTitle = firstArticle.getTitle();
			}
			if (image != null) {
				Bitmap bitmap = ImageUtils.drawTextToCenterBottom(HomeActivity.this, Base64Utils.base64ToBitmap(image.getBase64()), firstTitle, 26, Color.WHITE, 50);
				bitmap = ImageUtils.drawTextToCenterBottom(HomeActivity.this, bitmap, firstArticle.getDate() + " · 阅读" + firstArticle.getViews(), 16, Color.WHITE, 20);
				imageView.setImageBitmap(bitmap);
			} else {
				ImageUtils.setFirstImageViewFromNetwork(imageView, firstArticle.getFirstPicture(), HomeActivity.this, firstTitle, firstArticle.getDate(), firstArticle.getViews());
			}
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showArticle(firstArticle.getId());
				}
			});
		}

		public void addData(int position) {
			data = HomeActivity.this.data;
			//添加动画
			notifyItemInserted(position);
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return TYPE_IMAGE;
			} else {
				return TYPE_LIST;
			}
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

		private class MyFirstPictureRecyclerViewHolder extends RecyclerView.ViewHolder {
			private ImageView firstPicture;

			public MyFirstPictureRecyclerViewHolder(View itemView) {
				super(itemView);
				firstPicture = itemView.findViewById(R.id.imageView_firstPicture);
			}
		}
	}

	private void loadImage(@NonNull final MyRecyclerviewAdapter.MyRecyclerViewHolder holder, final int position) {
		Image image = imageService.getImage("\'" + data.get(position).getFirstPicture() + "\'");
		if (image != null) {
			final Bitmap bitmap = Base64Utils.base64ToBitmap(image.getBase64());
			HomeActivity.this.runOnUiThread(new Runnable() {
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