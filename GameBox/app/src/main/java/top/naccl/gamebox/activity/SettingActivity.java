package top.naccl.gamebox.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import top.naccl.gamebox.R;
import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.service.UserService;
import top.naccl.gamebox.service.impl.UserServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.ToastUtils;


public class SettingActivity extends AppCompatActivity {
	UserService userService = new UserServiceImpl(this);
	private ConstraintLayout layout_update;
	private ImageView avatar_iv;
	private TextView username_tv;
	private TextView id_tv;
	private Button logout_btn;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		toolbar = findViewById(R.id.toolbar_article);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(view -> finish());

		layout_update = findViewById(R.id.layout_update);
		avatar_iv = findViewById(R.id.imageView_avatar);
		username_tv = findViewById(R.id.textView_username);
		id_tv = findViewById(R.id.textView_id);
		logout_btn = findViewById(R.id.button_logout);
		initUserData();
	}

	private void initUserData() {
		User user = userService.getUser();
		if (user != null) {
			username_tv.setText(user.getUsername());
			id_tv.setText("ID:" + user.getId());
			if (user.getAvatar() != null) {
				Bitmap bitmap = Base64Utils.base64ToBitmap(user.getAvatar());
				if (bitmap != null) {
					avatar_iv.setImageBitmap(bitmap);
				} else {
					avatar_iv.setImageResource(R.drawable.avatar_login_default);
				}
			} else {
				avatar_iv.setImageResource(R.drawable.avatar_login_default);
			}
			//用户登录状态才能修改资料、退出登录
			layout_update.setOnClickListener(updateLayoutListener);
			logout_btn.setEnabled(true);
			logout_btn.setOnClickListener(logoutButtonListener);
		} else {
			username_tv.setText("请登录");
			id_tv.setText("ID:00000");
			avatar_iv.setImageResource(R.drawable.avatar_login_default);
		}
	}

	View.OnClickListener updateLayoutListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			//跳转修改资料页面
			Intent intent = new Intent(SettingActivity.this, MeUpdateActivity.class);
			startActivity(intent);
		}
	};

	View.OnClickListener logoutButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (userService.deleteUser()) {
				ToastUtils.showToast(SettingActivity.this, "退出成功");
				//跳转 me_logout 页面
				Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
				startActivity(intent);
			} else {
				ToastUtils.showToast(SettingActivity.this, "退出失败，请重试");
			}
		}
	};
}
