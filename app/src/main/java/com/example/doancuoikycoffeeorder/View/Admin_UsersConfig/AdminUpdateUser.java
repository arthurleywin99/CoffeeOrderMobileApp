package com.example.doancuoikycoffeeorder.View.Admin_UsersConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminUpdateUser extends AppCompatActivity implements View.OnClickListener {
    protected List<UserLoginModel> listUser = new ArrayList<>();
    protected List<String> listUserID = new ArrayList<>();
    private Spinner spUserID, spPermission;
    private EditText edFullName, edDate, edPhone, edAddress, edEmail;
    private RadioButton rdMale, rdFeMale;
    protected Button btnUpdate, btnCancel;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_update_user);

        spUserID = findViewById(R.id.spUserId);
        spPermission = findViewById(R.id.spPermission);
        edFullName = findViewById(R.id.edFullName);
        edDate = findViewById(R.id.edDate);
        edDate.setOnClickListener(this);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edAddress = findViewById(R.id.edAddress);
        rdMale = findViewById(R.id.rdMale);
        rdFeMale = findViewById(R.id.rdFeMale);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        addPermission();
        getDataFireBase();
        changeDataOnView();
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
            case R.id.btnUpdate: {
                upDateUser();
                break;
            }
            case R.id.btnCancel: {
                AdminUpdateUser.this.finish();
                break;
            }
        }
    }

    /*Set dữ liệu cho spinner userID*/
    public void setDataSpinner() {
        for (UserLoginModel value : listUser) {
            listUserID.add(value.getUserID());
        }
        /*Đổ dữ liệu từ listUser vào spinner*/
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUserID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserID.setAdapter(adapter);
    }

    /*Set dữ liệu cho spinner permission*/
    public void addPermission() {
        try {
            List<String> permission = new ArrayList<>();
            permission.add("admin");
            permission.add("chef");
            permission.add("employee");
            /*Đổ dữ liệu list permission vào spinner*/
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, permission);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPermission.setAdapter(arrayAdapter);
            spPermission.setSelection(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Hiện Calender để lựa chọn ngày tháng năm*/
    private void openDateDialog() {
        try {
            Calendar calendar = Calendar.getInstance();
            int nam = calendar.get(Calendar.YEAR);
            int thang = calendar.get(Calendar.MONTH);
            int ngay = calendar.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                edDate.setText(f.format(calendar.getTime()));
            }, nam, thang, ngay);
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ Database*/
    public void getDataFireBase() {
        try {
            /*Tham chiếu đến Node Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        listUser.clear();
                        /*Duyệt các Node con của Users*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Lưu các Node con vào đối tượng userLoginModel và add vào list*/
                            UserLoginModel user = ds.getValue(UserLoginModel.class);
                            listUser.add(user);
                        }
                        /*Cập nhật dữ liệu lại cho spinner userID*/
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

    /*Model lưu dữ liệu đối tượng lấy từ realtime database*/
    public UserLoginModel getUserInfo(String userID) {
        UserLoginModel userLoginModel = new UserLoginModel();
        try {
            for (UserLoginModel model : listUser) {
                if (model.getUserID().equals(userID)) {
                    userLoginModel = model;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userLoginModel; /*Có thể có userID này hoặc cũng có thể không*/
    }

    /*Thay đổi dữ liệu trên View*/
    public void changeDataOnView() {
        try {
            spUserID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String userID = spUserID.getSelectedItem().toString();
                    UserLoginModel userLoginModel = getUserInfo(userID);
                    setDataToView(userLoginModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Cập nhật dữ liệu trên View khi database có sự thay đổi hoặc Intent từ màn hình khác*/
    public void setDataToView(UserLoginModel userLoginModel) {
        try {
            edFullName.setText(userLoginModel.getFullName());
            edDate.setText(userLoginModel.getDateOfBirth());
            edPhone.setText(userLoginModel.getPhone());
            edEmail.setText(userLoginModel.getEmail());
            edAddress.setText(userLoginModel.getAddress());
            if (userLoginModel.getPermission().equals("admin")) {
                spPermission.setSelection(0);
            } else if (userLoginModel.getPermission().equals("chef")) {
                spPermission.setSelection(1);
            } else {
                spPermission.setSelection(2);
            }

            if (userLoginModel.getGender().equals("Male")) {
                rdMale.setChecked(true);
                rdFeMale.setChecked(false);
            } else {
                rdMale.setChecked(false);
                rdFeMale.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ View để trả về một đối tượng kiểu UserLoginModel để lưu vào database*/
    public UserLoginModel onGetDataFromView() {
        UserLoginModel userLoginModel = new UserLoginModel();
        try {
            userLoginModel.setUserID(spUserID.getSelectedItem().toString());
            userLoginModel.setPermission(spPermission.getSelectedItem().toString());
            userLoginModel.setFullName(edFullName.getText().toString());
            userLoginModel.setDateOfBirth(edDate.getText().toString());
            userLoginModel.setPhone(edPhone.getText().toString());
            userLoginModel.setEmail(edEmail.getText().toString());
            userLoginModel.setAddress(edAddress.getText().toString());
            if (rdMale.isChecked())
                userLoginModel.setGender("Male");
            else
                userLoginModel.setGender("FeMale");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userLoginModel;
    }

    /*Cập nhật lại user trên database khi click vào Button Update*/
    private void upDateUser() {
        try {
            UserLoginModel userLoginModel = onGetDataFromView();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("address").setValue(userLoginModel.getAddress());
            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("dateOfBirth").setValue(userLoginModel.getDateOfBirth());
            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("fullName").setValue(userLoginModel.getFullName());
            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("gender").setValue(userLoginModel.getGender());
            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("permission").setValue(userLoginModel.getPermission());
            database.child(DBNAME).child("Users").child(userLoginModel.getUserID()).child("phone").setValue(userLoginModel.getPhone());
            database.child(DBNAME).child("Users").child(userLoginModel.getEmail()).child("email").setValue(userLoginModel.getEmail());
            AdminUpdateUser.this.finish();
            Toast.makeText(this, "Cập nhật người dùng thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}