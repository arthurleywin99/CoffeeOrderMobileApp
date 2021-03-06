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
            /*Click v??o CardView ?????t m??n*/
            case R.id.cvOrder: {
                flagTableList = false; // order, pending
                /*Chuy???n t???i activity ch???n b??n*/
                startActivity(new Intent(MainActivity.this, TableList.class));
                break;
            }
            /*Click v??o CardView h??a ????n*/
            case R.id.cvInvoice: {
                flagTableList = true; // invoice
                /*Chuy???n t???i activity ch???n b??n*/
                startActivity(new Intent(MainActivity.this, TableList.class));
                break;
            }
            /*Click v??o CardView th??ng tin t??i kho???n*/
            case R.id.cvUserAccount: {
                /*Chuy???n t???i activity th??ng tin t??i kho???n*/
                startActivity(new Intent(MainActivity.this, UserInfo.class));
                break;
            }
            /*Click v??o CardView c?? ph??*/
            case R.id.cvSmoothie: {
                try {
                    /*Hi???n card view c???a c?? ph?? khi click v??o*/
                    OrderDialogMore orderDialogMore = new OrderDialogMore();
                    orderDialogMore.show(getSupportFragmentManager(), orderDialogMore.getTag());
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*Click v??o CardView sinh t???*/
            case R.id.cvCoffee: {
                try {
                    /*Hi???n card view c???a sinh t??? khi click v??o*/
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

    /*Thay ?????i d??? li???u t??? database*/
    public void onListeningDataChange() {
        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            /*L???y m?? b??n t??? database*/
                            String tableName = dataSnapshot.child("TableName").getValue().toString();
                            /*Th??ng b??o cho employee ???? ho??n t???t*/
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

    /*H??m th??ng b??o cho nh??n vi??n khi nh?? b???p ???? ho??n th??nh m??n t???i b??n "tableName"*/
    public void onMessageAlertInfo(String tableName) {
        try {
            /*T???o AlertDialog th??ng b??o ???? ho??n th??nh*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(tableName + STATUS_FINISH);
            builder.setMessage("???? ho??n th??nh r???i, v?? l???y ??em cho kh??ch ??i :D");

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
            /*Ph???i nh???n Button OK m???i t???t ???????c dialog*/
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
