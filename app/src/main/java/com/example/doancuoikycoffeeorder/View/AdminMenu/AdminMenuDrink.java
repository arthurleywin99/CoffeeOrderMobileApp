package com.example.doancuoikycoffeeorder.View.AdminMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig.AdminDeleteDrink;
import com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig.AdminNewDrinks;
import com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig.AdminUpdateDrink;

public class AdminMenuDrink extends AppCompatActivity implements View.OnClickListener {
    protected CardView cvDrinkAdd, cvDrinkUpdate, cvDrinkDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_menu_drink);

        cvDrinkAdd = findViewById(R.id.cvDrinkAdd);
        cvDrinkAdd.setOnClickListener(this);
        cvDrinkUpdate = findViewById(R.id.cvDrinkUpdate);
        cvDrinkUpdate.setOnClickListener(this);
        cvDrinkDelete = findViewById(R.id.cvDrinkDelete);
        cvDrinkDelete.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cvDrinkAdd: { startActivity(new Intent(AdminMenuDrink.this, AdminNewDrinks.class)); break; }
            case R.id.cvDrinkUpdate: { startActivity(new Intent(AdminMenuDrink.this, AdminUpdateDrink.class)); break; }
            case R.id.cvDrinkDelete: { startActivity(new Intent(AdminMenuDrink.this, AdminDeleteDrink.class)); break; }
        }
    }
}