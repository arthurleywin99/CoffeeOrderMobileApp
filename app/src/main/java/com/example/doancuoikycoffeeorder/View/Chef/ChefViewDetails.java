package com.example.doancuoikycoffeeorder.View.Chef;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_ORDER;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import com.example.doancuoikycoffeeorder.Model.Chef.ChefModel;
import com.example.doancuoikycoffeeorder.Presenter.Chef.ChefDetailsAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChefViewDetails extends AppCompatActivity implements View.OnClickListener {
    private ListView lvDrink;
    private List<ChefModel> drinkListOrder;
    private int position;
    protected TextView tvTableName;
    protected Button btnExit, btnFinish;
    DatabaseReference tableMaster, tableOrder, tableNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chef_view_details);

        try {
            lvDrink = findViewById(R.id.lvDrink);
            tvTableName = findViewById(R.id.tvTableName);

            /*Lấy dữ liệu từ Activity ChefViewOrder với key là POSITION và ORDERS_DETAILS tương ứng với giá trị "drinkList" và "position"*/
            drinkListOrder = (ArrayList<ChefModel>) getIntent().getSerializableExtra("ORDERS_DETAILS");
            position = getIntent().getIntExtra("POSITION", 0);
            tvTableName.setText(drinkListOrder.get(position).getTable());

            btnExit = findViewById(R.id.btnExit);
            btnExit.setOnClickListener(this);
            btnFinish = findViewById(R.id.btnFinish);
            btnFinish.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Hiển thị dữ liệu các thức uống của chi tiết bàn*/
        showDataToListView();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnExit: {
                ChefViewDetails.this.finish();
                break;
            }
            case R.id.btnFinish: {
                updateStatusTable();
                ChefViewDetails.this.finish();
                break;
            }
        }
    }

    /*Hiển thị danh sách các món cần làm của bàn ăn trong view chi tiết món*/
    public void showDataToListView() {
        try {
            /*Xóa dữ liệu cũ trên ListView*/
            lvDrink.setAdapter(null);
            /*Tạo Adapter*/
            ChefDetailsAdapter detailsAdapter = new ChefDetailsAdapter(this, R.layout.chef_view_details_item, drinkListOrder.get(position).getDrinksList());
            /*Gán giá trị Adapter cho listview*/
            this.lvDrink.setAdapter(detailsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Cập nhật trạng thái bàn ăn*/
    private void updateStatusTable() {
        try {
            /*Tham chiếu dữ liệu tới Node Tables để cập nhật trạng thái của bàn ăn với Node status*/
            tableMaster = FirebaseDatabase.getInstance().getReference(DBNAME).child("Tables");
            tableMaster.child(drinkListOrder.get(position).getTableID()).child("status").setValue(STATUS_ORDER);

            /*Tham chiếu dữ liệu tới Node Order để cập nhật trạng thái của đơn hàng với Node status*/
            tableOrder = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");
            tableOrder.child(drinkListOrder.get(position).getTableID()).child("status").setValue(STATUS_ORDER);

            /*Cập nhật tên bàn ăn đã hoàn thành và thông báo tới employee*/
            tableNotifications = FirebaseDatabase.getInstance().getReference(DBNAME).child("Notifications");
            tableNotifications.child("TableName").setValue(drinkListOrder.get(position).getTable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
