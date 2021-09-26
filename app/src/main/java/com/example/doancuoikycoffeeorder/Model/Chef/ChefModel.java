package com.example.doancuoikycoffeeorder.Model.Chef;

import java.io.Serializable;
import java.util.List;

public class ChefModel implements Serializable {
    private List<ChefDetails> drinksList;
    private String status, tableID, table;
    private int total;

    public List<ChefDetails> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<ChefDetails> drinksList) {
        this.drinksList = drinksList;
    }

    public String getTableID() {
        return tableID;
    }

    public void setTableID(String tableID) {
        this.tableID = tableID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
