package com.example.doancuoikycoffeeorder.View.Admin_Report;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.DBNAME;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doancuoikycoffeeorder.LocalVariablesAndMethods;
import com.example.doancuoikycoffeeorder.Model.OrderOverView.InvoiceModel;
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

public class AdminReport extends AppCompatActivity {
    private Spinner spinner;
    protected List<InvoiceModel> tableList = new ArrayList<>();
    private TextView tvTotal, tvAmount;
    FirebaseDatabase database;
    DatabaseReference tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_report);
        spinner = findViewById(R.id.spinner);

        tvTotal = findViewById(R.id.tvTotal);
        tvAmount = findViewById(R.id.tvAmount);

        getDataTableList();
        addTypeReport();
        changeDataOnView();
    }

    /*Tạo spinner các loại report*/
    public void addTypeReport() {
        try {
            List<String> per = new ArrayList<>();
            per.add("Hôm nay");
            per.add("Tháng này");
            per.add("Tất cả");
            /*Tạo ArrayAdapter để đổ dữ liệu list Permission vào Spinner*/
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AdminReport.this, android.R.layout.simple_list_item_1, per);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            /*Mặc định chọn "Hằng ngày"*/
            spinner.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Lấy ngày hiện tại format theo chuẩn dd/mm/yyyy*/
    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    /*Lấy dữ liệu từ các hóa đơn*/
    public void getDataTableList() {
        try {
            database = FirebaseDatabase.getInstance();
            tables = database.getReference(DBNAME).child("Invoices");

            tables.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*Nếu database có hóa đơn*/
                    if (dataSnapshot.getValue() != null) {
                        tableList.clear();
                        /*Duyệt các Node con của Invoices*/
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            /*Lưu các đối tượng hóa đơn vào InvoiceModel*/
                            InvoiceModel invoiceModel = ds.getValue(InvoiceModel.class);
                            tableList.add(invoiceModel);
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

    /*Thay đổi dữ liệu trên View*/
    public void changeDataOnView() {
        try {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String itemSelect = spinner.getSelectedItem().toString();
                    setDataToView(itemSelect);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Cập nhật dữ liệu trên View khi database có sự thay đổi hoặc Intent từ màn hình khác*/
    public void setDataToView(String item) {
        if (item.equals("Hôm nay")) {
            List<InvoiceModel> todayList = new ArrayList<>();
            for (InvoiceModel model : tableList){
                if (model.getDate().equals(getCurrentDate())) {
                    todayList.add(model);
                }
            }

            if (todayList.size() == 0) {
                tvTotal.setText("0");
                tvAmount.setText("0");
            } else {
                int amount = 0;
                tvTotal.setText(String.valueOf(todayList.size()));

                for (InvoiceModel model : todayList){
                    amount += model.getTotal();
                }
                /*Set tổng tiền và hiển thị trên TextView Amount*/
                tvAmount.setText(LocalVariablesAndMethods.getDecimalFormattedString(String.valueOf(amount)));
            }
        } else if (item.equals("Tháng này")) {
            /*Lấy tháng hiện tại*/
            List<InvoiceModel> thisMonthList = new ArrayList<>();
            for (InvoiceModel model : tableList){
                if (model.getDate().split("/")[1].equals(getCurrentDate().split("/")[1]) &&
                        model.getDate().split("/")[2].equals(getCurrentDate().split("/")[2])) {
                    thisMonthList.add(model);
                }
            }

            if (thisMonthList.size() == 0) {
                tvTotal.setText("0");
                tvAmount.setText("0");
            } else {
                int amount = 0;
                tvTotal.setText(String.valueOf(thisMonthList.size()));

                for (InvoiceModel model : thisMonthList){
                    amount += model.getTotal();
                }
                /*Set tổng tiền và hiển thị trên TextView Amount*/
                tvAmount.setText(LocalVariablesAndMethods.getDecimalFormattedString(String.valueOf(amount)));
            }

        } else if (item.equals("Tất cả")) {
            try {
                if (tableList.size() == 0) {
                    tvTotal.setText("0");
                    tvAmount.setText("0");
                } else {
                    int amount = 0;
                    tvTotal.setText(String.valueOf(tableList.size()));
                    for (InvoiceModel model : tableList){
                        amount += model.getTotal();
                    }
                    /*Set tổng tiền và hiển thị trên TextView Amount*/
                    tvAmount.setText(LocalVariablesAndMethods.getDecimalFormattedString(String.valueOf(amount)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}