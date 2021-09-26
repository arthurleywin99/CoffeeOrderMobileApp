package com.example.doancuoikycoffeeorder.Model.Chef;

import java.io.Serializable;

public class ChefDetails implements Serializable {
    private String drinkName, image;
    private int nowQty, price, drinkID;

    public ChefDetails() {

    }

    public ChefDetails(String drinkName, String image, int nowQty, int price, int drinkID) {
        this.drinkName = drinkName;
        this.image = image;
        this.nowQty = nowQty;
        this.price = price;
        this.drinkID = drinkID;
    }

    public int getDrinkID() {
        return drinkID;
    }

    public void setDrinkID(int drinkID) {
        this.drinkID = drinkID;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public int getNowQty() {
        return nowQty;
    }

    public void setNowQty(int nowQty) {
        this.nowQty = nowQty;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
