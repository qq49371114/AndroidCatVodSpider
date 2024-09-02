package com.github.catvod.bean.quark;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class Metadata {
    @SerializedName("_size")
    private int size;
    @SerializedName("_page")
    private int page;
    @SerializedName("_count")
    private int count;
    @SerializedName("_total")
    private int total;
    @SerializedName("check_fid_token")
    private int checkFidToken;
    @SerializedName("_g_group")
    private String gGroup;
    @SerializedName("_t_group")
    private String tGroup;

    // Getters and Setters

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCheckFidToken() {
        return checkFidToken;
    }

    public void setCheckFidToken(int checkFidToken) {
        this.checkFidToken = checkFidToken;
    }

    public String getGGroup() {
        return gGroup;
    }

    public void setGGroup(String gGroup) {
        this.gGroup = gGroup;
    }

    public String getTGroup() {
        return tGroup;
    }

    public void setTGroup(String tGroup) {
        this.tGroup = tGroup;
    }
}
