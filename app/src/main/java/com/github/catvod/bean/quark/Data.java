package com.github.catvod.bean.quark;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("is_owner")
    private int isOwner;
    @SerializedName("list")
    private List<Item> list;

    // Getters and Setters

    public int getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }
}