/*
package com.github.catvod.api;

import com.github.catvod.bean.quark.ShareData;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.Path;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuarkApi {

    private static final String API_URL = "https://drive-pc.QuarkApi.cn/1/clouddrive/";
    private String cookie;
    private String ckey;
    private Map<String, Object> shareTokenCache;
    private String pr = "pr=ucpro&fr=pc";
    private List<String> subtitleExts;
    private Map<String, String> saveFileIdCaches;
    private String saveDirId;
    private String saveDirName = "TV";
    private boolean isVip = false;

    private static class Loader {
        static volatile QuarkApi INSTANCE = new QuarkApi();
    }

    public static QuarkApi get() {
        return QuarkApi.Loader.INSTANCE;
    }

    public File getCache() {
        return Path.tv("quark");
    }

    private QuarkApi() {
        Init.checkPermission();
        this.shareTokenCache = new HashMap<>();
        this.subtitleExts = List.of(".srt", ".ass", ".scc", ".stl", ".ttml");
        this.saveFileIdCaches = new HashMap<>();
    }

    public void setRefreshToken(String token) {
        this.cookie = token;
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public JsonObject api(String url, String data, Integer retry, String method) {

        return null;

    }

    public ShareData getShareData(String url) {
        String regex = "https:\\/\\/pan\\.quark\\.cn\\/s\\/([^\\|#/]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {

            return new ShareData(matcher.group(1), "0");
        }
        return null;
    }

    boolean getVip() {
        JsonObject listData = this.api("member?pr=ucpro&fr=pc&uc_param_str=&fetch_subscribe=true&_ch=home&fetch_identity=true", null, null, "get");
        return "EXP_SVIP".equals(listData.getAsJsonObject("data").getAsJsonObject("member_type").getAsString());
    }


    List<String> getPlayFormatList() {
        if (this.isVip) {
            return List.of("4K", "超清", "高清", "普画");
        } else {
            return List.of("普画");
        }
    }


    List<String> getPlayFormtQuarkList() {
        if (this.isVip) {
            return List.of("4k", "2k", "super", "high", "normal", "low");
        }
        {
            return List.of("low");
        }
    }

    void getShareToken(ShareData shareData) {
        if (null != this.shareTokenCache.get(shareData.getShareId())) {
            this.shareTokenCache.put(shareData.getShareId(), null);
            JsonObject shareToken = this.api("share/sharepage/token?" + this.pr, new Gson().toJson(Map.of("pwd_id", shareData.getFolderId(), "passcode", Objects.isNull(shareData.getSharePwd()) ? "" : shareData.getSharePwd())), null, null);
            if (shareToken.getAsJsonObject("data") != null && StringUtils.isNoneBlank(shareToken.getAsJsonObject("data").getAsJsonObject("stoken").getAsString())) {
                this.shareTokenCache.put(shareData.getShareId(), shareToken.getAsJsonObject("data"));
            }
        }
    }


    listFile(shareIndex, shareData, videos, subtitles, shareId, folderId, page) {
    const prePage = 200;
        page = page || 1;
    const listData = await this.api(
                `share/sharepage/detail?${this.pr}&pwd_id=${shareId}&stoken=${encodeURIComponent(this.shareTokenCache[shareId].stoken)}&pdir_fid=${folderId}&force=0&_page=${page}&_size=${prePage}&_sort=file_type:asc,file_name:asc`,
        null,
                null,
                'get',
    );
        if (!listData.data) return [];
    const items = listData.data.list;
        if (!items) return [];
    const subDir = [];
        for (const item of items) {
            if (item.dir === true) {
                subDir.push(item);
            } else if (item.file === true && item.obj_category === 'video') {
                if (item.size < 1024 * 1024 * 5) continue;
                item.stoken = this.shareTokenCache[shareData.shareId].stoken;
                videos.push(Item.objectFrom(item, shareData.shareId, shareIndex));
            } else if (item.type === 'file' && this.subtitleExts.some((x) => item.file_name.endsWith(x))) {
                subtitles.push(Item.objectFrom(item, shareData, shareIndex));
            }
        }
        if (page < Math.ceil(listData.metadata._total / prePage)) {
      const nextItems = await this.listFile(
                    shareIndex,
                    shareData.shareId,
                    videos,
                    subtitles,
                    shareId,
                    folderId,
                    page + 1,
                    );
            for (const item of nextItems) {
                items.push(item);
            }
        }
        for (const dir of subDir) {
      const subItems = await this.listFile(shareIndex, shareData, videos, subtitles, shareId, dir.fid);
            for (const item of subItems) {
                items.push(item);
            }
        }
        return items;
    }
}


*/
