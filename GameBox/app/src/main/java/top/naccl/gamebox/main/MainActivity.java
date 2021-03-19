package top.naccl.gamebox.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import top.naccl.gamebox.R;
import top.naccl.gamebox.fragment.CommunityFragment;
import top.naccl.gamebox.fragment.HomeFragment;
import top.naccl.gamebox.fragment.MeFragment;

public class MainActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private List<Fragment> fragments;
	private RadioGroup radioGroup;
	private RadioButton t1, t2, t3;

	private static MainActivity mInstance;

	public static Context getInstance() {
		if (mInstance == null) {
			mInstance = new MainActivity();
		}
		return mInstance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bottom_navigation_bar);

		initRadioButton();
		initViewPager();
	}

	public void initRadioButton() {
		radioGroup = findViewById(R.id.radioGroup);
		t1 = findViewById(R.id.radioButton1);
		t2 = findViewById(R.id.radioButton2);
		t3 = findViewById(R.id.radioButton3);

		t1.setOnClickListener(new MyOnClickLister(0));
		t2.setOnClickListener(new MyOnClickLister(1));
		t3.setOnClickListener(new MyOnClickLister(2));
	}

	public class MyOnClickLister implements View.OnClickListener {
		private int index = 0;

		MyOnClickLister(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	public void initViewPager() {
		viewPager = findViewById(R.id.viewpager);
		fragments = new ArrayList<>();
		fragments.add(new HomeFragment());
		fragments.add(new CommunityFragment());
		fragments.add(new MeFragment());

		viewPager.setAdapter(new MyFragmentAdapter(this.getSupportFragmentManager(), fragments));
		radioGroup.check(R.id.radioButton1);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				int current = viewPager.getCurrentItem();
				switch (current) {
					case 0:
						radioGroup.check(R.id.radioButton1);
						break;
					case 1:
						radioGroup.check(R.id.radioButton2);
						break;
					case 2:
						radioGroup.check(R.id.radioButton3);
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	public class MyFragmentAdapter extends FragmentPagerAdapter {
		List<Fragment> fragments;

		MyFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@NonNull
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
}
