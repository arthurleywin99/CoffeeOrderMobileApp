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

    /*Hiện thông báo hỏi người dùng xem có muốn xóa không*/
    public void onMessageAlertInfo() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc là muốn xóa đồ uống này không?");
            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    /*Nút Đồng ý*/
                    case DialogInterface.BUTTON_POSITIVE: { deleteDrink(); break; }
                    /*Nút hủy*/
                    case DialogInterface.BUTTON_NEGATIVE: { break;}
                }
            };
            builder.setPositiveButton("Đồng ý", clickListener);
            builder.setNegativeButton("Từ chối", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);

            Dialog dialog = builder.create();
            /*Phải chọn 1 trong 2, nhấn ra màn hình ngoài không từ chối dialog*/
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Xóa đồ uống có mã là drinkID đang được chọn*/
    public void deleteDrink() {
        try {
            String dID = drinkID.getText().toString();
            /*Lấy các Node con của Drinks*/
            reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            /*Xóa Node con có tên là dID đang được chọn*/
            reference.child(dID).removeValue();
            getDataFireBase();
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Get lại dữ liệu từ Database*/
    public void getDataFireBase() {
        try {
            /*Lấy danh sách các đồ uống lại từ database và gán vào danh sách listDrink*/
            users = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Mỗi khi get lại dữ liệu cần clear mảng*/
                        listDrink.clear();
                        /*Duyệt dữ liệu của từng Node con*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Mỗi lần duyệt, tạo một Model để lưu giá trị của ĐỐI TƯỢNG Drink và thêm vào list*/
                            DrinksModel drinks = ds.getValue(DrinksModel.class);
                            listDrink.add(drinks);
                        }
                        /*Set lại TÊN ĐỒ UỐNG cho Spinner sau khi xóa một đồ uống hoặc Intent từ Activity AdminConfig*/
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

    /*Load lại các item của spinner sau mỗi lần xóa*/
    public void setDataSpinner() {
        try {
            listDrinkName.clear();
            /*Duyệt danh sách đối tượng đã lưu trong list để lấy tên và hiển thị*/
            for (DrinksModel model : listDrink) {
                listDrinkName.add(model.getDrinkName());
            }
            /*Đổ dữ liệu từ list vào spinner qua Adapter*/
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listDrinkName);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDrinkName.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Set lại dữ liệu cho View mỗi khi database thay đổi*/
    public void changeDataOnView() {
        try {
            spDrinkName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    /*Lấy tên item đang được chọn của spinner*/
                    String drinkName = spDrinkName.getSelectedItem().toString();
                    /*Lấy thông tin đối tượng có tên "drinkName"*/
                    DrinksModel drinksModel = getDrinkInfo(drinkName);
                    /*Cập nhật mã đồ uống drinkID*/
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

    /*Tìm thông tin của đối tượng có tên là "drinkName" trong danh sách đồ uống và trả ra đối tượng đó*/
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