package com.example.toni.receipt.Model;

import java.io.Serializable;

/**
 * Created by toni on 3/18/17.
 */

public class HomeReceiptsModel implements Serializable{
    private String image;
    private String date;
    private String name;
    private String desc;
    private String number;
    private Double total;
    private String category;
    private String uuid;

    public HomeReceiptsModel() {
    }

    public HomeReceiptsModel(String image, String date, String name, String desc, String number, Double total, String category, String uuid) {
        this.image = image;
        this.date = date;
        this.name = name;
        this.desc = desc;
        this.number = number;
        this.total = total;
        this.category = category;
        this.uuid = uuid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
