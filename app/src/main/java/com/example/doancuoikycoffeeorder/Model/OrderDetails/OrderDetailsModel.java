package com.example.doancuoikycoffeeorder.Model.OrderDetails;

import java.util.List;

public class OrderDetailsModel {
    private String tableID, table, status;
    private List<DrinksModel> drinksList;
    private int total;

    public OrderDetailsModel(String tableID, String table, String status, List<DrinksModel> drinksList, int total) {
        this.tableID = tableID;
        this.table = table;
        this.status = status;
        this.drinksList = drinksList;
        this.total = total;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DrinksModel> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<DrinksModel> drinksList) {
        this.drinksList = drinksList;
    }
}
