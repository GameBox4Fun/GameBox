package top.naccl.gamebox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import top.naccl.gamebox.R;

public class CommunityFragment extends BaseFragment {

	private View view;
	private Toolbar toolbar;
	private ImageView messageActionView;
	private ImageView scanActionView;
	private ImageView settingActionView;

	public CommunityFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_community, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		toolbar = getActivity().findViewById(R.id.toolbar_community);
		toolbar.inflateMenu(R.menu.community);

	}
}
