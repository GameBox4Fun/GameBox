package top.naccl.gamebox.activity;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.naccl.gamebox.R;
import top.naccl.gamebox.api.ApiConfig;
import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.service.UserService;
import top.naccl.gamebox.service.impl.UserServiceImpl;
import top.naccl.gamebox.util.Base64Utils;
import top.naccl.gamebox.util.OkHttpUtils;
import top.naccl.gamebox.util.ToastUtils;

public class MeUpdateActivity extends AppCompatActivity {
	private UserService userService = new UserServiceImpl(this);
	private User user = null;
	private Bitmap bitmap = null;
	private Toolbar toolbar;
	private ImageView avatar_iv;
	private TextView username_tv;
	private EditText introduction_et;
	private LinearLayout sex_ll;
	private LinearLayout education_ll;
	private LinearLayout job_ll;
	private TextView sex_tv;
	private EditText email_ed;
	private TextView job_tv;
	private TextView education_tv;
	private LinearLayout birthday_ll;
	private TextView birthday_tv;
	private int year, month, day;
	private String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me_update);

		this.user = userService.getUser();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			LocalDate today = LocalDate.now();
			year = today.getYear();
			month = today.getMonthValue() - 1;
			day = today.getDayOfMonth();
		}
		init();
	}

	private void init() {
		findView();
		setUserInfo();
		toolbar.setNavigationOnClickListener(view -> finish());
		toolbar.inflateMenu(R.menu.information_save);
		setOnClick();
		SharedPreferences sharedPreferences = getSharedPreferences("jwt", Context.MODE_PRIVATE);
		this.token = sharedPreferences.getString("token", "");
	}

	private void findView() {
		toolbar = findViewById(R.id.toolbar_update);
		avatar_iv = findViewById(R.id.imageView_avatar);
		username_tv = findViewById(R.id.textView_username);
		introduction_et = findViewById(R.id.editText_introduction);
		sex_ll = findViewById(R.id.linearLayout_sex);
		sex_tv = findViewById(R.id.textView_sex);
		education_ll = findViewById(R.id.linearLayout_education);
		education_tv = findViewById(R.id.textView_education);
		job_ll = findViewById(R.id.linearLayout_job);
		email_ed = findViewById(R.id.editText_email);
		job_tv = findViewById(R.id.textView_job);
		birthday_ll = findViewById(R.id.LinearLayout_birthday);
		birthday_tv = findViewById(R.id.textView_birthday);
	}

	private void setUserInfo() {
		if (user != null) {
			if (user.getAvatar() != null) {
				avatar_iv.setImageBitmap(Base64Utils.base64ToBitmap(user.getAvatar()));
			}
			username_tv.setText(user.getUsername());
			if (user.getIntroduction() != null) introduction_et.setText(user.getIntroduction());
			if (user.getSex() != null) sex_tv.setText(user.getSex());
			if (user.getEmail() != null) email_ed.setText(user.getEmail());
			if (user.getEducation() != null) education_tv.setText(user.getEducation());
			if (user.getJob() != null) job_tv.setText(user.getJob());
			if (user.getBirthday() != null) birthday_tv.setText(user.getBirthday());
		}
	}

	private void setOnClick() {
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.icon_save) {
					updateUserInfo();
				}
				return true;
			}
		});
		avatar_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectPicture();
			}
		});
		sex_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowSexChoice();
			}
		});
		education_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowEducationChoice();
			}
		});
		job_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowJobChoice();
			}
		});
		birthday_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowBirthdayChoice();
			}
		});
	}

	private void updateUserInfo() {
		if (user != null) {
			if (bitmap != null) {
				user.setAvatar(Base64Utils.bitmapToBase64(bitmap));
			}
			if (!"".equals(introduction_et.getText().toString())) {
				user.setIntroduction(introduction_et.getText().toString());
			}
			if (!"".equals(sex_tv.getText().toString())) {
				user.setSex(sex_tv.getText().toString());
			}
			if (!"".equals(email_ed.getText().toString())) {
				user.setEmail(email_ed.getText().toString());
			}
			if (!"".equals(education_tv.getText().toString())) {
				user.setEducation(education_tv.getText().toString());
			}
			if (!"".equals(job_tv.getText().toString())) {
				user.setJob(job_tv.getText().toString());
			}
			if (!"".equals(birthday_tv.getText().toString())) {
				user.setBirthday(birthday_tv.getText().toString());
			}
			postUpdateUser(user);
		}
	}

	private void postUpdateUser(final User user) {
		RequestBody requestBody = new FormBody.Builder()
				.add("user", JSON.toJSONString(user))
				.build();
		OkHttpUtils.postRequest(ApiConfig.UPDATE_USER_URL, token, requestBody, new Callback() {
			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				final String result = response.body().string();
				JSONObject jsonResult = JSON.parseObject(result);
				int code = jsonResult.getInteger("code");
				String msg = jsonResult.getString("msg");
				ToastUtils.showToast(MeUpdateActivity.this, msg);
				if (code == 200) {//????????????
					userService.updateUser(user);
				}
			}

			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				ToastUtils.showToast(MeUpdateActivity.this, "????????????");
			}
		});
	}

	/**
	 * ??????????????????????????????
	 */
	private void selectPicture() {
		//intent?????????????????????????????????????????????????????????ComponentName,action,data???
		Intent intent = new Intent();
		intent.setType("image/*");
		//action??????intent???????????????????????????????????????????????????????????????????????????ACTION_GET_CONTENT?????????????????????Type?????????????????????????????????Type???????????????????????????
		intent.setAction(Intent.ACTION_GET_CONTENT);
		//????????????????????????????????????0?????????????????????????????????????????????????????????onActivityResult??????
		startActivityForResult(intent, 1);
	}

	/**
	 * ?????????????????????????????????imageView???
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			//??????????????????????????????
			Uri uri = data.getData();
			//??????content?????????
			ContentResolver contentResolver = this.getContentResolver();
			try {
				//????????????
				Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
				avatar_iv.setImageBitmap(bitmap);
				this.bitmap = bitmap;
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void ShowSexChoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MeUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog);
		//builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("??????????????????");
		//    ?????????????????????????????????
		final String[] sexs = {"???", "???"};
		//    ????????????????????????????????????
		builder.setItems(sexs, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sex_tv.setText(sexs[which]);
			}
		});
		builder.show();
	}

	private void ShowEducationChoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MeUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog);
		builder.setTitle("????????????");
		final String[] educations = {"??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????"};
		builder.setItems(educations, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				education_tv.setText(educations[which]);
			}
		});
		builder.show();
	}

	private void ShowJobChoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MeUpdateActivity.this, android.R.style.Theme_Holo_Light_Dialog);
		builder.setTitle("??????");
		final String[] jobs = {"??????", "????????????", "?????????????????????", "????????????????????????", "??????/????????????/?????????"
				, "???????????????", "??????/???????????????", "???????????????", "???????????????", "????????????", "????????????(???????????????)", "??????"};
		builder.setItems(jobs, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				job_tv.setText(jobs[which]);
			}
		});
		builder.show();
	}

	private void ShowBirthdayChoice() {
		DatePickerDialog datePickerDialog = new DatePickerDialog(MeUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {
				String dateString = mYear + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
				birthday_tv.setText(dateString);
				year = mYear;
				month = monthOfYear;
				day = dayOfMonth;
			}
		}, year, month, day);
		datePickerDialog.show();
	}
}
