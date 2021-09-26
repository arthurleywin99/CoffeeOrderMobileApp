package com.example.doancuoikycoffeeorder.View.Admin_UsersConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminAddUser extends AppCompatActivity implements View.OnClickListener {
    private Spinner spPermission;
    private EditText edUserID, edPass, edFullName, edDate, edPhone, edAddress, edEmail;
    protected RadioButton rdMale, rdFeMale;
    protected Button btnCreate, btnCancel;
    DatabaseReference reference, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_add_user);

        spPermission = findViewById(R.id.spPermission);
        edUserID = findViewById(R.id.edUserID);
        edPass = findViewById(R.id.edPass);
        edFullName = findViewById(R.id.edFullName);
        edDate = findViewById(R.id.edDate);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edAddress = findViewById(R.id.edAddress);
        rdMale = findViewById(R.id.rdMale);
        rdFeMale = findViewById(R.id.rdFeMale);
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        try {
            addPermission();
            edDate.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.edDate: {
                openDateDialog();
                break;
            }
            case R.id.btnCreate: {
                try {
                    if (Strings.isEmptyOrWhitespace(edUserID.getText().toString())) {
                        Toast.makeText(AdminAddUser.this, "Tên tài khoản không được để trống", Toast.LENGTH_SHORT).show();
                    } else if (Strings.isEmptyOrWhitespace(edPass.getText().toString())) {
                        Toast.makeText(AdminAddUser.this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                    } else {
                        checkUserExist(edUserID.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.btnCancel: {
                AdminAddUser.this.finish();
                break;
            }
        }
    }

    /*Hiện Calender để chọn ngày tháng năm sinh*/
    private void openDateDialog() {
        try {
            Calendar calendar = Calendar.getInstance();
            int nam = calendar.get(Calendar.YEAR);
            int thang = calendar.get(Calendar.MONTH);
            int ngay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                /*Hiện ngày tháng năm theo dạng dd/MM/yyyy trong Edit Text sau khi chọn trên Calender*/
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                edDate.setText(f.format(calendar.getTime()));
            }, nam, thang, ngay);
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Thêm quyền cho tài khoản tạo*/
    public void addPermission() {
        try {
            List<String> per = new ArrayList<>();
            per.add("admin");
            per.add("chef");
            per.add("employee");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AdminAddUser.this, android.R.layout.simple_list_item_1, per);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPermission.setAdapter(arrayAdapter);
            spPermission.setSelection(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ View trả về một đối tượng kiểu UserLoginModel*/
    public UserLoginModel onGetDataFromView() {
        UserLoginModel userModel = new UserLoginModel();
        try {
            userModel.setUserID(edUserID.getText().toString());
            /*Lấy mật khẩu từ Edit Text Password và mã hóa mật khẩu*/
            String pass = LocalVariablesAndMethods.getASCIICodeFromText(edPass.getText().toString(), 2);

            userModel.setPassWord(pass);
            userModel.setPermission(spPermission.getSelectedItem().toString());
            userModel.setFullName(edFullName.getText().toString());
            userModel.setDateOfBirth(edDate.getText().toString());
            userModel.setPhone(edPhone.getText().toString());
            userModel.setEmail(edEmail.getText().toString());
            userModel.setAddress(edAddress.getText().toString());
            if (rdMale.isChecked()) { userModel.setGender("Male"); }
            else { userModel.setGender("FeMale"); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userModel;
    }

    /*Kiểm tra trong database xem username vừa tạo đã tồn tại hay chưa*/
    public void checkUserExist(String userName) {
        try {
            /*Tham chiếu đến các child của Node Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userName);
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            /*Báo lỗi khi hệ thống đã tồn tại username này*/
                            Toast.makeText(AdminAddUser.this, "Hệ thống đã tồn tại username này. Hãy thử tên khác!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            /*Thêm user vào database nếu chưa tồn tại username hoặc username hợp lệ*/
                            addNewUser();
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

    /*Thêm User vào database*/
    private void addNewUser() {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            /*Tạo đối tượng UserLoginModel để get dữ liệu từ View và tạo trên database*/
            UserLoginModel userModel = onGetDataFromView();
            reference.child(DBNAME).child("Users").child(userModel.getUserID()).setValue(userModel);
            AdminAddUser.this.finish();
            Toast.makeText(this, "Tạo người dùng thành công", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
