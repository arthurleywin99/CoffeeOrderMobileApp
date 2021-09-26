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

    /*Lấy trạng thái các bàn hiện tại*/
    private void getDataTableList() {
        try {
            /*Tham chiếu đến Node Orders*/
            tables = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");

            tables.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        tableList.clear();
                        /*Duyệt các Node con của Orders*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Lưu dữ liệu các bàn từ database*/
                            ChefModel chefModel = ds.getValue(ChefModel.class);
                            tableList.add(chefModel);
                        }
                        /*Load các bàn đang ở trạng thái ORDER*/
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

    /*Lấy các hóa đơn hiện tại*/
    private void getDataInvoiceList() {
        try {
            /*Tham chiếu đến Node Invoices*/
            invoices = FirebaseDatabase.getInstance().getReference(DBNAME).child("Invoices");
            invoices.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        invoiceList.clear();
                        /*Duyệt các Node con của Orders*/
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

    /*Load dữ liệu các bàn*/
    public void LoadDrinkList() {
        try {
            /*Chỉ hiển thị những bàn đang trạng thái order*/
            drinkList.clear();
            assert tableList != null;
            for (ChefModel model : tableList) {
                if (model.getStatus().equals(STATUS_ORDER) && model.getTableID().equals(tableNameInvoice)) {
                    drinkList.add(model);
                }
            }
            /*Adapter chi tiết hóa đơn của từng bàn*/
            /*Clear dữ liệu cũ trên View*/
            lvInvoice.setAdapter(null);
            /*Khởi tạo Adapter để lưu chi tiết từng bàn*/
            InvoiceAdapter detailsAdapter = new InvoiceAdapter(this, R.layout.invoice_item, drinkList.get(0).getDrinksList());
            /*Đổ dữ liệu từ drinkList vào Listview*/
            this.lvInvoice.setAdapter(detailsAdapter);
            /*Tổng tiền của bàn*/
            this.tvTotal.setText(String.valueOf(drinkList.get(0).getTotal()) + " VNĐ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Cập nhật lại trạng thái của bàn sau khi đã thanh toán*/
    private void updateStatusTable() {
        try {
            /*Tham chiếu đến Node Tables*/
            myRef = FirebaseDatabase.getInstance().getReference(DBNAME).child("Tables");
            /*Set lại trạng thái của bàn là FREE*/
            myRef.child(tableNameInvoice).child("status").setValue(STATUS_FREE);
            /*Tham chiếu đến Node Orders*/
            myOrders = FirebaseDatabase.getInstance().getReference(DBNAME).child("Orders");
            /*Xóa tên bàn đang nằm trong hóa đơn vì đã thanh toán rồi*/
            myOrders.child(tableNameInvoice).removeValue();
            /*Lưu dữ liệu hóa đơn vào database*/
            setDataInvoice();
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            Invoice.this.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy ngày hiện tại format theo chuẩn dd/mm/yyyy*/
    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    /*Lưu dữ liệu hóa đơn lại trong database*/
    private void setDataInvoice() {
        try {
            reference = FirebaseDatabase.getInstance().getReference();
            InvoiceModel invoiceModel = new InvoiceModel(getMaxIDInvoice() + 1, tableNameInvoice, drinkList.get(0).getTable(), "invoice", getCurrentDate(), drinkList,drinkList.get(0).getTotal());
            /*Tạo Node hóa đơn mới và lưu vào Invoices sau khi đã xác nhận in bill*/
            reference.child(DBNAME).child("Invoices").child(String.valueOf(getMaxIDInvoice() + 1)).setValue(invoiceModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Hiện AlertDialog xác nhận khi nhấn in hóa đơn*/
    public void onMessageAlertInfo() {
        try {
            /*AlertDialog xác nhận*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận in hóa đơn");
            builder.setMessage("Bạn có chắc muốn in hóa đơn?");

            DialogInterface.OnClickListener clickListener = (dialogInterface, i) -> {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        /*Khi nhấn đồng ý thì hệ thống sẽ cập nhật lại tình trạng bàn là đã thanh toán*/
                        updateStatusTable();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            /*Set 2 lựa chọn cho AlertDialog*/
            builder.setPositiveButton("Đồng ý", clickListener);
            builder.setNegativeButton("Hủy", clickListener);
            builder.setIcon(R.drawable.admin_ic_delete_user);

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy ID lớn nhất hiện tại của list hóa đơn*/
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