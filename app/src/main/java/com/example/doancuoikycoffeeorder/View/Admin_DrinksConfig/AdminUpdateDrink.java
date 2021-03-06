package com.example.doancuoikycoffeeorder.View.Admin_DrinksConfig;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.R;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUpdateDrink extends AppCompatActivity implements View.OnClickListener {
    Button btnUpdate, btnCancel;
    private EditText drinkID, dinkNameUpdate, price;
    private Spinner spDrinkName;
    protected List<DrinksModel> listDrink = new ArrayList<>();
    protected List<String> listDrinkName = new ArrayList<>();
    DatabaseReference reference, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_update_drink);

        drinkID = findViewById(R.id.edDrinkID);
        dinkNameUpdate = findViewById(R.id.edName);
        price = findViewById(R.id.edPrice);
        spDrinkName = findViewById(R.id.spDrinkName);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        getDataFireBase();
        changeDataOnView();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUpdate: {
                upDateDrink();
                break;
            }
            case R.id.btnCancel: {
                AdminUpdateDrink.this.finish();
                break;
            }
        }
    }

    /*C???p nh???t ????? u???ng*/
    private void upDateDrink() {
        try {
            if (Strings.isEmptyOrWhitespace(dinkNameUpdate.getText().toString())) {
                Toast.makeText(this, "T??n ????? u???ng kh??ng ???????c ????? tr???ng", Toast.LENGTH_SHORT).show();
            } else if (!price.getText().toString().matches("[0-9]+")) {
                Toast.makeText(this, "Sai ?????nh d???ng gi?? ti???n", Toast.LENGTH_SHORT).show();
            } else if (Strings.isEmptyOrWhitespace(price.getText().toString())) {
                Toast.makeText(this, "Gi?? ti???n kh??ng ???????c ????? tr???ng", Toast.LENGTH_SHORT).show();
            } else {
                reference = FirebaseDatabase.getInstance().getReference();
                /*L???y d??? li???u t??? View*/
                DrinksModel drinksModel = onGetDataFromView();
                /*Tham chi???u theo th??? t??? Drinks -> DrinkID c???a "drinksModel" -> drinkName v?? update t??n ????? u???ng*/
                reference.child(DBNAME).child("Drinks").child(String.valueOf(drinksModel.getDrinkID())).child("drinkName").setValue(drinksModel.getDrinkName());
                /*Tham chi???u theo th??? t??? Drinks -> DrinkID c???a "drinksModel" -> price v?? update gi?? ????? u???ng*/
                reference.child(DBNAME).child("Drinks").child(String.valueOf(drinksModel.getDrinkID())).child("price").setValue(drinksModel.getPrice());
                /*Khi c???p nh???t ho??n th??nh, xu???t Toast th??ng b??o*/
                Toast.makeText(this, "C???p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                AdminUpdateDrink.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L??u v?? tr??? ra d??? li???u c???a ????? u???ng v??o m???t Model ????? x??? l??*/
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

    /*Thay ?????i d??? li???u tr??n View sau khi c???p nh???t m???t ????? u???ng ho???c Intent t??? Activity kh??c*/
    public void changeDataOnView() {
        try {
            spDrinkName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String drinkName = spDrinkName.getSelectedItem().toString();
                    DrinksModel drinksModel = getDrinkInfo(drinkName);
                    setDataToView(drinksModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*C???p nh???t l???i d??? li???u v??o View sau khi update*/
    public void setDataToView(DrinksModel drinksModel) {
        try {
            drinkID.setText(String.valueOf(drinksModel.getDrinkID()));
            dinkNameUpdate.setText(drinksModel.getDrinkName());
            price.setText(String.valueOf(drinksModel.getPrice()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y d??? li???u t??? database*/
    public void getDataFireBase() {
        try {
            /*Tham chi???u t???i Node Drinks*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Kh???i t???o l???i list ?????i t?????ng Drink ????? l??u*/
                        listDrink.clear();
                        /*Duy???t c??c Node con c???a Drinks*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*L??u c??c ?????i t?????ng v?? th??m v??o list*/
                            DrinksModel drinks = ds.getValue(DrinksModel.class);
                            listDrink.add(drinks);
                        }
                        /*?????t l???i d??? li???u c???a spinner sau khi c?? thay ?????i trong database*/
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

    /*?????t l???i d??? li???u c???a spinner userID khi database c?? thay ?????i*/
    public void setDataSpinner() {
        try {
            for (DrinksModel value : listDrink) {
                listDrinkName.add(value.getDrinkName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDrinkName);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDrinkName.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L??u v?? tr??? v??? m???t ?????i t?????ng l??u c??c d??? li???u get v??? t??? database*/
    public DrinksModel onGetDataFromView() {
        DrinksModel drinksModel = new DrinksModel();
        try {
            drinksModel.setDrinkID(Integer.parseInt(drinkID.getText().toString()));
            drinksModel.setDrinkName(dinkNameUpdate.getText().toString());
            drinksModel.setPrice(Integer.parseInt(price.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drinksModel;
    }
}