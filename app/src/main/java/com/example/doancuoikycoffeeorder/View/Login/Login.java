package com.example.doancuoikycoffeeorder.View.Login;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.PER_ADMIN;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.PER_CHEF;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.getTextFromASCIICode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.LocalVariablesAndMethods;
import com.example.doancuoikycoffeeorder.Model.Login.UserLoginModel;
import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.AdminMenu.AdminMenu;
import com.example.doancuoikycoffeeorder.View.Chef.ChefViewOrder;
import com.example.doancuoikycoffeeorder.View.Main.MainActivity;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText edLoginUserName, edLoginPassword;
    private ProgressBar progressBar;
    private UserLoginModel userLoginModel;
    protected Button btnLogin;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_password);

        edLoginUserName = findViewById(R.id.edLoginUserName);
        edLoginPassword = findViewById(R.id.edLoginPassword);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            /*Kiểm tra hợp lệ*/
            if (Strings.isEmptyOrWhitespace(edLoginUserName.getText().toString().trim())) {
                edLoginUserName.setError(("Tên tài khoản không được để trống"));
                edLoginUserName.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (Strings.isEmptyOrWhitespace(edLoginPassword.getText().toString().trim())) {
                edLoginPassword.setError(("Mật khẩu không được để trống"));
                edLoginPassword.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }

            /*Kiểm tra username - password trong CSDL firebase*/
            /*Tham chiếu đến Node Users và lấy tất cả các child có trong Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(edLoginUserName.getText().toString().trim());
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        /*Kiểm tra xem Users có child nào không*/
                        if (dataSnapshot.getValue() != null) {
                            userLoginModel = new UserLoginModel();
                            userLoginModel = dataSnapshot.getValue(UserLoginModel.class);

                            assert userLoginModel != null;
                            /*Giải mã mật khẩu*/
                            String pass = getTextFromASCIICode(userLoginModel.getPassWord(),2);

                            assert pass != null;
                            /*Kiểm tra pass có trùng khớp không*/
                            if (pass.equals(edLoginPassword.getText().toString().trim())) {
                                LocalVariablesAndMethods.userLogin = edLoginUserName.getText().toString().trim();

                                /*Kiểm tra Permission của tài khoản*/
                                if (userLoginModel.getPermission().equals(PER_ADMIN)) {
                                    /*Nếu là ADMIN thì chuyển sang layout chức năng của ADMIN*/
                                    startActivity(new Intent(Login.this, AdminMenu.class));
                                } else if (userLoginModel.getPermission().equals(PER_CHEF)) {
                                    /*Nếu là CHEF thì chuyển sang layout chức năng của CHEF*/
                                    startActivity(new Intent(Login.this, ChefViewOrder.class));
                                } else {
                                    /*Trường hợp còn lại chuyển sang layout chức năng của EMPLOYEE*/
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                }

                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                /*Nếu không trùng mật khẩu thì hiện Toast báo lỗi*/
                                Toast.makeText(Login.this, "Sai mật khẩu", Toast.LENGTH_LONG).show();
                                edLoginPassword.requestFocus();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            /*Nếu chưa có child nào thì hiện Toast báo chưa có ai đăng ký tài khoản hết*/
                            Toast.makeText(Login.this, "Chưa đăng ký tài khoản nào trong hệ thống. Liên hệ với admin", Toast.LENGTH_LONG).show();
                            edLoginUserName.requestFocus();
                            progressBar.setVisibility(View.GONE);
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
