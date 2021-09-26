package com.example.doancuoikycoffeeorder.View.AdminMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.Admin_Report.AdminReport;
import com.example.doancuoikycoffeeorder.View.Login.Login;
import com.example.doancuoikycoffeeorder.View.UserInfo.UserInfo;

public class AdminMenu extends AppCompatActivity implements View.OnClickListener {
    protected CardView cvUserManager, cvDrinkManager, cvReportManager, cvLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu);

        cvUserManager = findViewById(R.id.cvUserManager);
        cvUserManager.setOnClickListener(this);
        cvDrinkManager = findViewById(R.id.cvDrinkManager);
        cvDrinkManager.setOnClickListener(this);
        cvReportManager = findViewById(R.id.cvReportManager);
        cvReportManager.setOnClickListener(this);
        cvLogOut = findViewById(R.id.cvLogOut);
        cvLogOut.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cvUserManager: { startActivity(new Intent(AdminMenu.this, AdminMenuUser.class)); break; }
            case R.id.cvDrinkManager: { startActivity(new Intent(AdminMenu.this, AdminMenuDrink.class)); break; }
            case R.id.cvReportManager: { startActivity(new Intent(AdminMenu.this, AdminReport.class)); break; }
            case R.id.cvLogOut: { startActivity(new Intent(AdminMenu.this, Login.class)); finish(); break; }
        }
    }
}
