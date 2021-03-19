package top.naccl.gamebox.util;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {
	public static void showToast(final Activity activity, final String msg) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
