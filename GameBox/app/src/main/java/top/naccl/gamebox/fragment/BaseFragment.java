package top.naccl.gamebox.fragment;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import top.naccl.gamebox.main.MainActivity;


public class BaseFragment extends Fragment {
	private Activity activity;

	public Context getContext() {
		if (activity == null) {
			return MainActivity.getInstance();
		}
		return activity;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = getActivity();
	}
}
