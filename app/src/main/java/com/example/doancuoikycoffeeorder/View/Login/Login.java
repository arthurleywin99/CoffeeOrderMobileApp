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

            /*Ki???m tra h???p l???*/
            if (Strings.isEmptyOrWhitespace(edLoginUserName.getText().toString().trim())) {
                edLoginUserName.setError(("T??n t??i kho???n kh??ng ???????c ????? tr???ng"));
                edLoginUserName.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (Strings.isEmptyOrWhitespace(edLoginPassword.getText().toString().trim())) {
                edLoginPassword.setError(("M???t kh???u kh??ng ???????c ????? tr???ng"));
                edLoginPassword.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }

            /*Ki???m tra username - password trong CSDL firebase*/
            /*Tham chi???u ?????n Node Users v?? l???y t???t c??? c??c child c?? trong Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(edLoginUserName.getText().toString().trim());
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        /*Ki???m tra xem Users c?? child n??o kh??ng*/
                        if (dataSnapshot.getValue() != null) {
                            userLoginModel = new UserLoginModel();
                            userLoginModel = dataSnapshot.getValue(UserLoginModel.class);

                            assert userLoginModel != null;
                            /*Gi???i m?? m???t kh???u*/
                            String pass = getTextFromASCIICode(userLoginModel.getPassWord(),2);

                            assert pass != null;
                            /*Ki???m tra pass c?? tr??ng kh???p kh??ng*/
                            if (pass.equals(edLoginPassword.getText().toString().trim())) {
                                LocalVariablesAndMethods.userLogin = edLoginUserName.getText().toString().trim();

                                /*Ki???m tra Permission c???a t??i kho???n*/
                                if (userLoginModel.getPermission().equals(PER_ADMIN)) {
                                    /*N???u l?? ADMIN th?? chuy???n sang layout ch???c n??ng c???a ADMIN*/
                                    startActivity(new Intent(Login.this, AdminMenu.class));
                                } else if (userLoginModel.getPermission().equals(PER_CHEF)) {
                                    /*N???u l?? CHEF th?? chuy???n sang layout ch???c n??ng c???a CHEF*/
                                    startActivity(new Intent(Login.this, ChefViewOrder.class));
                                } else {
                                    /*Tr?????ng h???p c??n l???i chuy???n sang layout ch???c n??ng c???a EMPLOYEE*/
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                }

                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                /*N???u kh??ng tr??ng m???t kh???u th?? hi???n Toast b??o l???i*/
                                Toast.makeText(Login.this, "Sai m???t kh???u", Toast.LENGTH_LONG).show();
                                edLoginPassword.requestFocus();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            /*N???u ch??a c?? child n??o th?? hi???n Toast b??o ch??a c?? ai ????ng k?? t??i kho???n h???t*/
                            Toast.makeText(Login.this, "Ch??a ????ng k?? t??i kho???n n??o trong h??? th???ng. Li??n h??? v???i admin", Toast.LENGTH_LONG).show();
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
