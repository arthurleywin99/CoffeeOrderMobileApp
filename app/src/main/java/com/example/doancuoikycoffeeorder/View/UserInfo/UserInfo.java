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
                    /*Chuy???n sang m??n h??nh ?????i m???t kh???u*/
                    startActivity(new Intent(UserInfo.this, ChangePassword.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.tvLogout: {
                /*Chuy???n sang m??n h??nh ????ng nh???p*/
                startActivity(new Intent(UserInfo.this, Login.class));
                finish();
                break;
            }
        }
    }

    /*Set d??? li???u c???a ng?????i d??ng l??n View*/
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

    /*L???y d??? li???u c???a ng?????i d??ng t??? database*/
    public void getDataUserLogin() {
        try {
            /*Tham chi???u ?????n Node Users ????? l???y th??ng tin c???a ng?????i d??ng*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userLogin);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            userLoginModel = new UserLoginModel();
                            userLoginModel = dataSnapshot.getValue(UserLoginModel.class);
                            /*Hi???n th??? d??? li???u ra m??n h??nh*/
                            getDataToView();
                        } else {
                            Toast.makeText(UserInfo.this, "Kh??ng th??? l???y th??ng tin ng?????i d??ng, vui l??ng li??n h??? admin", Toast.LENGTH_LONG).show();
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
