package com.example.doancuoikycoffeeorder.View.Main;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_FINISH;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.flagTableList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.OrderDetails.OrderDialog;
import com.example.doancuoikycoffeeorder.View.OrderDetails.OrderDialogMore;
import com.example.doancuoikycoffeeorder.View.TableList.TableList;
import com.example.doancuoikycoffeeorder.View.UserInfo.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView cvOrder, cvInvoice, cvUserAccount, cvSmoothie, cvCoffee;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reference = FirebaseDatabase.getInstance().getReference(DBNAME).child("Notifications");

        cvOrder = findViewById(R.id.cvOrder);
        cvOrder.setOnClickListener(this);
        cvInvoice = findViewById(R.id.cvInvoice);
        cvInvoice.setOnClickListener(this);
        cvUserAccount = findViewById(R.id.cvUserAccount);
        cvUserAccount.setOnClickListener(this);
        cvCoffee = findViewById(R.id.cvCoffee);
        cvCoffee.setOnClickListener(this);
        cvSmoothie = findViewById(R.id.cvSmoothie);
        cvSmoothie.setOnClickListener(this);

        onListeningDataChange();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            /*Click vào CardView đặt món*/
            case R.id.cvOrder: {
                flagTableList = false; // order, pending
                /*Chuyển tới activity chọn bàn*/
                startActivity(new Intent(MainActivity.this, TableList.class));
                break;
            }
            /*Click vào CardView hóa đơn*/
            case R.id.cvInvoice: {
                flagTableList = true; // invoice
                /*Chuyển tới activity chọn bàn*/
                startActivity(new Intent(MainActivity.this, TableList.class));
                break;
            }
            /*Click vào CardView thông tin tài khoản*/
            case R.id.cvUserAccount: {
                /*Chuyển tới activity thông tin tài khoản*/
                startActivity(new Intent(MainActivity.this, UserInfo.class));
                break;
            }
            /*Click vào CardView cà phê*/
            case R.id.cvSmoothie: {
                try {
                    /*Hiện card view của cà phê khi click vào*/
                    OrderDialogMore orderDialogMore = new OrderDialogMore();
                    orderDialogMore.show(getSupportFragmentManager(), orderDialogMore.getTag());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*Click vào CardView sinh tố*/
            case R.id.cvCoffee: {
                try {
                    /*Hiện card view của sinh tố khi click vào*/
                    OrderDialog orderDialog = new OrderDialog();
                    orderDialog.show(getSupportFragmentManager(), orderDialog.getTag());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /*Thay đổi dữ liệu từ database*/
    public void onListeningDataChange() {
        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            /*Lấy mã bàn từ database*/
                            String tableName = dataSnapshot.child("TableName").getValue().toString();
                            /*Thông báo cho employee đã hoàn tất*/
                            onMessageAlertInfo(tableName);
                            reference.child("TableName").removeValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    /*Hàm thông báo cho nhân viên khi nhà bếp đã hoàn thành món tại bàn "tableName"*/
    public void onMessageAlertInfo(String tableName) {
        try {
            /*Tạo AlertDialog thông báo đã hoàn thành*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(tableName + STATUS_FINISH);
            builder.setMessage("Đã hoàn thành rồi, vô lấy đem cho khách đi :D");

            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE: break;
                    }
                }
            };

            builder.setPositiveButton("OK", clickListener);
            builder.setIcon(R.drawable.ic_check);

            Dialog dialog = builder.create();
            /*Phải nhấn Button OK mới tắt được dialog*/
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
