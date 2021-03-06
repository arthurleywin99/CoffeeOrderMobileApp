package com.example.doancuoikycoffeeorder.View.OrderOverView;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_FREE;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_ORDER;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.getDecimalFormattedString;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.tableNameInvoice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.Model.Chef.ChefModel;
import com.example.doancuoikycoffeeorder.Model.OrderOverView.InvoiceModel;
import com.example.doancuoikycoffeeorder.Presenter.OderOverView.InvoiceAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Invoice extends AppCompatActivity implements View.OnClickListener {
    protected List<ChefModel> tableList = new ArrayList<>();
    protected List<ChefModel> drinkList = new ArrayList<>();
    protected List<InvoiceModel> invoiceList = new ArrayList<>();

    protected Button btnPayNow;
    private ListView lvInvoice;
    private TextView tvTotal;
    DatabaseReference tables, reference, myRef, myOrders, invoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);

        btnPayNow = findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(this);

        lvInvoice = findViewById(R.id.lvInvoice);
        tvTotal = findViewById(R.id.tvTotal);

        getDataTableList();
    }

    @Override
    public void onClick(View view) {
        try {
            getDataInvoiceList();
            onMessageAlertInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y tr???ng th??i c??c b??n hi???n t???i*/
    private void getDataTableList() {
        try {
            /*Tham chi???u ?????n Node Orders*/
            tables = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");

            tables.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        tableList.clear();
                        /*Duy???t c??c Node con c???a Orders*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*L??u d??? li???u c??c b??n t??? database*/
                            ChefModel chefModel = ds.getValue(ChefModel.class);
                            tableList.add(chefModel);
                        }
                        /*Load c??c b??n ??ang ??? tr???ng th??i ORDER*/
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

    /*L???y c??c h??a ????n hi???n t???i*/
    private void getDataInvoiceList() {
        try {
            /*Tham chi???u ?????n Node Invoices*/
            invoices = FirebaseDatabase.getInstance().getReference(DBNAME).child("Invoices");
            invoices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        invoiceList.clear();
                        /*Duy???t c??c Node con c???a Orders*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            InvoiceModel invoiceModel = ds.getValue(InvoiceModel.class);
                            invoiceList.add(invoiceModel);
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

    /*Load d??? li???u c??c b??n*/
    public void LoadDrinkList() {
        try {
            /*Ch??? hi???n th??? nh???ng b??n ??ang tr???ng th??i order*/
            drinkList.clear();
            assert tableList != null;
            for (ChefModel model : tableList) {
                if (model.getStatus().equals(STATUS_ORDER) && model.getTableID().equals(tableNameInvoice)) {
                    drinkList.add(model);
                }
            }
            /*Adapter chi ti???t h??a ????n c???a t???ng b??n*/
            /*Clear d??? li???u c?? tr??n View*/
            lvInvoice.setAdapter(null);
            /*Kh???i t???o Adapter ????? l??u chi ti???t t???ng b??n*/
            InvoiceAdapter detailsAdapter = new InvoiceAdapter(this, R.layout.invoice_item, drinkList.get(0).getDrinksList());
            /*????? d??? li???u t??? drinkList v??o Listview*/
            this.lvInvoice.setAdapter(detailsAdapter);
            /*T???ng ti???n c???a b??n*/
            this.tvTotal.setText(String.valueOf(drinkList.get(0).getTotal()) + " VN??");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*C???p nh???t l???i tr???ng th??i c???a b??n sau khi ???? thanh to??n*/
    private void updateStatusTable() {
        try {
            /*Tham chi???u ?????n Node Tables*/
            myRef = FirebaseDatabase.getInstance().getReference(DBNAME).child("Tables");
            /*Set l???i tr???ng th??i c???a b??n l?? FREE*/
            myRef.child(tableNameInvoice).child("status").setValue(STATUS_FREE);
            /*Tham chi???u ?????n Node Orders*/
            myOrders = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");
            /*X??a t??n b??n ??ang n???m trong h??a ????n v?? ???? thanh to??n r???i*/
            myOrders.child(tableNameInvoice).removeValue();
            /*L??u d??? li???u h??a ????n v??o database*/
            setDataInvoice();
            Toast.makeText(this, "Thanh to??n th??nh c??ng", Toast.LENGTH_SHORT).show();
            Invoice.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y ng??y hi???n t???i format theo chu???n dd/mm/yyyy*/
    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    /*L??u d??? li???u h??a ????n l???i trong database*/
    private void setDataInvoice() {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            InvoiceModel invoiceModel = new InvoiceModel(getMaxIDInvoice() + 1, tableNameInvoice, drinkList.get(0).getTable(), "invoice", getCurrentDate(), drinkList,drinkList.get(0).getTotal());
            /*T???o Node h??a ????n m???i v?? l??u v??o Invoices sau khi ???? x??c nh???n in bill*/
            reference.child(DBNAME).child("Invoices").child(String.valueOf(getMaxIDInvoice() + 1)).setValue(invoiceModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Hi???n AlertDialog x??c nh???n khi nh???n in h??a ????n*/
    public void onMessageAlertInfo() {
        try {
            /*AlertDialog x??c nh???n*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("X??c nh???n in h??a ????n");
            builder.setMessage("B???n c?? ch???c mu???n in h??a ????n?");

            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        /*Khi nh???n ?????ng ?? th?? h??? th???ng s??? c???p nh???t l???i t??nh tr???ng b??n l?? ???? thanh to??n*/
                        updateStatusTable();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            /*Set 2 l???a ch???n cho AlertDialog*/
            builder.setPositiveButton("?????ng ??", clickListener);
            builder.setNegativeButton("H???y", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*L???y ID l???n nh???t hi???n t???i c???a list h??a ????n*/
    public int getMaxIDInvoice() {
        int invoiceID = 1;
        try {
            for (InvoiceModel model : invoiceList) {
                if (model.getInvoiceID() >= invoiceID) {
                    invoiceID = model.getInvoiceID();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoiceID;
    }
}