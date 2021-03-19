package top.naccl.gamebox.util;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtils {
	private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS)
			.build();

	public static void getRequest(String url, okhttp3.Callback callback) {
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		okHttpClient.newCall(request).enqueue(callback);
	}

	public static void getRequest(String url, String token, okhttp3.Callback callback) {
		Request request = new Request.Builder()
				.url(url)
				.get()
				.addHeader("Authorization", token)
				.build();
		okHttpClient.newCall(request).enqueue(callback);
	}

	public static void postRequest(String url, String token, okhttp3.Callback callback) {
		Request request = new Request.Builder()
				.url(url)
				.post(new FormBody.Builder().build())
				.addHeader("Authorization", token)
				.build();
		okHttpClient.newCall(request).enqueue(callback);
	}

	public static void postRequest(String url, RequestBody requestBody, okhttp3.Callback callback) {
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		okHttpClient.newCall(request).enqueue(callback);
	}

	public static void postRequest(String url, String token, RequestBody requestBody, okhttp3.Callback callback) {
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.addHeader("Authorization", token)
				.build();
		okHttpClient.newCall(request).enqueue(callback);
	}
}
