package top.naccl.gamebox.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.service.ImageService;
import top.naccl.gamebox.service.impl.ImageServiceImpl;

/**
 * html渲染文章详情页的工具类
 */
public class HtmlUtils {
	private static ImageService imageService;
	private static Drawable drawable;

	/**
	 * 加载html中img标签的图片 设置点击事件 显示在TextView
	 */
	public static void setTextFromHtml(final Activity activity, final TextView textView, final String content) {
		imageService = new ImageServiceImpl(activity);
		if (TextUtils.isEmpty(content) || activity == null || textView == null) return;
		synchronized (HtmlUtils.class) {//加个同步锁
			textView.setMovementMethod(LinkMovementMethod.getInstance());//使图片可以获取焦点 对img标签添加点击事件
			textView.setText(Html.fromHtml(content));//默认不处理图片
			new Thread(new Runnable() {//子线程加载图片
				@Override
				public void run() {
					Html.ImageGetter imageGetter = new Html.ImageGetter() {//加载source中的图片
						@Override
						public Drawable getDrawable(String source) {
							//source = "http://www.baidu.com/" + source;//source就是img中的src，相对路径的此处可以对其进行处理添加头部
							Image image = imageService.getImage("\'" + source + "\'");
							if (image != null) {
								Bitmap bitmap = Base64Utils.base64ToBitmap(image.getBase64());
								drawable = new BitmapDrawable(bitmap);
							} else {
								drawable = getImageFromNetwork(source);
								saveDrawable(source);
							}
							if (drawable != null) {
								double w = drawable.getIntrinsicWidth();
								double h = drawable.getIntrinsicHeight();
								// getWidth()需要等待textView加载完成，否则是0，网络加载图片有延迟可用，本地读取图片太快
//								int width = textView.getWidth();
								int width = 1002;
								int height = (int) (h * (width / w));
								drawable.setBounds(0, 0, width, height);
								return drawable;
							} else return null;
						}
					};
					final CharSequence charSequence;
					//new URLTagHandler(activity)添加img标签的点击事件
					//FROM_HTML_MODE_COMPACT：html块元素之间使用一个换行符分隔
					//FROM_HTML_MODE_LEGACY：html块元素之间使用两个换行符分隔
					//reference:
					//https://developer.android.com/reference/android/text/Html#fromHtml(java.lang.String,%20int,%20android.text.Html.ImageGetter,%20android.text.Html.TagHandler)
					//https://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						charSequence = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, new URLTagHandler(activity));
					} else {
						charSequence = Html.fromHtml(content, imageGetter, new URLTagHandler(activity));
					}
					//在activity的runOnUiThread方法中更新ui
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							textView.setText(charSequence);
						}
					});
				}
			}).start();
		}
	}

	private static void saveDrawable(String source) {
		/**
		 * 这个异常不知道怎么解决，随机发生
		 * java.lang.ClassCastException: android.graphics.drawable.AnimatedImageDrawable cannot be cast to android.graphics.drawable.BitmapDrawable
		 */
		try {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			Image image1 = new Image();
			image1.setUrl(source);
			image1.setBase64(Base64Utils.bitmapToBase64(bitmap));
			imageService.saveImage(image1);
		} catch (Exception e) {
			Log.e("异常", "saveDrawable: " + e);
		}
	}

	/**
	 * 从网络请求图片
	 */
	private static Drawable getImageFromNetwork(String imageUrl) {
		URL fileUrl = null;
		Drawable drawable = null;
		try {
			fileUrl = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
			conn.setDoInput(true);//将URL连接用于输入
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			drawable = Drawable.createFromStream(inputStream, null);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return drawable;
	}


	/**
	 * 对img标签添加点击事件
	 */
	private static class URLTagHandler implements Html.TagHandler {
		private Context mContext;

		public URLTagHandler(Context context) {
			mContext = context.getApplicationContext();
		}

		@Override
		public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
			// 处理img标签
			if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
				// 获取长度
				int len = output.length();
				// 获取图片地址
				ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
				String imgURL = images[0].getSource();
				// 使图片可点击并监听点击事件
				output.setSpan(new ClickableImage(mContext, imgURL), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		private class ClickableImage extends ClickableSpan {
			private String imgUrl;
			private Context context;

			public ClickableImage(Context context, String imgUrl) {
				this.context = context;
				this.imgUrl = imgUrl;
			}

			@Override
			public void onClick(View widget) {
				Toast.makeText(context, "点击图片的地址" + imgUrl, Toast.LENGTH_SHORT).show();
			}
		}
	}


}
