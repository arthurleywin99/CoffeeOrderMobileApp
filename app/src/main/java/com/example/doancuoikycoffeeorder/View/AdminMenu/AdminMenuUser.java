package com.example.doancuoikycoffeeorder.View.AdminMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.Admin_UsersConfig.AdminAddUser;
import com.example.doancuoikycoffeeorder.View.Admin_UsersConfig.AdminDeleteUser;
import com.example.doancuoikycoffeeorder.View.Admin_UsersConfig.AdminResetPassword;
import com.example.doancuoikycoffeeorder.View.Admin_UsersConfig.AdminUpdateUser;
import com.example.doancuoikycoffeeorder.View.ChangePassword.ChangePassword;

public class AdminMenuUser extends AppCompatActivity implements View.OnClickListener {
    protected CardView cvUserAdd, cvPasswordReset, cvUserUpdate, cvPasswordChange, cvUserDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu_user);

        cvUserAdd = findViewById(R.id.cvUserAdd);
        cvUserAdd.setOnClickListener(this);
        cvPasswordReset = findViewById(R.id.cvPasswordReset);
        cvPasswordReset.setOnClickListener(this);
        cvUserUpdate = findViewById(R.id.cvUserUpdate);
        cvUserUpdate.setOnClickListener(this);
        cvPasswordChange = findViewById(R.id.cvPasswordChange);
        cvPasswordChange.setOnClickListener(this);
        cvUserDelete = findViewById(R.id.cvUserDelete);
        cvUserDelete.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cvUserAdd: { startActivity(new Intent(AdminMenuUser.this, AdminAddUser.class)); break; }
            case R.id.cvPasswordReset: { startActivity(new Intent(AdminMenuUser.this, AdminResetPassword.class)); break; }
            case R.id.cvUserUpdate: { startActivity(new Intent(AdminMenuUser.this, AdminUpdateUser.class)); break; }
            case R.id.cvPasswordChange: { startActivity(new Intent(AdminMenuUser.this, ChangePassword.class)); break; }
            case R.id.cvUserDelete: { startActivity(new Intent(AdminMenuUser.this, AdminDeleteUser.class)); break; }
        }
    }
}