package com.example.doancuoikycoffeeorder.View.TableList;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_FREE;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_ORDER;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_PENDING;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.flagTableList;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableNameInvoice;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableOrder;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableNameOrder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.TableList.TableModel;
import com.example.doancuoikycoffeeorder.Presenter.TableList.Adapter;
import com.example.doancuoikycoffeeorder.R;
import com.example.doancuoikycoffeeorder.View.OrderDetails.OrderDetails;
import com.example.doancuoikycoffeeorder.View.OrderOverView.Invoice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TableList extends AppCompatActivity {
    protected List<TableModel> tableList = new ArrayList<>();
    private GridView gvTable;
    private TextView tvDes, tvTitle;
    DatabaseReference tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_list);

        gvTable = findViewById(R.id.gvTable);
        tvDes = findViewById(R.id.tvDes);
        tvTitle = findViewById(R.id.tvTitle);

        getDataTableList();
        onTouch();
    }

    /*L???y danh s??ch c??c b??n t??? database*/
    public void getDataTableList() {
        try {
            /*Tham chi???u ?????n Node Tables*/
            tables = FirebaseDatabase.getInstance().getReference(DBNAME).child("Tables");
            tables.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        tableList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Duy???t c??c Node con c???a Tables*/
                            TableModel tableModel = ds.getValue(TableModel.class);
                            tableList.add(tableModel);
                        }
                        /*Load danh s??ch c??c b??n khi ng?????i d??ng click v??o 1 trong 2 button*/
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

    /*Load c??c b??n theo 1 trong 2 Button ???????c click: ORDER || INVOICE*/
    public void LoadDrinkList() {
        try {
            /*X??a d??? li???u c?? tr??n GridView*/
            gvTable.setAdapter(null);

            /*Ki???m tra tr???ng th??i c???a b??n*/
            /*INVOICE*/
            if (flagTableList) {
                tvDes.setText(getResources().getString(R.string.Ordering));
                tvTitle.setText(getResources().getString(R.string.SelectTableToGetInvoice));
                /*Duy???t c??c b??n ????? lo???i b??? c??c b??n c?? tr???ng th??i FREE v?? PENDING b???ng Collections.removeIf*/
                tableList.removeIf(clsTable -> clsTable.getStatus().equals(STATUS_FREE) || clsTable.getStatus().equals(STATUS_PENDING));
            } else { /*ORDER*/
                tvDes.setText(getResources().getString(R.string.FreeAndPending));
                tvTitle.setText(getResources().getString(R.string.SelectTableToOrder));
                /*Duy???t c??c b??n ????? lo???i b??? c??c b??n c?? tr???ng th??i ORDER b???ng Collections.removeIf*/
                tableList.removeIf(clsTable -> clsTable.getStatus().equals(STATUS_ORDER));
            }
            /*????? gi?? tr??? Adapter chi ti???t b??n v??o GridView*/
            this.gvTable.setAdapter(new Adapter(this, R.layout.table_list_item, tableList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*S??? ki???n click tr??n t???ng b??n*/
    private void onTouch() {
        try {
            gvTable.setOnItemClickListener((parent, view, position, id) -> {
                /*Tr???ng th??i INVOICE*/
                if (flagTableList) {
                    /*Chuy???n sang Activity Invoice*/
                    tableNameInvoice = tableList.get(position).getId();
                    startActivity(new Intent(TableList.this, Invoice.class));
                } else { /*Tr???ng th??i ORDER*/
                    /*Chuy???n sang Activity OrderDetails*/
                    tableOrder = tableList.get(position).getId();
                    tableNameOrder = tableList.get(position).getName();
                    startActivity(new Intent(TableList.this, OrderDetails.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
