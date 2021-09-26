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

    /*Cập nhật đồ uống*/
    private void upDateDrink() {
        try {
            if (Strings.isEmptyOrWhitespace(dinkNameUpdate.getText().toString())) {
                Toast.makeText(this, "Tên đồ uống không được để trống", Toast.LENGTH_SHORT).show();
            } else if (!price.getText().toString().matches("[0-9]+")) {
                Toast.makeText(this, "Sai định dạng giá tiền", Toast.LENGTH_SHORT).show();
            } else if (Strings.isEmptyOrWhitespace(price.getText().toString())) {
                Toast.makeText(this, "Giá tiền không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                reference = FirebaseDatabase.getInstance().getReference();
                /*Lấy dữ liệu từ View*/
                DrinksModel drinksModel = onGetDataFromView();
                /*Tham chiếu theo thứ tự Drinks -> DrinkID của "drinksModel" -> drinkName và update tên đồ uống*/
                reference.child(DBNAME).child("Drinks").child(String.valueOf(drinksModel.getDrinkID())).child("drinkName").setValue(drinksModel.getDrinkName());
                /*Tham chiếu theo thứ tự Drinks -> DrinkID của "drinksModel" -> price và update giá đồ uống*/
                reference.child(DBNAME).child("Drinks").child(String.valueOf(drinksModel.getDrinkID())).child("price").setValue(drinksModel.getPrice());
                /*Khi cập nhật hoàn thành, xuất Toast thông báo*/
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                AdminUpdateDrink.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lưu và trả ra dữ liệu của đồ uống vào một Model để xử lý*/
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

    /*Thay đổi dữ liệu trên View sau khi cập nhật một đồ uống hoặc Intent từ Activity khác*/
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

    /*Cập nhật lại dữ liệu vào View sau khi update*/
    public void setDataToView(DrinksModel drinksModel) {
        try {
            drinkID.setText(String.valueOf(drinksModel.getDrinkID()));
            dinkNameUpdate.setText(drinksModel.getDrinkName());
            price.setText(String.valueOf(drinksModel.getPrice()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu từ database*/
    public void getDataFireBase() {
        try {
            /*Tham chiếu tới Node Drinks*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Khởi tạo lại list đối tượng Drink để lưu*/
                        listDrink.clear();
                        /*Duyệt các Node con của Drinks*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Lưu các đối tượng và thêm vào list*/
                            DrinksModel drinks = ds.getValue(DrinksModel.class);
                            listDrink.add(drinks);
                        }
                        /*Đặt lại dữ liệu của spinner sau khi có thay đổi trong database*/
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

    /*Đặt lại dữ liệu của spinner userID khi database có thay đổi*/
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

    /*Lưu và trả về một đối tượng lưu các dữ liệu get về từ database*/
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