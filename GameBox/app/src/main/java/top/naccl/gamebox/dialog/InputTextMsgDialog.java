package top.naccl.gamebox.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import top.naccl.gamebox.R;

public class InputTextMsgDialog extends AppCompatDialog {
	private Context mContext;
	private InputMethodManager imm;
	private EditText messageTextView;
	private TextView confirmBtn;
	private RelativeLayout rlDlg;
	private int mLastDiff = 0;
	private OnTextSendListener onTextSendListener;
	private TextView textView;

	public InputTextMsgDialog(@NonNull Context context, int theme, TextView textView) {
		super(context, theme);
		this.mContext = context;
		this.textView = textView;
		init();
		setLayout();
	}

	public interface OnTextSendListener {
		void onTextSend(String msg);
	}

	public void setOnTextSendListener(OnTextSendListener onTextSendListener) {
		this.onTextSendListener = onTextSendListener;
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		textView.setVisibility(View.VISIBLE);
		//dismiss之前重置mLastDiff值避免下次无法打开
		mLastDiff = 0;
	}

	private void init() {
		setContentView(R.layout.dialog_input_text_msg);
		messageTextView = findViewById(R.id.et_input_message);
		final LinearLayout rlDlgView = findViewById(R.id.rl_inputdlg_view);
		confirmBtn = findViewById(R.id.confrim_btn);
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String msg = messageTextView.getText().toString().trim();
				if (!TextUtils.isEmpty(msg)) {
					onTextSendListener.onTextSend(msg);
					imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
					imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
					messageTextView.setText("");
					dismiss();
				}
				messageTextView.setText(null);
			}
		});
		rlDlg = findViewById(R.id.rl_outside_view);
		rlDlg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() != R.id.rl_inputdlg_view)
					dismiss();
			}
		});
		rlDlgView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
				Rect r = new Rect();
				//获取当前界面可视部分
				getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
				//获取屏幕的高度
				int screenHeight = getWindow().getDecorView().getRootView().getHeight();
				//此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
				int heightDifference = screenHeight - r.bottom;
				if (heightDifference <= 0 && mLastDiff > 0) {
					dismiss();
				}
				mLastDiff = heightDifference;
			}
		});
		rlDlgView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
				dismiss();
			}
		});
		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
				if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0)
					dismiss();
				return false;
			}
		});
	}

	private void setLayout() {
		getWindow().setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = WindowManager.LayoutParams.MATCH_PARENT;
		p.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(p);
		setCancelable(true);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}
}