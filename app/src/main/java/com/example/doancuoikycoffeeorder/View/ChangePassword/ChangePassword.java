package com.example.doancuoikycoffeeorder.View.ChangePassword;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
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
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private EditText edOldPass, edNewPass, edRepeatPass;
    private ProgressBar progressBar;
    private UserLoginModel userLoginModel;
    protected Button btnChange;
    DatabaseReference users, changePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        edOldPass = findViewById(R.id.edOldPass);
        edNewPass = findViewById(R.id.edNewPass);
        edRepeatPass = findViewById(R.id.edRepeat);
        progressBar = findViewById(R.id.progressBar);
        btnChange = findViewById(R.id.btnChange);
        btnChange.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            /*Kiểm tra password có hợp lệ không*/
            if (Strings.isEmptyOrWhitespace(edOldPass.getText().toString())) {
                Toast.makeText(ChangePassword.this, "Mật khẩu cũ không được để trống", Toast.LENGTH_SHORT).show();
                edOldPass.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (Strings.isEmptyOrWhitespace(edNewPass.getText().toString())) {
                Toast.makeText(ChangePassword.this, "Mật khẩu mới không được để trống", Toast.LENGTH_SHORT).show();
                edNewPass.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            } else if (!edNewPass.getText().toString().equals(edRepeatPass.getText().toString())) {
                Toast.makeText(ChangePassword.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                edRepeatPass.requestFocus();
                progressBar.setVisibility(View.GONE);
                return;
            }

            /*Kiểm tra dữ liệu trong database*/
            /*Tham chiếu đến Node con của Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(LocalVariablesAndMethods.userLogin);
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        /*Nếu trong database đang có dữ liệu*/
                        if (dataSnapshot.getValue() != null) {
                            userLoginModel = new UserLoginModel();
                            /*Lưu dữ liệu vào Model*/
                            userLoginModel = dataSnapshot.getValue(UserLoginModel.class);
                            assert userLoginModel != null;
                            /*Giải mã passowrd và lưu lại*/
                            String oldPasswordDB = LocalVariablesAndMethods.getTextFromASCIICode(userLoginModel.getPassWord(), 2);
                            /*Kiểm tra password cũ đang nhập và password trên database*/
                            if (oldPasswordDB != null && oldPasswordDB.equals(edOldPass.getText().toString())) {
                                /*Tham chiếu đến Node Users*/
                                changePassword = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
                                /*Mã hóa mật khẩu*/
                                String newPassMD5 = LocalVariablesAndMethods.getASCIICodeFromText(edNewPass.getText().toString(), 2);
                                /*Set lại mật khẩu mới trên database*/
                                changePassword.child(LocalVariablesAndMethods.userLogin).child("passWord").setValue(newPassMD5);
                                /*Thông báo thay đổi thành công*/
                                Toast.makeText(ChangePassword.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                finish();
                            } else {
                                Toast.makeText(ChangePassword.this, "Mật khẩu cũ không đúng!", Toast.LENGTH_LONG).show();
                                edOldPass.requestFocus();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            /*Database đang trống*/
                            Toast.makeText(ChangePassword.this, "Người dùng chưa đăng ký. Liên hệ với admin", Toast.LENGTH_LONG).show();
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
