package com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDeleteDrink extends AppCompatActivity implements View.OnClickListener {
    private Spinner spDrinkName;
    private TextView drinkID;
    Button btnDelete, btnCancel;
    protected List<DrinksModel> listDrink = new ArrayList<>();
    protected List<String> listDrinkName = new ArrayList<>();
    DatabaseReference reference, users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_drink);

        spDrinkName = findViewById(R.id.spDrinkName);
        drinkID = findViewById(R.id.drinkID);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        getDataFireBase();
        changeDataOnView();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDelete: {
                onMessageAlertInfo();
                break;
            }
            case R.id.btnCancel: {
                AdminDeleteDrink.this.finish();
                break;
            }
        }
    }

    /*Hi???n th??ng b??o h???i ng?????i d??ng xem c?? mu???n x??a kh??ng*/
    public void onMessageAlertInfo() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("X??c nh???n x??a");
            builder.setMessage("B???n c?? ch???c l?? mu???n x??a ????? u???ng n??y kh??ng?");
            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    /*N??t ?????ng ??*/
                    case DialogInterface.BUTTON_POSITIVE: { deleteDrink(); break; }
                    /*N??t h???y*/
                    case DialogInterface.BUTTON_NEGATIVE: { break;}
                }
            };
            builder.setPositiveButton("?????ng ??", clickListener);
            builder.setNegativeButton("T??? ch???i", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);

            Dialog dialog = builder.create();
            /*Ph???i ch???n 1 trong 2, nh???n ra m??n h??nh ngo??i kh??ng t??? ch???i dialog*/
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*X??a ????? u???ng c?? m?? l?? drinkID ??ang ???????c ch???n*/
    public void deleteDrink() {
        try {
            String dID = drinkID.getText().toString();
            /*L???y c??c Node con c???a Drinks*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            /*X??a Node con c?? t??n l?? dID ??ang ???????c ch???n*/
            reference.child(dID).removeValue();
            getDataFireBase();
            Toast.makeText(this, "X??a th??nh c??ng", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Get l???i d??? li???u t??? Database*/
    public void getDataFireBase() {
        try {
            /*L???y danh s??ch c??c ????? u???ng l???i t??? database v?? g??n v??o danh s??ch listDrink*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*M???i khi get l???i d??? li???u c???n clear m???ng*/
                        listDrink.clear();
                        /*Duy???t d??? li???u c???a t???ng Node con*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*M???i l???n duy???t, t???o m???t Model ????? l??u gi?? tr??? c???a ?????I T?????NG Drink v?? th??m v??o list*/
                            DrinksModel drinks = ds.getValue(DrinksModel.class);
                            listDrink.add(drinks);
                        }
                        /*Set l???i T??N ????? U???NG cho Spinner sau khi x??a m???t ????? u???ng ho???c Intent t??? Activity AdminConfig*/
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

    /*Load l???i c??c item c???a spinner sau m???i l???n x??a*/
    public void setDataSpinner() {
        try {
            listDrinkName.clear();
            /*Duy???t danh s??ch ?????i t?????ng ???? l??u trong list ????? l???y t??n v?? hi???n th???*/
            for (DrinksModel model : listDrink) {
                listDrinkName.add(model.getDrinkName());
            }
            /*????? d??? li???u t??? list v??o spinner qua Adapter*/
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDrinkName);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDrinkName.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Set l???i d??? li???u cho View m???i khi database thay ?????i*/
    public void changeDataOnView() {
        try {
            spDrinkName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    /*L???y t??n item ??ang ???????c ch???n c???a spinner*/
                    String drinkName = spDrinkName.getSelectedItem().toString();
                    /*L???y th??ng tin ?????i t?????ng c?? t??n "drinkName"*/
                    DrinksModel drinksModel = getDrinkInfo(drinkName);
                    /*C???p nh???t m?? ????? u???ng drinkID*/
                    try {
                        drinkID.setText(String.valueOf(drinksModel.getDrinkID()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*T??m th??ng tin c???a ?????i t?????ng c?? t??n l?? "drinkName" trong danh s??ch ????? u???ng v?? tr??? ra ?????i t?????ng ????*/
    public DrinksModel getDrinkInfo(String drinkName) {
        DrinksModel drinksModel = new DrinksModel();
        try {
            for (DrinksModel model : listDrink) {
                if (model.getDrinkName().equals(drinkName)) {
                    drinksModel = model;
                    return drinksModel;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drinksModel;
    }
}