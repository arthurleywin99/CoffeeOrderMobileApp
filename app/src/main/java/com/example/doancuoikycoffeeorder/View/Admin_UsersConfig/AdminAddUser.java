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
                        Toast.makeText(AdminAddUser.this, "T??n t??i kho???n kh??ng ???????c ????? tr???ng", Toast.LENGTH_SHORT).show();
                    } else if (Strings.isEmptyOrWhitespace(edPass.getText().toString())) {
                        Toast.makeText(AdminAddUser.this, "M???t kh???u kh??ng ???????c ????? tr???ng", Toast.LENGTH_SHORT).show();
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

    /*Hi???n Calender ????? ch???n ng??y th??ng n??m sinh*/
    private void openDateDialog() {
        try {
            Calendar calendar = Calendar.getInstance();
            int nam = calendar.get(Calendar.YEAR);
            int thang = calendar.get(Calendar.MONTH);
            int ngay = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                /*Hi???n ng??y th??ng n??m theo d???ng dd/MM/yyyy trong Edit Text sau khi ch???n tr??n Calender*/
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                edDate.setText(f.format(calendar.getTime()));
            }, nam, thang, ngay);
            datePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Th??m quy???n cho t??i kho???n t???o*/
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

    /*L???y d??? li???u t??? View tr??? v??? m???t ?????i t?????ng ki???u UserLoginModel*/
    public UserLoginModel onGetDataFromView() {
        UserLoginModel userModel = new UserLoginModel();
        try {
            userModel.setUserID(edUserID.getText().toString());
            /*L???y m???t kh???u t??? Edit Text Password v?? m?? h??a m???t kh???u*/
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

    /*Ki???m tra trong database xem username v???a t???o ???? t???n t???i hay ch??a*/
    public void checkUserExist(String userName) {
        try {
            /*Tham chi???u ?????n c??c child c???a Node Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userName);
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getValue() != null) {
                            /*B??o l???i khi h??? th???ng ???? t???n t???i username n??y*/
                            Toast.makeText(AdminAddUser.this, "H??? th???ng ???? t???n t???i username n??y. H??y th??? t??n kh??c!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            /*Th??m user v??o database n???u ch??a t???n t???i username ho???c username h???p l???*/
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

    /*Th??m User v??o database*/
    private void addNewUser() {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            /*T???o ?????i t?????ng UserLoginModel ????? get d??? li???u t??? View v?? t???o tr??n database*/
            UserLoginModel userModel = onGetDataFromView();
            reference.child(DBNAME).child("Users").child(userModel.getUserID()).setValue(userModel);
            AdminAddUser.this.finish();
            Toast.makeText(this, "T???o ng?????i d??ng th??nh c??ng", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
