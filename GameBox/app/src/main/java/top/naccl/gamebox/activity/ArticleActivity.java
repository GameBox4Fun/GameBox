package top.naccl.gamebox.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.bean.Article;
import top.naccl.gamebox.bean.Comment;
import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.dialog.InputTextMsgDialog;
import top.naccl.gamebox.service.ArticleService;
import top.naccl.gamebox.service.ImageService;
import top.naccl.gamebox.service.UserService;
import top.naccl.gamebox.service.impl.ArticleServiceImpl;
import top.naccl.gamebox.service.impl.ImageServiceImpl;
import top.naccl.gamebox.service.impl.UserServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.HtmlUtils;
import top.naccl.gamebox.util.ImageUtils;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

public class ArticleActivity extends AppCompatActivity {
	private ArticleService articleService = new ArticleServiceImpl(this);
	private ImageService imageService = new ImageServiceImpl(this);
	private UserService userService = new UserServiceImpl(this);
	private Long articleId;
	private Article article;
	private User user;
	private Boolean articleFavorite = false;
	private Drawable add;
	private ImageView firstPicture_iv;
	private TextView title_tv;
	private TextView authorName_tv;
	private TextView articleDate_tv;
	private Button favorite1_btn;
	private Button favorite2_btn;
	private Button star_btn;
	private TextView views_tv;
	private TextView content_tv;
	private TextView comment_tv;
	private Toolbar toolbar;
	private InputTextMsgDialog inputTextMsgDialog;
	private String token;
	private List<Comment> commentList;
	private RecyclerView recyclerView;
	private ArticleActivity.MyRecyclerviewAdapter adapter;
	private int count;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);

		toolbar = findViewById(R.id.toolbar_article);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> finish());

		add = this.getResources().getDrawable(R.drawable.icon_article_add);
		add.setBounds(0, 0, add.getMinimumWidth(), add.getMinimumHeight());
		this.articleId = getIntent().getExtras().getLong("articleId", 0);

		if (articleId == 0) {
			ToastUtils.showToast(this, "异常错误");
		}
		this.user = userService.getUser();
		findView();
		setOnClick();
		setRefreshLayout();
		SharedPreferences sharedPreferences = getSharedPreferences("jwt", Context.MODE_PRIVATE);
		this.token = sharedPreferences.getString("token", "");
		setArticle(articleId);
		getCommentList(articleId);
	}

	class MyRecyclerviewAdapter extends RecyclerView.Adapter<ArticleActivity.MyRecyclerviewAdapter.MyRecyclerViewHolder> {
		private Context context;
		private List<Comment> commentList;

		public MyRecyclerviewAdapter(Context context, List<Comment> commentList) {
			this.context = context;
			this.commentList = commentList;
		}

		@Override
		public ArticleActivity.MyRecyclerviewAdapter.MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
			return new ArticleActivity.MyRecyclerviewAdapter.MyRecyclerViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull final ArticleActivity.MyRecyclerviewAdapter.MyRecyclerViewHolder holder, final int position) {
			holder.content.setText(commentList.get(position).getContent());
			holder.date.setText(simpleDateFormat.format(commentList.get(position).getCreateTime()));
			holder.username.setText(commentList.get(position).getUsername());
			if (commentList.get(position).getAvatar() != null) {
				holder.avatar.setImageBitmap(Base64Utils.base64ToBitmap(commentList.get(position).getAvatar()));
			}
		}

		@Override
		public int getItemCount() {
			if (commentList != null) {
				return commentList.size();
			}
			return 0;
		}

		private class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
			private TextView username;
			private TextView date;
			private TextView content;
			private ImageView avatar;

			public MyRecyclerViewHolder(View itemView) {
				super(itemView);
				username = itemView.findViewById(R.id.textView_commentUsername);
				date = itemView.findViewById(R.id.textView_commentTime);
				content = itemView.findViewById(R.id.textView_commentContent);
				avatar = itemView.findViewById(R.id.roundImageView_commentAvatar);
			}
		}
	}

	private void init() {
		this.runOnUiThread(new Runnable() {
			public void run() {
				recyclerView = findViewById(R.id.commentList);
				//设置布局管理器
				recyclerView.setLayoutManager(new LinearLayoutManager(ArticleActivity.this));
				//设置RecycleView的Adapter
				adapter = new ArticleActivity.MyRecyclerviewAdapter(ArticleActivity.this, commentList);
				recyclerView.setAdapter(adapter);
				//设置分割线，非必须
				recyclerView.addItemDecoration(new DividerItemDecoration(ArticleActivity.this, DividerItemDecoration.VERTICAL));
				//设置item的增删动画，非必须
				recyclerView.setItemAnimator(new DefaultItemAnimator());
				comment_tv.setText("评论(已有" + count + "条评论)");
			}
		});

	}

	private void setRefreshLayout() {
		RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
		refreshLayout.setRefreshHeader(new BezierRadarHeader(this));
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshLayout refreshlayout) {
				ArticleActivity.this.article = null;
				if (articleService.deleteArticle(articleId)) {
					setArticle(articleId);
				} else {
					refreshlayout.finishRefresh(false);//传入false表示刷新失败
				}
				refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
			}
		});
	}

	private void findView() {
		firstPicture_iv = findViewById(R.id.imageView_articleFirstPicture);
		title_tv = findViewById(R.id.textView_articleTitle);
		authorName_tv = findViewById(R.id.textView_authorName);
		articleDate_tv = findViewById(R.id.textView_articleDate);
		favorite1_btn = findViewById(R.id.button_favorite1);
		star_btn = findViewById(R.id.button_star);
		favorite2_btn = findViewById(R.id.button_favorite2);
		views_tv = findViewById(R.id.textView_views);
		content_tv = findViewById(R.id.textView_content);
		comment_tv = findViewById(R.id.textView_comment);
	}

	private void setOnClick() {
		star_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user != null) {
					postStar(articleId);
				} else {
					ToastUtils.showToast(ArticleActivity.this, "请登录");
				}
			}
		});
		comment_tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user != null) {
					comment_tv.setVisibility(View.INVISIBLE);
					inputTextMsgDialog.show();
				} else {
					ToastUtils.showToast(ArticleActivity.this, "请登录");
				}

			}
		});
		inputTextMsgDialog = new InputTextMsgDialog(this, R.style.dialog_center, comment_tv);
		inputTextMsgDialog.setOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
			@Override
			public void onTextSend(String msg) {
				if (user != null) {
					postComment(msg);
				} else {
					ToastUtils.showToast(ArticleActivity.this, "请登录");
				}

			}
		});
		favorite1_btn.setOnClickListener(favorite);
		favorite2_btn.setOnClickListener(favorite);
	}

	View.OnClickListener favorite = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (user != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ArticleActivity.this);
				builder.setTitle("收藏");
				builder.setMessage(articleFavorite ? "确定要取消收藏吗？" : "确定要收藏此文章吗？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						postFavorite(articleId);
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
				builder.create().show();
			} else {
				ToastUtils.showToast(ArticleActivity.this, "请登录");
			}
		}
	};

	private void setArticle(Long id) {
		Article article = articleService.getArticle(id);
		if (article == null) {
			getArticleFromServer(id);
		} else {
			this.article = article;
			article.setViews(article.getViews() + 1);
			setArticleInfo();
			setArticleContent();
			getFavorite(id);
		}
	}

	private void checkFavorite(JSONObject jsonResult) {
		if (user != null) {
			boolean favorite = jsonResult.getBoolean("favorite");
			this.articleFavorite = favorite;
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (articleFavorite) {
						favorite1_btn.setCompoundDrawables(null, null, null, null);
						favorite1_btn.setText("已收藏");
						favorite2_btn.setText("已收藏");
					} else {
						favorite1_btn.setCompoundDrawables(add, null, null, null);
						favorite1_btn.setText("收藏");
						favorite2_btn.setText("收藏");
					}
				}
			});
		}
	}

	private void setArticleInfo() {
		Image image = imageService.getImage("\'" + article.getFirstPicture() + "\'");
		if (image != null) {
			final Bitmap bitmap = Base64Utils.base64ToBitmap(image.getBase64());
			ArticleActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					firstPicture_iv.setImageBitmap(bitmap);
				}
			});
		} else {
			ImageUtils.setImageViewFromNetwork(firstPicture_iv, article.getFirstPicture(), this);
		}
		ArticleActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				title_tv.setText(article.getTitle());
				authorName_tv.setText(article.getAuthor());
				articleDate_tv.setText(article.getDate());
				star_btn.setText("赞  " + article.getStar());
				views_tv.setText("阅读  " + article.getViews());
			}
		});
	}

	private void setArticleContent() {
		ArticleActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				HtmlUtils.setTextFromHtml(ArticleActivity.this, content_tv, article.getContent());
			}
		});
	}

	private void getArticleFromServer(Long id) {
		OkHttpUtils.getRequest(ApiConfig.ARTICLE_URL + id, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				JSONObject data = jsonResult.getJSONObject("data");
				if (code == 200) {//请求成功
					final Article article = JSON.parseObject(data.getString("article"), Article.class);
					ArticleActivity.this.article = article;
					checkFavorite(data);
					setArticleInfo();
					setArticleContent();
					articleService.saveArticle(article);
				} else {
					ToastUtils.showToast(ArticleActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}

	private void getCommentList(Long id) {
		OkHttpUtils.getRequest(ApiConfig.COMMENT_LIST_URL + id, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				String data = jsonResult.getString("data");
				if (code == 200) {//请求成功
					List<Comment> comment = JSON.parseArray(data, Comment.class);
					handleData(comment);
				} else {
					ToastUtils.showToast(ArticleActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}

	private void handleData(List<Comment> comment) {
		if (comment.size() == 0) {
			return;
		}
		count = comment.size();
		this.commentList = comment;
		ArticleActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				init();
			}
		});
	}

	private void postStar(Long id) {
		OkHttpUtils.postRequest(ApiConfig.ARTICLE_STAR_URL + id, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				if (code == 200) {//点赞成功，更新点赞数
					article.setStar(article.getStar() + 1);
					ArticleActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							star_btn.setText("赞  " + article.getStar());
						}
					});
					articleService.updateStar(article);
				} else {//登录失效，请重新登录
					ToastUtils.showToast(ArticleActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}

	private void postFavorite(Long id) {
		OkHttpUtils.postRequest(ApiConfig.FAVORITE_URL + id, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				JSONObject data = jsonResult.getJSONObject("data");
				ToastUtils.showToast(ArticleActivity.this, msg);
				if (code == 200) {//收藏/取消收藏成功
					checkFavorite(data);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}

	private void getFavorite(Long id) {
		OkHttpUtils.getRequest(ApiConfig.ARTICLE_UPDATE_VIEW_URL + id, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				JSONObject data = jsonResult.getJSONObject("data");
				if (code == 200) {//请求成功
					checkFavorite(data);
					articleService.updateViews(article);
				} else {
					ToastUtils.showToast(ArticleActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}

	private void postComment(String content) {
		RequestBody requestBody = new FormBody.Builder()
				.add("content", content)
				.build();
		OkHttpUtils.postRequest(ApiConfig.COMMENT_POST_URL + articleId, token, requestBody, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				if (code == 200) {
					getCommentList(articleId);
				} else {//登录失效，请重新登录
					ToastUtils.showToast(ArticleActivity.this, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(ArticleActivity.this, "请求失败");
			}
		});
	}
}