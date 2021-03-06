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

    /*T???o l???i m???t kh???u*/
    private void resetPassword() {
        try {
            String userID = spUserID.getSelectedItem().toString();
            /*L???y d??? li???u t??i kho???n t??? Node Users*/
            userMaster = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users").child(userID);
            userMaster.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*N???u database c?? d??? li???u*/
                    if (dataSnapshot.getValue() != null) {
                        /*Tham chi???u ?????n Node Users*/
                        user = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
                        /*?????t m???t kh???u m???c ?????nh ???? ???????c m?? h??a*/
                        user.child(userID).child("passWord").setValue(PASS_DEFAULT);
                        Toast.makeText(AdminResetPassword.this, "?????t l???i m???t kh???u th??nh c??ng", Toast.LENGTH_SHORT).show();
                        AdminResetPassword.this.finish();
                    } else {
                        Toast.makeText(AdminResetPassword.this, "Sai t??n t??i kho???n, vui l??ng th??? l???i!", Toast.LENGTH_LONG).show();
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

    /*L???y l???i d??? li???u t??? database m???i khi c?? s??? thay ?????i t??? database b???ng ho???t ?????ng n??o ????*/
    public void getDataFireBase() {
        try {
            /*Tham chi???u ?????n Node Users*/
            user = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Kh???i t???o l???i list User*/
                        listUser.clear();
                        /*Duy???t c??c Node con c???a Users*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*L??u c??c Node con n??y v??o Model v?? th??m v??o list*/
                            UserLoginModel user = ds.getValue(UserLoginModel.class);
                            listUser.add(user);
                        }
                        /*?????t l???i d??? li???u c???a spinner m???i khi database thay ?????i*/
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

    /*?????t l???i d??? li???u c???a Spinner*/
    public void setDataSpinner() {
        for (int i = 0; i < listUser.size(); i++) {
            listUserID.add(listUser.get(i).getUserID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUserID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserID.setAdapter(adapter);
    }
}
