package top.naccl.gamebox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Utils {
	public static String bitmapToBase64(Bitmap bitmap) {
		String reslut = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				//压缩只对保存有效果bitmap还是原来的大小,100不压缩
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				baos.flush();
				baos.close();
				byte[] byteArray = baos.toByteArray();
				reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return reslut;
	}

	public static Bitmap base64ToBitmap(String base64String) {
		byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
		return bitmap;
	}
}