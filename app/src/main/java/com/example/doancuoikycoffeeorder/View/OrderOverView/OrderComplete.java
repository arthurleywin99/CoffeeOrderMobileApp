package com.example.doancuoikycoffeeorder.View.OrderOverView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.Main.MainActivity;

public class OrderComplete extends AppCompatActivity implements View.OnClickListener {
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_complete);

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(OrderComplete.this, MainActivity.class));
        finish(); /*Back về thì không thể quay lại Activity OrderComplete nữa*/
    }
}
