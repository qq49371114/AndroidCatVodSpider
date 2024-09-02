package com.github.catvod.bean.quark;

import com.github.catvod.utils.Util;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.regex.Pattern;

public class Item {
    @SerializedName("fid")
    private String fid;
    @SerializedName("file_name")
    private String fileName;
    @SerializedName("pdir_fid")
    private String pdirFid;
    private int category;
    private int fileType;
    private long size;
    @SerializedName("format_type")
    private String formatType;
    private int status;
    private String tags;
    @SerializedName("owner_ucid")
    private String ownerUcid;
    @SerializedName("l_created_at")
    private long lCreatedAt;
    @SerializedName("l_updated_at")
    private long lUpdatedAt;
    private String extra;
    @SerializedName("source")
    private String source;
    @SerializedName("file_source")
    private String fileSource;
    private int nameSpace;
    @SerializedName("l_shot_at")
    private long lShotAt;
    @SerializedName("series_id")
    private String seriesId;
    private String thumbnail;
    private String bigThumbnail;
    @SerializedName("preview_url")
    private String previewUrl;
    @SerializedName("video_max_resolution")
    private String videoMaxResolution;
    @SerializedName("source_display")
    private String sourceDisplay;
    private int videoWidth;
    private boolean seriesDir;
    private boolean uploadCameraRootDir;
    private int videoHeight;
    private int videoRotate;
    private double fps;
    private int like;
    @SerializedName("operated_at")
    private long operatedAt;
    private int riskType;
    private int backupSign;
    @SerializedName("obj_category")
    private String objCategory;
    @SerializedName("file_name_hl")
    private String fileNameHl;
    @SerializedName("file_name_hl_start")
    private int fileNameHlStart;
    @SerializedName("file_name_hl_end")
    private int fileNameHlEnd;

    private int duration;

    private Map<String, Object> eventExtra;
    private int scrapeStatus;

    @SerializedName("update_view_at")
    private long updateViewAt;
    @SerializedName("last_update_at")
    private long lastUpdateAt;
    @SerializedName("share_fid_token")
    private String shareFidToken;
    private boolean ban;
    @SerializedName("save_as_source")
    private boolean saveAsSource;
    private int curVersionOrDefault;
    private int rawNameSpace;
    private boolean backupSource;
    private boolean offlineSource;
    @SerializedName("owner_drive_type_or_default")
    private int ownerDriveTypeOrDefault;
    private boolean dir;
    private boolean file;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updatedAt;
    private String shareId;
    private String shareToken;
    private String subtitle;

    public Item() {
    }

    public static Item objectFrom(Item item, String shareId, int shareIndex) {
        item.setShareId(shareId);
        item.setShareIndex(shareIndex);
        return item;
    }

    public String getShareToken() {
        return shareToken;
    }

    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }

    private int shareIndex;

    public int getShareIndex() {
        return shareIndex;
    }

    public void setShareIndex(int shareIndex) {
        this.shareIndex = shareIndex;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
/*  public Item() {
        this.fileId = "";
        this.shareId = "";
        this.shareToken = "";
        this.shareFileToken = "";
        this.seriesId = "";
        this.name = "";
        this.type = "";
        this.formatType = "";
        this.size = 0d;
        this.parent = "";
        this.shareData = null;
        this.shareIndex = 0;
        this.lastUpdateAt = 0d;
    }

    public static Item objectFrom(Map<String, Object> item_json, String shareId, int shareIndex) {
        Item item = new Item();
        item.fileId = item_json.get("fid") != null ? (String) item_json.get("fid") : "";
        item.shareId = shareId;
        item.shareToken = item_json.get("stoken") != null ? (String) item_json.get("stoken") : "";
        item.shareFileToken = item_json.get("share_fid_token") != null ? (String) item_json.get("share_fid_token") : "";
        item.seriesId = item_json.get("series_id") != null ? (String) item_json.get("series_id") : "";
        item.name = item_json.get("file_name") != null ? (String) item_json.get("file_name") : "";
        item.type = item_json.get("obj_category") != null ? (String) item_json.get("obj_category") : "";
        item.formatType = item_json.get("format_type") != null ? (String) item_json.get("format_type") : "";
        item.size = item_json.get("size") != null ? (Double) item_json.get("size") : 0d;
        item.parent = item_json.get("pdir_fid") != null ? (String) item_json.get("pdir_fid") : "";
        item.lastUpdateAt = item_json.get("last_update_at") != null ? (Double) item_json.get("last_update_at") : Double.valueOf(0d);
        item.shareIndex = shareIndex;
        return item;
    }*/

    public String getFileExtension() {
        String[] arr = fileName.split("\\.");
        return arr[arr.length - 1];
    }

/*    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
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
    }*/

    public String getDisplayName(String type_name) {
        String name = getFileName();
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
        return name + " " + Util.getSize(size);
    }

    public String getEpisodeUrl(String type_name) {
        return getDisplayName(type_name) + "$" + getFid() + "++" + getShareFidToken() + "++" + getShareId() + "++" + getShareToken();
    }


    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPdirFid() {
        return pdirFid;
    }

    public void setPdirFid(String pdirFid) {
        this.pdirFid = pdirFid;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getOwnerUcid() {
        return ownerUcid;
    }

    public void setOwnerUcid(String ownerUcid) {
        this.ownerUcid = ownerUcid;
    }

    public long getlCreatedAt() {
        return lCreatedAt;
    }

    public void setlCreatedAt(long lCreatedAt) {
        this.lCreatedAt = lCreatedAt;
    }

    public long getlUpdatedAt() {
        return lUpdatedAt;
    }

    public void setlUpdatedAt(long lUpdatedAt) {
        this.lUpdatedAt = lUpdatedAt;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFileSource() {
        return fileSource;
    }

    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

    public int getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(int nameSpace) {
        this.nameSpace = nameSpace;
    }

    public long getlShotAt() {
        return lShotAt;
    }

    public void setlShotAt(long lShotAt) {
        this.lShotAt = lShotAt;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBigThumbnail() {
        return bigThumbnail;
    }

    public void setBigThumbnail(String bigThumbnail) {
        this.bigThumbnail = bigThumbnail;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getVideoMaxResolution() {
        return videoMaxResolution;
    }

    public void setVideoMaxResolution(String videoMaxResolution) {
        this.videoMaxResolution = videoMaxResolution;
    }

    public String getSourceDisplay() {
        return sourceDisplay;
    }

    public void setSourceDisplay(String sourceDisplay) {
        this.sourceDisplay = sourceDisplay;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public boolean isSeriesDir() {
        return seriesDir;
    }

    public void setSeriesDir(boolean seriesDir) {
        this.seriesDir = seriesDir;
    }

    public boolean isUploadCameraRootDir() {
        return uploadCameraRootDir;
    }

    public void setUploadCameraRootDir(boolean uploadCameraRootDir) {
        this.uploadCameraRootDir = uploadCameraRootDir;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoRotate() {
        return videoRotate;
    }

    public void setVideoRotate(int videoRotate) {
        this.videoRotate = videoRotate;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public long getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(long operatedAt) {
        this.operatedAt = operatedAt;
    }

    public int getRiskType() {
        return riskType;
    }

    public void setRiskType(int riskType) {
        this.riskType = riskType;
    }

    public int getBackupSign() {
        return backupSign;
    }

    public void setBackupSign(int backupSign) {
        this.backupSign = backupSign;
    }

    public String getObjCategory() {
        return objCategory;
    }

    public void setObjCategory(String objCategory) {
        this.objCategory = objCategory;
    }

    public String getFileNameHl() {
        return fileNameHl;
    }

    public void setFileNameHl(String fileNameHl) {
        this.fileNameHl = fileNameHl;
    }

    public int getFileNameHlStart() {
        return fileNameHlStart;
    }

    public void setFileNameHlStart(int fileNameHlStart) {
        this.fileNameHlStart = fileNameHlStart;
    }

    public int getFileNameHlEnd() {
        return fileNameHlEnd;
    }

    public void setFileNameHlEnd(int fileNameHlEnd) {
        this.fileNameHlEnd = fileNameHlEnd;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Map<String, Object> getEventExtra() {
        return eventExtra;
    }

    public void setEventExtra(Map<String, Object> eventExtra) {
        this.eventExtra = eventExtra;
    }

    public int getScrapeStatus() {
        return scrapeStatus;
    }

    public void setScrapeStatus(int scrapeStatus) {
        this.scrapeStatus = scrapeStatus;
    }

    public long getUpdateViewAt() {
        return updateViewAt;
    }

    public void setUpdateViewAt(long updateViewAt) {
        this.updateViewAt = updateViewAt;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getShareFidToken() {
        return shareFidToken;
    }

    public void setShareFidToken(String shareFidToken) {
        this.shareFidToken = shareFidToken;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public boolean isSaveAsSource() {
        return saveAsSource;
    }

    public void setSaveAsSource(boolean saveAsSource) {
        this.saveAsSource = saveAsSource;
    }

    public int getCurVersionOrDefault() {
        return curVersionOrDefault;
    }

    public void setCurVersionOrDefault(int curVersionOrDefault) {
        this.curVersionOrDefault = curVersionOrDefault;
    }

    public int getRawNameSpace() {
        return rawNameSpace;
    }

    public void setRawNameSpace(int rawNameSpace) {
        this.rawNameSpace = rawNameSpace;
    }

    public boolean isBackupSource() {
        return backupSource;
    }

    public void setBackupSource(boolean backupSource) {
        this.backupSource = backupSource;
    }

    public boolean isOfflineSource() {
        return offlineSource;
    }

    public void setOfflineSource(boolean offlineSource) {
        this.offlineSource = offlineSource;
    }

    public int getOwnerDriveTypeOrDefault() {
        return ownerDriveTypeOrDefault;
    }

    public void setOwnerDriveTypeOrDefault(int ownerDriveTypeOrDefault) {
        this.ownerDriveTypeOrDefault = ownerDriveTypeOrDefault;
    }

    public boolean isDir() {
        return dir;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

