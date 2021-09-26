package com.example.doancuoikycoffeeorder.View.Chef;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_PENDING;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.Chef.ChefModel;
import com.example.doancuoikycoffeeorder.Presenter.Chef.ChefAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChefViewOrder extends AppCompatActivity {
    private GridView gvTable;
    protected List<ChefModel> tableList = new ArrayList<>();
    protected List<ChefModel> drinkList = new ArrayList<>();
    DatabaseReference tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chef_view_order);
        gvTable = findViewById(R.id.gvTableList);

        getDataTableList();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*Lấy dữ liệu các bàn đang chờ từ database*/
    public void getDataTableList() {
        try {
            /*Thiết lập kết nối đến Node Orders*/
            tables = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");
            tables.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        tableList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ChefModel clsTable = ds.getValue(ChefModel.class);
                            tableList.add(clsTable);
                        }
                        /*Load danh sách các bàn chưa xử lý*/
                        LoadDrinkList();
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

    /*Load danh sách các bàn chưa xử lý*/
    public void LoadDrinkList() {
        try {
            drinkList.clear();
            assert tableList != null;
            /*Lấy danh sách bàn đang ở trạng thái PENDING*/
            for (ChefModel model : tableList) {
                if (model.getStatus().equals(STATUS_PENDING)) {
                    drinkList.add(model);
                }
            }
            /*Clear adapter cũ*/
            gvTable.setAdapter(null);
            /*Tạo Adapter mới và đổ dữ liệu vào GridView*/
            this.gvTable.setAdapter(new ChefAdapter(this, R.layout.chef_view_order_item, drinkList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Sự kiện click xem chi tiết bàn*/
    public void onClick() {
        try {
            gvTable.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    /*Chuyển từ Activity tất cả các bàn đang chờ xử lý sang chi tiết bàn khi click*/
                    Intent iChefViewDetails = new Intent(ChefViewOrder.this, ChefViewDetails.class);
                    /*Đặt 2 key ORDERS_DETAILS và POSITION với 2 giá trị lần lượt là drinkList và position để truyền dữ liệu qua Activity ChefViewDetails*/
                    iChefViewDetails.putExtra("ORDERS_DETAILS", (Serializable) drinkList);
                    iChefViewDetails.putExtra("POSITION", position);
                    startActivity(iChefViewDetails);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
