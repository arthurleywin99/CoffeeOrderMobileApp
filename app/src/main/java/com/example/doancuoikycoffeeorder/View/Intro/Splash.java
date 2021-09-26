package com.example.doancuoikycoffeeorder.View.Intro;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.Login.Login;

public class Splash extends AppCompatActivity {
    TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        txtVersion = (TextView) findViewById(R.id.txtVersion);
        try {
            PackageInfo packageInfo = (PackageInfo) getPackageManager().getPackageInfo(getPackageName(), 0);
            /*Lấy version của app từ gradle*/
            txtVersion.setText(getResources().getText(R.string.Version) + " " + packageInfo.versionName);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(Splash.this, Login.class));
                finish();
            }, 2500);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
