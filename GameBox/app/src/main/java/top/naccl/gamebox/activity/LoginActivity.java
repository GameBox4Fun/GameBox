package top.naccl.gamebox.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.main.MainActivity;
import top.naccl.gamebox.service.UserService;
import top.naccl.gamebox.service.impl.UserServiceImpl;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

public class LoginActivity extends AppCompatActivity {
	private UserService userService = new UserServiceImpl(this);
	private EditText username_et;
	private EditText password_et;
	private Button login_btn;
	private TextView register_text;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		imageView = findViewById(R.id.button_return);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});

		login_btn = findViewById(R.id.button_login);
		username_et = findViewById(R.id.editText_username);
		password_et = findViewById(R.id.editText_password);
		login_btn.setOnClickListener(loginButtonListener);
		register_text = findViewById(R.id.register);
		register_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	View.OnClickListener loginButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String username = username_et.getText().toString();
			String password = password_et.getText().toString();
			if (checkEditText(username, password)) {
				postLogin(username, password);
			}
		}
	};

	private boolean checkEditText(String username, String password) {
		if (username.length() < 3 || username.length() > 10) {
			ToastUtils.showToast(LoginActivity.this, "用户名长度为3-10个字符！");
			return false;
		}
		if (password.length() < 6 || password.length() > 16) {
			ToastUtils.showToast(LoginActivity.this, "密码长度为6-16个字符！");
			return false;
		}
		return true;
	}

	private void postLogin(final String username, final String password) {
		RequestBody requestBody = new FormBody.Builder()
				.add("username", username)
				.add("password", password)
				.build();
		OkHttpUtils.postRequest(ApiConfig.LOGIN_URL, requestBody, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				JSONObject data = jsonResult.getJSONObject("data");
				ToastUtils.showToast(LoginActivity.this, msg);
				if (code == 200) {//登录验证正确
					SharedPreferences.Editor editor = getSharedPreferences("jwt", MODE_PRIVATE).edit();
					editor.putString("token", data.getString("token"));
					editor.commit();
					final User user = JSON.parseObject(data.getString("user"), User.class);
					try {
						boolean res = userService.saveUser(user);
						if (res) {
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
						} else {
							userService.deleteUser();
							userService.saveUser(user);
						}
					} catch (Exception e) {
						//已登录状态 主键约束导致存储失败
					}
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(LoginActivity.this, "请求失败");
			}
		});
	}
}
