package top.naccl.gamebox.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.activity.FavoriteActivity;
import top.naccl.gamebox.activity.LoginActivity;
import top.naccl.gamebox.activity.SettingActivity;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.qrcode.activity.CaptureActivity;
import top.naccl.gamebox.service.UserService;
import top.naccl.gamebox.service.impl.UserServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.Constant;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

import static android.app.Activity.RESULT_OK;

public class MeFragment extends BaseFragment {
	Activity activity;
	private UserService userService;
	private User user;
	private Toolbar toolbar;
	private ImageView messageActionView;
	private ImageView scanActionView;
	private ImageView avatar_iv;
	private TextView username_tv;
	private TextView introduction_tv;
	private TextView favoriteNum_tv;
	private TextView favoriteText_tv;
	private String token;

	public MeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_me, container, false);
		activity = (Activity) getContext();
		userService = new UserServiceImpl(getContext());
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		user = userService.getUser();
		init();
	}

	private void init() {
		findView();
		setOnClick();
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("jwt", Context.MODE_PRIVATE);
		this.token = sharedPreferences.getString("token", "");
		setUserInfo();
	}

	private void setUserInfo() {
		if (user != null) {
			if (user.getAvatar() != null) {
				avatar_iv.setImageBitmap(Base64Utils.base64ToBitmap(user.getAvatar()));
			}
			username_tv.setText(user.getUsername());
			introduction_tv.setText(user.getIntroduction());
			getFavoriteNum();
		} else {
			username_tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					getActivity().startActivity(intent);
				}
			});
		}
	}

	private void getFavoriteNum() {
		OkHttpUtils.getRequest(ApiConfig.FAVORITE_NUM_URL, token, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				Integer data = jsonResult.getInteger("data");
				if (code == 200) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							favoriteNum_tv.setText(String.valueOf(data));
						}
					});
				} else {
					ToastUtils.showToast(activity, msg);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(activity, "请求失败");
			}
		});
	}

	private void findView() {
		toolbar = getActivity().findViewById(R.id.toolbar_me);
		messageActionView = getActivity().findViewById(R.id.message_action_view);
		scanActionView = getActivity().findViewById(R.id.scan_action_view);
		toolbar.inflateMenu(R.menu.setting);

		avatar_iv = getActivity().findViewById(R.id.imageView_avatar);
		username_tv = getActivity().findViewById(R.id.textView_username);
		introduction_tv = getActivity().findViewById(R.id.textView_introduction);
		favoriteNum_tv = getActivity().findViewById(R.id.textView_favoriteNum);
		favoriteText_tv = getActivity().findViewById(R.id.textView_favoriteText);
	}

	private void setOnClick() {
		favoriteNum_tv.setOnClickListener(favoriteClick);
		favoriteText_tv.setOnClickListener(favoriteClick);

		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_scan:
						startQrCode();
						break;
					case R.id.menu_setting:
						Intent intent = new Intent(getActivity(), SettingActivity.class);
						startActivity(intent);
						break;
					case R.id.menu_message:
						break;
				}
				return true;
			}
		});
		toolbar.getMenu().findItem(R.id.menu_message).setActionView(messageActionView);
	}

	View.OnClickListener favoriteClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getActivity(), FavoriteActivity.class);
			startActivity(intent);
		}
	};

	// 开始扫码
	private void startQrCode() {
		// 申请相机权限
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			// 申请权限
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
			return;
		}
		// 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
		if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			// 申请权限
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
			return;
		}
		// 二维码扫码
		Intent intent = new Intent(activity, CaptureActivity.class);
		startActivityForResult(intent, Constant.REQ_QR_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//扫描结果回调
		if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
			//将扫描出的信息显示出来
			Toast.makeText(getContext(), scanResult, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case Constant.REQ_PERM_CAMERA:
				// 摄像头权限申请
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获得授权
					startQrCode();
				} else {
					// 被禁止授权
					Toast.makeText(getContext(), "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
				}
				break;
			case Constant.REQ_PERM_EXTERNAL_STORAGE:
				// 文件读写权限申请
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 获得授权
					startQrCode();
				} else {
					// 被禁止授权
					Toast.makeText(getContext(), "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
				}
				break;
		}
	}
}
