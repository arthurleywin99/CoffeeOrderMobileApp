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

    /*Hi???n th??ng b??o x??c nh???n x??a*/
    public void onMessageAlertInfo() {
        try {
            /*Hi???n AlertDialog x??c nh???n x??a ng?????i d??ng*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("X??c nh???n x??a");
            builder.setMessage("B???n c?? x??c nh???n mu???n x??a ng?????i d??ng n??y kh??ng?");

            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    /*Button H???y b???*/
                    case DialogInterface.BUTTON_NEGATIVE: { break; }
                    /*Button ?????ng ??*/
                    case DialogInterface.BUTTON_POSITIVE: { deleteUser(); break; }
                }
            };

            builder.setPositiveButton("?????ng ??", clickListener);
            builder.setNegativeButton("H???y b???", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*X??a ng?????i d??ng c?? t??i kho???n l?? "userID" ra kh???i database khi click button ?????ng ??*/
    public void deleteUser() {
        try {
            /*G??n userID ??ang ???????c ch???n*/
            String userID = spUserID.getSelectedItem().toString();
            /*Thi???t l???p k???t n???i t???i Node Users*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            /*X??a Node child c?? t??n l?? userID*/
            reference.child(userID).removeValue();
            /*Sau khi x??a xong update l???i database ????? spinner hi???n th???*/
            getDataFireBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y d??? li???u t??? database v?? g??n v??o list sau khi x??a m???t user ho???c Intent t??? Activity AdminConfig*/
    public void getDataFireBase() {
        try {
            /*T???o k???t n???i t???i Node Users*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Users");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*M???i khi c???p nh???t c???n x??a list ?????i t?????ng*/
                        listUser.clear();
                        /*Duy???t danh s??ch c??c Node con c???a TBM_Users ????? l???y c??c ?????i t?????ng v?? l??u v??o list*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*T???o Model ????? l??u ?????i t?????ng ???????c get t??? database*/
                            UserLoginModel user = ds.getValue(UserLoginModel.class);
                            listUser.add(user);
                        }
                        /*Set l???i chu???i hi???n th??? tr??n spinner*/
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

    /*Set l???i chu???i hi???n th??? tr??n spinner, d??? li???u l???y t??? list ?????i t?????ng ???? get v??? t??? database*/
    public void setDataSpinner() {
        /*X??a t??n t??i kho???n*/
        listUserID.clear();
        /*Duy???t list ?????i t?????ng ????? l???y userID*/
        for (UserLoginModel model : listUser) {
            if (!model.getUserID().equals("Admin"))
                listUserID.add(model.getUserID());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listUserID);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserID.setAdapter(adapter);
    }
}