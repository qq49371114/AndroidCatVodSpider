package com.github.catvod.bean.quark;

import java.util.regex.Pattern;

public class Item {
    private String fileId;
    private String shareId;
    private String shareToken;
    private String shareFileToken;
    private String seriesId;
    private String name;
    private String type;
    private String formatType;
    private String size;
    private String parent;
    private String shareData;
    private int shareIndex;
    private long lastUpdateAt;

    public Item() {
        this.fileId = "";
        this.shareId = "";
        this.shareToken = "";
        this.shareFileToken = "";
        this.seriesId = "";
        this.name = "";
        this.type = "";
        this.formatType = "";
        this.size = "";
        this.parent = "";
        this.shareData = null;
        this.shareIndex = 0;
        this.lastUpdateAt = 0;
    }

    public static Item objectFrom(String item_json, String shareId, int shareIndex) {
        Item item = new Item();
        item.fileId = item_json.contains("fid") ? item_json.split("fid=")[1].split("&")[0] : "";
        item.shareId = shareId;
        item.shareToken = item_json.contains("stoken") ? item_json.split("stoken=")[1].split("&")[0] : "";
        item.shareFileToken = item_json.contains("share_fid_token") ? item_json.split("share_fid_token=")[1].split("&")[0] : "";
        item.seriesId = item_json.contains("series_id") ? item_json.split("series_id=")[1].split("&")[0] : "";
        item.name = item_json.contains("file_name") ? item_json.split("file_name=")[1].split("&")[0] : "";
        item.type = item_json.contains("obj_category") ? item_json.split("obj_category=")[1].split("&")[0] : "";
        item.formatType = item_json.contains("format_type") ? item_json.split("format_type=")[1].split("&")[0] : "";
        item.size = item_json.contains("size") ? item_json.split("size=")[1].split("&")[0] : "";
        item.parent = item_json.contains("pdir_fid") ? item_json.split("pdir_fid=")[1].split("&")[0] : "";
        item.lastUpdateAt = item_json.contains("last_update_at") ? Long.parseLong(item_json.split("last_update_at=")[1].split("&")[0]) : 0;
        item.shareIndex = shareIndex;
        return item;
    }

    public String getFileExtension() {
        String[] arr = name.split("\\.");
        return arr[arr.length - 1];
    }

    public String getFileId() {
        return fileId.isEmpty() ? "" : fileId;
    }

    public String getName() {
        return name.isEmpty() ? "" : name;
    }

    public String getParent() {
        return parent.isEmpty() ? "" : "[" + parent + "]";
    }

    public String getSize() {
        return size.equals("0") ? "" : "[" + size + "]";
    }


    public int getShareIndex() {
        return shareIndex;
    }

    public String getDisplayName(String type_name) {
        String name = getName();
        if (type_name.equals("电视剧")) {
            String[] replaceNameList = {"4k", "4K"};
            name = name.replaceAll("\\." + getFileExtension(), "");
            for (String replaceName : replaceNameList) {
                name = name.replaceAll(replaceName, "");
            }
            name = Pattern.compile("/\\.S01E(.*?)\\./").matcher(name).find() ? name.split("/\\.S01E(.*?)\\./")[1] : name;
            String[] numbers = name.split("\\d+");
            if (numbers.length > 0) {
                name = numbers[0];
            }
        }
        return name + " " + size;
    }

    public String getEpisodeUrl(String type_name) {
        return getDisplayName(type_name) + "$" + getFileId() + "++" + shareFileToken + "++" + shareId + "++" + shareToken;
    }
}

