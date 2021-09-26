package com.example.doancuoikycoffeeorder.Model.OrderOverView;

import com.example.doancuoikycoffeeorder.Model.Chef.ChefModel;

import java.util.List;

public class InvoiceModel {
    private int invoiceID;
    private String tableID, table, status, date;
    private List<ChefModel> drinksList;
    private int total;

    public InvoiceModel() {
    }

    public InvoiceModel(int invoiceID, String tableID, String table, String status, String date, List<ChefModel> drinksList, int total) {
        this.invoiceID = invoiceID;
        this.tableID = tableID;
        this.table = table;
        this.status = status;
        this.date = date;
        this.drinksList = drinksList;
        this.total = total;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getTableID() {
        return tableID;
    }

    public void setTableID(String tableID) {
        this.tableID = tableID;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ChefModel> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<ChefModel> drinksList) {
        this.drinksList = drinksList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
