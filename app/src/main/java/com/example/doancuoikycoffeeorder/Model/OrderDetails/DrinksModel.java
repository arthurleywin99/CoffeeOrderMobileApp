package com.example.doancuoikycoffeeorder.Model.OrderDetails;

import java.io.Serializable;

public class DrinksModel implements Serializable {
    private String drinkName, image;
    private int nowQty, drinkID;
    private int price;

    public DrinksModel() {
    }

    public DrinksModel(String drinkName, String image, int nowQty, int drinkID, int price) {
        this.drinkName = drinkName;
        this.image = image;
        this.nowQty = nowQty;
        this.drinkID = drinkID;
        this.price = price;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNowQty() {
        return nowQty;
    }

    public void setNowQty(int nowQty) {
        this.nowQty = nowQty;
    }

    public int getDrinkID() {
        return drinkID;
    }

    public void setDrinkID(int drinkID) {
        this.drinkID = drinkID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
