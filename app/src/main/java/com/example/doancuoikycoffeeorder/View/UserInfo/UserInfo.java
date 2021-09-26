package com.example.doancuoikycoffeeorder.View.UserInfo;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.userLogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.Login.UserLoginModel;
import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.ChangePassword.ChangePassword;
import com.example.doancuoikycoffeeorder.View.Login.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity implements View.OnClickListener {
    public UserLoginModel userLoginModel;
    private TextView tvFullnameTitle, tvFullname, tvGender, tvEmail, tvAddress, tvPhoneNumber, tvAccountType, tvDateofBirth;
    protected TextView tvChangePass, tvLogout;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        try {
            tvFullnameTitle = findViewById(R.id.tvFullnameTitle);
            tvFullname = findViewById(R.id.tvFullname);
            tvGender = findViewById(R.id.tvGender);
            tvEmail = findViewById(R.id.tvEmail);
            tvAddress = findViewById(R.id.tvAddress);
            tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
            tvAccountType = findViewById(R.id.tvAccountType);
            tvDateofBirth = findViewById(R.id.tvDateofBirth);
            tvChangePass = findViewById(R.id.tvChangePass);
            tvLogout = findViewById(R.id.tvLogout);

            tvChangePass = findViewById(R.id.tvChangePass);
            tvChangePass.setOnClickListener(this);
            tvLogout = findViewById(R.id.tvLogout);
            tvLogout.setOnClickListener(this);

            getDataUserLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvChangePass: {
                try {
                    /*Chuyển sang màn hình đổi mật khẩu*/
                    startActivity(new Intent(UserInfo.this, ChangePassword.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.tvLogout: {
                /*Chuyển sang màn hình đăng nhập*/
                startActivity(new Intent(UserInfo.this, Login.class));
                finish();
                break;
            }
        }
    }

    /*Set dữ liệu của người dùng lên View*/
    public void getDataToView() {
        try {
            tvFullnameTitle.setText(userLoginModel.getFullName());
            tvFullname.setText(userLoginModel.getFullName());
            tvGender.setText(userLoginModel.getGender());
            tvEmail.setText(userLoginModel.getEmail());
            tvAddress.setText(userLoginModel.getAddress());
            tvDateofBirth.setText(userLoginModel.getDateOfBirth());
            tvAccountType.setText(userLoginModel.getPermission());
            tvPhoneNumber.setText(userLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu của người dùng từ database*/
    public void getDataUserLogin() {
        try {
            /*Tham chiếu đến Node Users để lấy thông tin của người dùng*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userLogin);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            userLoginModel = new UserLoginModel();
                            userLoginModel = dataSnapshot.getValue(UserLoginModel.class);
                            /*Hiển thị dữ liệu ra màn hình*/
                            getDataToView();
                        } else {
                            Toast.makeText(UserInfo.this, "Không thể lấy thông tin người dùng, vui lòng liên hệ admin", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
