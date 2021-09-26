package com.example.doancuoikycoffeeorder.View.OrderDetails;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.Presenter.OrderDetails.OrderDetailsAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.OrderOverView.OverView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OrderDetails extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton btnAddOrder;
    private ListView lvDrink;
    protected List<DrinksModel> drinkList = new ArrayList<>();
    private ImageView imgBanner;
    DatabaseReference drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        lvDrink = findViewById(R.id.a3_listView);
        imgBanner = findViewById(R.id.imageView3);

        btnAddOrder = findViewById(R.id.btnAddOrder);
        btnAddOrder.setOnClickListener(this);

        /*Set banner trên cho giao diện*/
        setImageBanner();
        getDataFireBase();
    }

    @Override
    public void onClick(View view) {
        try {
            int orderTotal = 0;
            /*Tính tổng tiền các thức uống đã chọn*/
            for (DrinksModel model : drinkList) {
                orderTotal += model.getNowQty() * model.getPrice();
            }
            /*Đã chọn thức uống thì tổng tiền > 0*/
            if (orderTotal > 0) {
                /*Chuyển đến Activity tổng quan các món đã đặt và tiến hành xác nhận*/
                Intent intent = new Intent(OrderDetails.this, OverView.class);
                /*Tạo key ORDERS để truyền dữ liệu qua Activity OverView*/
                intent.putExtra("ORDERS", (Serializable) drinkList);
                startActivity(intent);
            } else { /*Ngược lại chưa chọn thức uống nào*/
                Toast.makeText(this, "Vui lòng chọn một thức uống", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy dữ liệu các bàn từ database*/
    public void getDataFireBase() {
        try {
            /*Tham chiếu đến Node Drinks*/
            drinks = FirebaseDatabase.getInstance().getReference(DBNAME).child("Drinks");
            drinks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Xóa dữ liệu các bàn cũ*/
                        drinkList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Duyệt các Node con để lấy ra danh sách các bàn và lưu vào list*/
                            DrinksModel drink = ds.getValue(DrinksModel.class);
                            drinkList.add(drink);
                        }
                        /*Load các bàn đang ở trạng thái FREE và PENDING*/
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

    /*Load dữ liệu của các bàn*/
    public void LoadDrinkList() {
        try {
            /*Xóa dữ liệu cũ trên View*/
            lvDrink.setAdapter(null);

            OrderDetailsAdapter detailsAdapter = new OrderDetailsAdapter(this, R.layout.order_details_item, drinkList);
            /*Đổ dữ liệu từ drinkList thông qua detailsAdapter vào ListView*/
            this.lvDrink.setAdapter(detailsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Load banner cho giao diện đặt món - đường dẫn ảnh lấy từ database*/
    public void setImageBanner() {
        try {
            /*Tham chiếu đến Node OrderDetails để lấy đường dẫn hình ảnh*/
            drinks = FirebaseDatabase.getInstance().getReference(DBNAME).child("Banners").child("OrderDetails");
            drinks.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        /*Load ảnh theo đường dẫn lấy từ database bằng thư viện Picasso*/
                        Picasso.get().load(dataSnapshot.getValue().toString()).placeholder(R.drawable.banner1).error(R.drawable.banner1).into(imgBanner);
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
}
