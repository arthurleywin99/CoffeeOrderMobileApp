package com.example.doancuoikycoffeeorder.View.OrderOverView;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_PENDING;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.getDecimalFormattedString;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableNameOrder;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableOrder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.Model.OrderDetails.OrderDetailsModel;
import com.example.doancuoikycoffeeorder.Presenter.OderOverView.OverViewAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OverView extends AppCompatActivity implements View.OnClickListener {
    protected Button btnOrderNow;
    private ListView lvOrderDetails;
    protected List<DrinksModel> drinkListOrder;
    private TextView tvOrderTotal;
    private int orderTotal = 0;
    DatabaseReference reference, myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.over_view);

        try {
            btnOrderNow = findViewById(R.id.btnOrderNow);
            btnOrderNow.setOnClickListener(this);
            lvOrderDetails = findViewById(R.id.lvOrderDetails);
            tvOrderTotal = findViewById(R.id.tvOrderTotal);
            /*Lấy dữ liệu drinkList từ Activity OrderDetails thông qua key ORDERS*/
            ArrayList<DrinksModel> drinkList = (ArrayList<DrinksModel>) getIntent().getSerializableExtra("ORDERS");
            drinkListOrder = new ArrayList<>();

            assert drinkList != null;
            for (DrinksModel model : drinkList) {
                if (model.getNowQty() > 0) {
                    drinkListOrder.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showDataToListView();
    }
    @Override
    public void onClick(View view) {
        try {
            setDataOrder();
            updateStatusTable();
            /*Chuyển sang Activity đặt hoàn thành khi nhấn vào nút OrderNow*/
            startActivity(new Intent(OverView.this, OrderComplete.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*ListView chứa chi tiết các món đã đặt*/
    public void showDataToListView() {
        try {
            /*Xóa dữ liệu cũ trên View*/
            lvOrderDetails.setAdapter(null);
            /*Tạo Adapter chi tiết các món đã đặt*/
            OverViewAdapter detailsAdapter = new OverViewAdapter(this, R.layout.over_view_item, drinkListOrder);
            /*Đổ giá trị của drinkListOrder vào ListView*/
            this.lvOrderDetails.setAdapter(detailsAdapter);
            /*Tính tổng tiền*/
            for (DrinksModel model : drinkListOrder) {
                orderTotal += model.getNowQty() * model.getPrice();
            }
            tvOrderTotal.setText(getDecimalFormattedString(String.valueOf(orderTotal)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Set dữ liệu đã Order vào database*/
    private void setDataOrder() {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            /*Thêm child cho bàn đã order thành công vào Node cha là Orders*/
            OrderDetailsModel orderDetailsModel = new OrderDetailsModel(tableOrder, tableNameOrder, "pending", drinkListOrder, orderTotal);
            reference.child(DBNAME).child("Orders").child(tableOrder).setValue(orderDetailsModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Set lại trạng thái của bàn*/
    private void updateStatusTable() {
        try {
            /*Sau khi đặt thành công, set lại trạng thái của bàn trong Node Tables*/
            myRef = FirebaseDatabase.getInstance().getReference(DBNAME).child("Tables");
            myRef.child(tableOrder).child("status").setValue(STATUS_PENDING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
