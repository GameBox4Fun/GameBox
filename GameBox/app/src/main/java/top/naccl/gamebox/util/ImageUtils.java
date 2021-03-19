package top.naccl.gamebox.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.service.ImageService;
import top.naccl.gamebox.service.impl.ImageServiceImpl;

public class ImageUtils {
	private static ImageService imageService;

	public static void setImageViewFromNetwork(final ImageView imageView, final String imageUrl, final Activity activity) {
		imageService = new ImageServiceImpl(activity);
		OkHttpUtils.getRequest(imageUrl, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				byte[] imageBytes = response.body().bytes();
				final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(bitmap);
					}
				});
				saveBitmap(bitmap, imageUrl);
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(activity, "请求失败");
			}
		});
	}

	public static void setFirstImageViewFromNetwork(final ImageView imageView, final String imageUrl, final Activity activity, final String title, final String date, final int views) {
		imageService = new ImageServiceImpl(activity);
		OkHttpUtils.getRequest(imageUrl, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				byte[] imageBytes = response.body().bytes();
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
				saveBitmap(bitmap, imageUrl);
				bitmap = drawTextToCenterBottom(activity, bitmap, title, 26, Color.WHITE, 50);
				bitmap = drawTextToCenterBottom(activity, bitmap, date + " · 阅读" + views, 16, Color.WHITE, 20);
				final Bitmap finalBitmap = bitmap;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(finalBitmap);
					}
				});
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(activity, "请求失败");
			}
		});
	}

	private static void saveBitmap(Bitmap bitmap, String imageUrl) {
		Image image = new Image();
		image.setUrl(imageUrl);
		image.setBase64(Base64Utils.bitmapToBase64(bitmap));
		imageService.saveImage(image);
	}

	/**
	 * 给图片添加文字到左上角
	 */
	public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingTop) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, dp2px(context, paddingLeft), dp2px(context, paddingTop) + bounds.height());
	}

	/**
	 * 绘制文字到右下角
	 */
	public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingRight, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight), bitmap.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 绘制文字到右上方
	 */
	public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text, int size, int color, int paddingRight, int paddingTop) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight), dp2px(context, paddingTop) + bounds.height());
	}

	/**
	 * 绘制文字到左下方
	 */
	public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, dp2px(context, paddingLeft), bitmap.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 绘制文字到中间
	 */
	public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text, int size, int color) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, (bitmap.getWidth() - bounds.width()) / 2, (bitmap.getHeight() + bounds.height()) / 2);
	}

	/**
	 * 绘制文字到中间
	 */
	public static Bitmap drawTextToCenterBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(bitmap, text, paint, (bitmap.getWidth() - bounds.width()) / 2, bitmap.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 图片上绘制文字
	 */
	private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
		Bitmap.Config bitmapConfig = bitmap.getConfig();
		paint.setDither(true); // 获取跟清晰的图像采样
		paint.setFilterBitmap(true);// 过滤一些
		if (bitmapConfig == null) {
			bitmapConfig = Bitmap.Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, paddingLeft, paddingTop, paint);
		return bitmap;
	}

	/**
	 * 缩放图片
	 */
	public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
		if (w == 0 || h == 0 || src == null) {
			return src;
		} else {
			// 记录src的宽高
			int width = src.getWidth();
			int height = src.getHeight();
			// 创建一个matrix容器
			Matrix matrix = new Matrix();
			// 计算缩放比例
			float scaleWidth = (float) (w / width);
			float scaleHeight = (float) (h / height);
			// 开始缩放
			matrix.postScale(scaleWidth, scaleHeight);
			// 创建缩放后的图片
			return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
		}
	}

	/**
	 * dip转pix
	 */
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
