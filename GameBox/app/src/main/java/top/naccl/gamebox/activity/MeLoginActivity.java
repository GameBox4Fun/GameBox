package top.naccl.gamebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import top.naccl.gamebox.R;

public class MeLoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView messageActionView;
    private ImageView scanActionView;
    private ImageView settingActionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me);

        toolbar = findViewById(R.id.toolbar_me);
        messageActionView = findViewById(R.id.message_action_view);
        scanActionView = findViewById(R.id.scan_action_view);
        settingActionView = findViewById(R.id.setting_action_view);

        toolbar.inflateMenu(R.menu.setting);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_scan:
                        break;
                    case R.id.menu_setting:
                        Intent intent = new Intent(MeLoginActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_message:
                        break;
                }
                return true;
            }
        });

        toolbar.getMenu().findItem(R.id.menu_message).setActionView(messageActionView);
//        messageActionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        toolbar.getMenu().findItem(R.id.menu_scan).setActionView(scanActionView);
//        scanActionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        toolbar.getMenu().findItem(R.id.menu_setting).setActionView(settingActionView);
//        settingActionView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MeLoginActivity.this, SettingActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
