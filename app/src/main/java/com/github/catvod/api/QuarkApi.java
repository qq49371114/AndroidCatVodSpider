package com.github.catvod.api;

import com.github.catvod.bean.Vod;
import com.github.catvod.bean.quark.Item;
import com.github.catvod.bean.quark.ShareData;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuarkApi {
    private String apiUrl = "https://drive-pc.quark.cn/1/clouddrive/";
    private String cookie = "";
    private String ckey = "";
    private Map<String, Map<String, Object>> shareTokenCache = new HashMap<>();
    private String pr = "pr=ucpro&fr=pc";
    private List<String> subtitleExts = Arrays.asList(".srt", ".ass", ".scc", ".stl", ".ttml");
    private Map<String, String> saveFileIdCaches = new HashMap<>();
    private String saveDirId = null;
    private String saveDirName = "TV";
    private boolean isVip = false;

    public Vod getVod(ShareData shareData) throws Exception {
        getShareToken(shareData);

        return null;
    }

    private static class Loader {
        static volatile QuarkApi INSTANCE = new QuarkApi();
    }

    public static QuarkApi get() {
        return QuarkApi.Loader.INSTANCE;
    }


    public void initQuark(String cookie) throws Exception {
        this.ckey = bytesToHex(MessageDigest.getInstance("MD5").digest(cookie.getBytes()));
        this.cookie = cookie;
        this.isVip = getVip();
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch");
        headers.put("Referer", "https://pan.quark.cn/");
        headers.put("Content-Type", "application/json");
        headers.put("Cookie", this.cookie);
        headers.put("Host", "drive-pc.quark.cn");
        return headers;
    }

    private String api(String url, Map<String, String> data, Integer retry, String method) throws Exception {


        int leftRetry = retry != null ? retry : 3;
        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.apiUrl + url, data, getHeaders());
        } else {
            okResult = OkHttp.post(this.apiUrl + url, data, getHeaders());
        }


        if (okResult.getResp().get("Set-Cookie") != null) {
            Matcher matcher = Pattern.compile("__puus=([^;]+)").matcher(StringUtils.join(okResult.getResp().get("Set-Cookie"), ";;;"));
            if (matcher.find()) {
                Matcher cookieMatcher = Pattern.compile("__puus=([^;]+)").matcher(this.cookie);
                if (cookieMatcher.find() && !cookieMatcher.group(1).equals(matcher.group(1))) {
                    this.cookie = this.cookie.replaceAll("__puus=[^;]+", "__puus=" + matcher.group(1));
                }
            }
        }

        if (okResult.getCode() != 200 && leftRetry > 0) {
            Thread.sleep(1000);
            return api(url, data, leftRetry - 1, method);
        }
        return okResult.getBody();
    }

    public ShareData getShareData(String url) {
        Pattern pattern = Pattern.compile("https://pan\\.quark\\.cn/s/([^\\\\|#/]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return new ShareData(matcher.group(1), "0");
        }
        return null;
    }

    private boolean getVip() throws Exception {
        Map<String, Object> listData = Json.parseSafe(api("member?pr=ucpro&fr=pc&uc_param_str=&fetch_subscribe=true&_ch=home&fetch_identity=true", null, 0, "GET"), Map.class);
        return "EXP_SVIP".equals(((Map<String, String>) listData.get("data")).get("member_type"));
    }

    private List<String> getPlayFormatList() {
        if (this.isVip) {
            return Arrays.asList("4K", "超清", "高清", "普画");
        } else {
            return Collections.singletonList("普画");
        }
    }

    private List<String> getPlayFormatQuarkList() {
        if (this.isVip) {
            return Arrays.asList("4k", "2k", "super", "high", "normal", "low");
        } else {
            return Collections.singletonList("low");
        }
    }

    private void getShareToken(ShareData shareData) throws Exception {
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) {
            this.shareTokenCache.remove(shareData.getShareId());
            Map<String, Object> shareToken = Json.parseSafe(api("share/sharepage/token?" + this.pr, Map.of("pwd_id", shareData.getShareId(), "passcode", shareData.getSharePwd()), 0, "POST"), Map.class);
            if (shareToken.containsKey("data") && ((Map<String, Object>) shareToken.get("data")).containsKey("stoken")) {
                this.shareTokenCache.put(shareData.getShareId(), (Map<String, Object>) shareToken.get("data"));
            }
        }
    }

    private List<Map<String, Object>> listFile(int shareIndex, ShareData shareData, List<Item> videos, List<Item> subtitles, String shareId, String folderId, Integer page) throws Exception {
        int prePage = 200;
        page = page != null ? page : 1;
        Map<String, Object> listData = Json.parseSafe(api("share/sharepage/detail?" + this.pr + "&pwd_id=" + shareId + "&stoken=" + encodeURIComponent((String) this.shareTokenCache.get(shareId).get("stoken")) + "&pdir_fid=" + folderId + "&force=0&_page=" + page + "&_size=" + prePage + "&_sort=file_type:asc,file_name:asc", null, 0, "GET"), Map.class);
        if (listData.get("data") == null) return Collections.emptyList();
        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list");
        if (items == null) return Collections.emptyList();
        List<Map<String, Object>> subDir = new ArrayList<>();
        for (Map<String, Object> item : items) {
            if (Boolean.TRUE.equals(item.get("dir"))) {
                subDir.add(item);
            } else if (Boolean.TRUE.equals(item.get("file")) && "video".equals(item.get("obj_category"))) {
                if ((int) item.get("size") < 1024 * 1024 * 5) continue;
                item.put("stoken", this.shareTokenCache.get(shareData.getShareId()).get("stoken"));
                videos.add(Item.objectFrom(Json.toJson(item), shareData.getShareId(), shareIndex));
            } else if ("file".equals(item.get("type")) && this.subtitleExts.contains("." + Util.getExt((String) item.get("file_name")))) {
                subtitles.add(Item.objectFrom(Json.toJson(item), shareData.getShareId(), shareIndex));
            }
        }
        if (page < Math.ceil((double) ((Map<String, Object>) listData.get("metadata")).get("_total") / prePage)) {
            List<Map<String, Object>> nextItems = listFile(shareIndex, shareData, videos, subtitles, shareId, folderId, page + 1);
            items.addAll(nextItems);
        }
        for (Map<String, Object> dir : subDir) {
            List<Map<String, Object>> subItems = listFile(shareIndex, shareData, videos, subtitles, shareId, dir.get("fid").toString(), null);
            items.addAll(subItems);
        }
        return items;
    }

    private Map<String, Object> findBestLCS(Item mainItem, List<Item> targetItems) {
        List<Map<String, Object>> results = new ArrayList<>();
        int bestMatchIndex = 0;
        for (int i = 0; i < targetItems.size(); i++) {
            Util.LCSResult currentLCS = Util.lcs(mainItem.getName(), targetItems.get(i).getName());
            Map<String, Object> result = new HashMap<>();
            result.put("target", targetItems.get(i));
            result.put("lcs", currentLCS);
            results.add(result);
            if (currentLCS.length > results.get(bestMatchIndex).get("lcs").toString().length()) {
                bestMatchIndex = i;
            }
        }
        Map<String, Object> bestMatch = results.get(bestMatchIndex);
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("allLCS", results);
        finalResult.put("bestMatch", bestMatch);
        finalResult.put("bestMatchIndex", bestMatchIndex);
        return finalResult;
    }

    public void getFilesByShareUrl(int shareIndex, String shareInfo, List<Item> videos, List<Item> subtitles) throws Exception {
        ShareData shareData = getShareData((String) shareInfo);
        if (shareData == null) return;
        getShareToken(shareData);
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) return;
        listFile(shareIndex, shareData, videos, subtitles, shareData.getShareId(), shareData.getFolderId(), 1);
        if (!subtitles.isEmpty()) {
            for (Item video : videos) {
                Map<String, Object> matchSubtitle = findBestLCS(video, subtitles);
                if (matchSubtitle.get("bestMatch") != null) {
                    video.setSubtitle((String) ((Map<String, Object>) matchSubtitle.get("bestMatch")).get("target"));
                }
            }
        }
    }

    private void clean() {
        saveFileIdCaches.clear();
    }

    private void clearSaveDir() throws Exception {
        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=" + this.saveDirId + "&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null && ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")).size() > 0) {
            List<String> list = new ArrayList<>();
            for (Map<String, Object> stringStringMap : ((List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list"))) {
                list.add((String) stringStringMap.get("fid"));
            }
            api("file/delete?" + this.pr,


                    Map.of("action_type", "2", "filelist", Json.toJson(list), "exclude_fids", ""), 0, "POST");
        }
    }

    private void createSaveDir(boolean clean) throws Exception {
        if (this.saveDirId != null) {
            if (clean) clearSaveDir();
            return;
        }
        Map<String, Object> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=0&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), 0, "GET"), Map.class);
        if (listData.get("data") != null) {
            for (Map<String, Object> item : (List<Map<String, Object>>) ((Map<String, Object>) listData.get("data")).get("list")) {
                if (this.saveDirName.equals(item.get("file_name"))) {
                    this.saveDirId = item.get("fid").toString();
                    clearSaveDir();
                    break;
                }
            }
        }
        if (this.saveDirId == null) {
            Map<String, Object> create = Json.parseSafe(api("file?" + this.pr, Map.of("pdir_fid", "0", "file_name", this.saveDirName, "dir_path", "", "dir_init_lock", "false"), 0, "POST"), Map.class);
            if (create.get("data") != null && ((Map<String, Object>) create.get("data")).get("fid") != null) {
                this.saveDirId = ((Map<String, Object>) create.get("data")).get("fid").toString();
            }
        }
    }

    private String save(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        createSaveDir(clean);
        if (clean) {
            clean();
        }
        if (this.saveDirId == null) return null;
        if (stoken == null) {
            getShareToken(new ShareData(shareId, null));
            if (!this.shareTokenCache.containsKey(shareId)) return null;
        }
        Map<String, Object> saveResult = Json.parseSafe(api("share/sharepage/save?" + this.pr, Map.of("fid_list", fileId, "fid_token_list", fileToken, "to_pdir_fid", this.saveDirId, "pwd_id", shareId, "stoken", stoken != null ? stoken : (String) this.shareTokenCache.get(shareId).get("stoken"), "pdir_fid", "0", "scene", "link"), 0, "POST"), Map.class);
        if (saveResult.get("data") != null && ((Map<String, Object>) saveResult.get("data")).get("task_id") != null) {
            int retry = 0;
            while (true) {
                Map<String, Object> taskResult = Json.parseSafe(api("task?" + this.pr + "&task_id=" + ((Map<String, Object>) saveResult.get("data")).get("task_id") + "&retry_index=" + retry, Collections.emptyMap(), 0, "GET"), Map.class);
                if (taskResult.get("data") != null && ((Map<String, Object>) taskResult.get("data")).get("save_as") != null && ((Map<String, Object>) ((Map<String, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids") != null && ((List<String>) ((Map<String, Object>) ((Map<String, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).size() > 0) {
                    return ((List<String>) ((Map<String, Object>) ((Map<String, Object>) taskResult.get("data")).get("save_as")).get("save_as_top_fids")).get(0);
                }
                retry++;
                if (retry > 2) break;
                Thread.sleep(1000);
            }
        }
        return null;
    }

    private String getLiveTranscoding(String shareId, String stoken, String fileId, String fileToken, String flag) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, true);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }
        Map<String, Object> transcoding = Json.parseSafe(api("file/v2/play?" + this.pr, Map.of("fid", this.saveFileIdCaches.get(fileId), "resolutions", "normal,low,high,super,2k,4k", "supports", "fmp4"), 0, "POST"), Map.class);
        if (transcoding.get("data") != null && ((Map<String, Object>) transcoding.get("data")).get("video_list") != null) {
            String flagId = flag.split("-")[flag.split("-").length - 1];
            int index = Util.findAllIndexes(getPlayFormatList(), flagId);
            String quarkFormat = getPlayFormatQuarkList().get(index);
            for (Map<String, Object> video : (List<Map<String, Object>>) ((Map<String, Object>) transcoding.get("data")).get("video_list")) {
                if (video.get("resolution").equals(quarkFormat)) {
                    return video.get("video_info").toString();
                }
            }
            return ((List<Map<String, Object>>) ((Map<String, Object>) transcoding.get("data")).get("video_list")).get(index).get("video_info").toString();
        }
        return null;
    }

    private String getDownload(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, clean);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }
        Map<String, Object> down = Json.parseSafe(api("file/download?" + this.pr + "&uc_param_str=", Map.of("fids", this.saveFileIdCaches.get(fileId)), 0, "POST"), Map.class);
        if (down.get("data") != null) {
            return ((List<String>) down.get("data")).get(0);
        }
        return null;
    }

    // Helper method to convert bytes to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Encoding helper method
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }


}

