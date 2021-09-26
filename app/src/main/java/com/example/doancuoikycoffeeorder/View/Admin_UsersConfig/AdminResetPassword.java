package com.example.doancuoikycoffeeorder.View.Admin_UsersConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.PASS_DEFAULT;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.Login.UserLoginModel;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminResetPassword extends AppCompatActivity implements View.OnClickListener {
    protected Button btnReset, btnCancel;
    private Spinner spUserID;
    protected List<UserLoginModel> listUser = new ArrayList<>();
    protected List<String> listUserID = new ArrayList<>();
    DatabaseReference userMaster, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_reset_password);

        spUserID = findViewById(R.id.spUserID);
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        getDataFireBase();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnReset: {
                resetPassword();
                break;
            }
            case R.id.btnCancel: {
                AdminResetPassword.this.finish();
                break;
            }
        }
    }

    /*Tạo lại mật khẩu*/
    private void resetPassword() {
        try {
            String userID = spUserID.getSelectedItem().toString();
            /*Lấy dữ liệu tài khoản từ Node Users*/
            userMaster = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userID);
            userMaster.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Nếu database có dữ liệu*/
                    if (dataSnapshot.getValue() != null) {
                        /*Tham chiếu đến Node Users*/
                        user = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
                        /*Đặt mật khẩu mặc định đã được mã hóa*/
                        user.child(userID).child("passWord").setValue(PASS_DEFAULT);
                        Toast.makeText(AdminResetPassword.this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        AdminResetPassword.this.finish();
                    } else {
                        Toast.makeText(AdminResetPassword.this, "Sai tên tài khoản, vui lòng thử lại!", Toast.LENGTH_LONG).show();
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

    /*Lấy lại dữ liệu từ database mỗi khi có sự thay đổi từ database bằng hoạt động nào đó*/
    public void getDataFireBase() {
        try {
            /*Tham chiếu đến Node Users*/
            user = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Khởi tạo lại list User*/
                        listUser.clear();
                        /*Duyệt các Node con của Users*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Lưu các Node con này vào Model và thêm vào list*/
                            UserLoginModel user = ds.getValue(UserLoginModel.class);
                            listUser.add(user);
                        }
                        /*Đặt lại dữ liệu của spinner mỗi khi database thay đổi*/
                        setDataSpinner();
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

    /*Đặt lại dữ liệu của Spinner*/
    public void setDataSpinner() {
        for (int i = 0; i < listUser.size(); i++) {
            listUserID.add(listUser.get(i).getUserID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUserID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserID.setAdapter(adapter);
    }
}
