package com.example.doancuoikycoffeeorder.View.Admin_UsersConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class AdminDeleteUser extends AppCompatActivity implements View.OnClickListener {
    protected Button btnDelete, btnCancel;
    private Spinner spUserID;
    protected List<UserLoginModel> listUser = new ArrayList<>();
    protected List<String> listUserID = new ArrayList<>();
    DatabaseReference reference, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_user);

        spUserID = findViewById(R.id.spUserID);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        getDataFireBase();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnDelete: {
                onMessageAlertInfo();
                break;
            }
            case R.id.btnCancel: {
                AdminDeleteUser.this.finish();
                break;
            }
        }
    }

    /*Hiện thông báo xác nhận xóa*/
    public void onMessageAlertInfo() {
        try {
            /*Hiện AlertDialog xác nhận xóa người dùng*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có xác nhận muốn xóa người dùng này không?");

            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    /*Button Hủy bỏ*/
                    case DialogInterface.BUTTON_NEGATIVE: { break; }
                    /*Button đồng ý*/
                    case DialogInterface.BUTTON_POSITIVE: { deleteUser(); break; }
                }
            };

            builder.setPositiveButton("Đồng ý", clickListener);
            builder.setNegativeButton("Hủy bỏ", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Xóa người dùng có tài khoản là "userID" ra khỏi database khi click button Đồng ý*/
    public void deleteUser() {
        try {
            /*Gán userID đang được chọn*/
            String userID = spUserID.getSelectedItem().toString();
            /*Thiết lập kết nối tới Node Users*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            /*Xóa Node child có tên là userID*/
            reference.child(userID).removeValue();
            /*Sau khi xóa xong update lại database để spinner hiển thị*/
            getDataFireBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ database và gán vào list sau khi xóa một user hoặc Intent từ Activity AdminConfig*/
    public void getDataFireBase() {
        try {
            /*Tạo kết nối tới Node Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Mỗi khi cập nhật cần xóa list đối tượng*/
                        listUser.clear();
                        /*Duyệt danh sách các Node con của TBM_Users để lấy các đối tượng và lưu vào list*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Tạo Model để lưu đối tượng được get từ database*/
                            UserLoginModel user = ds.getValue(UserLoginModel.class);
                            listUser.add(user);
                        }
                        /*Set lại chuỗi hiển thị trên spinner*/
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

    /*Set lại chuỗi hiển thị trên spinner, dữ liệu lấy từ list đối tượng đã get về từ database*/
    public void setDataSpinner() {
        /*Xóa tên tài khoản*/
        listUserID.clear();
        /*Duyệt list đối tượng để lấy userID*/
        for (UserLoginModel model : listUser) {
            if (!model.getUserID().equals("Admin"))
                listUserID.add(model.getUserID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUserID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserID.setAdapter(adapter);
    }
}