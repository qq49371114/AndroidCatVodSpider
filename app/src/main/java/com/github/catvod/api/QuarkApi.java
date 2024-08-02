/*
package com.github.catvod.api;

import com.github.catvod.bean.quark.Item;
import com.github.catvod.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuarkApi {

    private static final String API_URL = "https://drive-pc.QuarkApi.cn/1/clouddrive/";
    private String cookie;
    private String ckey;
    private Map<String, String> shareTokenCache;
    private String pr = "pr=ucpro&fr=pc";
    private List<String> subtitleExts;
    private Map<String, String> saveFileIdCaches;
    private String saveDirId;
    private String saveDirName = "TV";

    public QuarkApi() {
        this.shareTokenCache = new HashMap<>();
        this.subtitleExts = List.of(".srt", ".ass", ".scc", ".stl", ".ttml");
        this.saveFileIdCaches = new HashMap<>();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void initQuark(String cookie) {

        this.ckey = bytesToHex(Util.MD5(cookie).getBytes());
        String localCfg = local.get("quark", "cookie");
        if (!localCfg.isEmpty()) {
            Map<String, String> cookieMap = new HashMap<>();
            JSONObject localCookie = new JSONObject(localCfg);
            cookieMap.put(this.ckey, localCookie.getString(this.ckey));
            this.cookie = cookieMap.get(this.ckey);
        } else {
            this.cookie = cookie;
        }
    }

    public Map<String, String> getHeaders() {
        return Map.of("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch", "Referer", "https://pan.QuarkApi.cn/", "Content-Type", "application/json", "Cookie", this.cookie, "Host", "drive-pc.QuarkApi.cn");
    }

    public JSONObject api(String url, String data, int retry, String method) throws ExecutionException, InterruptedException {
        int leftRetry = retry != null ? retry : 3;
        JSONObject resp = req(API_URL + url, method != null ? method : "post", data, getHeaders());
        if (resp.headers().get("set-cookie") != null) {
            String[] puus = resp.headers().get("set-cookie").split(";;;").stream().map(String::trim).filter(s -> s.startsWith("__puus=")).map(s -> s.substring(s.indexOf('=') + 1)).collect(Collectors.toList()).toArray(new String[0]);
            if (puus.length > 0 && !this.cookie.matches("__puus=[^;]+") || !this.cookie.replace("__puus=[^;]+", "__puus=" + puus[0]).equals(this.cookie)) {
                this.cookie = this.cookie.replace("__puus=[^;]+", "__puus=" + puus[0]);
                Map<String, String> cookieDic = new HashMap<>();
                cookieDic.put(this.ckey, this.cookie);
                local.set("quark", this.cookie, new JSONObject(cookieDic).toString());
            }
        }
        if (resp.code() != 200 && leftRetry > 0) {
            Utils.sleep(1);
            return api(url, data, leftRetry - 1, method);
        }
        return resp;
    }

    public Map<String, String> getShareData(String url) {
        Pattern regex = Pattern.compile("https://pan\\.quark\\.cn\\/s\\/([^\\|#/]+)");
        Matcher matches = regex.matcher(url);
        if (matches.find()) {
            return Map.of("shareId", matches.group(1), "folderId", "0");
        }
        return null;
    }

    public void getShareToken(QuarkApi.ShareData shareData) throws ExecutionException, InterruptedException {
        if (!shareTokenCache.containsKey(shareData.shareId)) {
            JSONObject shareToken = api("share/sharepage/token?" + pr, Map.of("pwd_id", shareData.shareId, "passcode", shareData.sharePwd), null, "get");
            if (shareToken != null && shareToken.has("data") && shareToken.getJSONObject("data").has("stoken")) {
                shareTokenCache.put(shareData.shareId, shareToken.getJSONObject("data").getJSONObject("stoken").toString());
            }
        }
    }

    public List<Item> listFile(QuarkApi.ShareData shareData, List<Item> videos, List<Item> subtitles, String shareId, String folderId, int page) throws ExecutionException, InterruptedException {
        int prePage = 200;
        page = page != 0 ? page : 1;
        JSONObject listData = api("share/sharepage/detail?" + pr + "&pwd_id=" + shareId + "&stoken=" + URLEncoder.encode(shareTokenCache.get(shareData.shareId).get("stoken")) + "&pdir_fid=" + folderId + "&force=0&_page=" + page + "&_size=" + prePage + "&_sort=file_type:asc,file_name:asc", null, null, "get");
        if (listData != null && listData.has("data") && listData.getJSONObject("data").has("list")) {
            List<Item> items = listData.getJSONObject("data").getJSONArray("list").toList().stream().map(item -> Item.objectFrom(item, shareData, page)).collect(Collectors.toList());
            if (page < Math.ceil(listData.getJSONObject("metadata").getJSONObject("_total").toString() / prePage)) {
                List<Item> nextItems = listFile(shareData, videos, subtitles, shareId, folderId, page + 1);
                items.addAll(nextItems);
            }
            for (Item dir : items) {
                if (dir.dir()) {
                    List<Item> subItems = listFile(shareData, videos, subtitles, shareId, dir.fid);
                    items.addAll(subItems);
                }
            }
            return items;
        }
        return new ArrayList<>();
    }

    public JSONObject findBestLCS(Item mainItem, List<Item> targetItems) {
        List<JSONObject> results = targetItems.stream().map(targetItem -> new JSONObject().put("target", targetItem).put("lcs", Utils.lcs(mainItem.name, targetItem.name))).collect(Collectors.toList());
        int bestMatchIndex = 0;
        for (int i = 1; i < results.size(); i++) {
            if (results.get(i).getJSONObject("lcs").length() > results.get(bestMatchIndex).getJSONObject("lcs").length()) {
                bestMatchIndex = i;
            }
        }
        return results.get(bestMatchIndex);
    }

    public List<Item> getFilesByShareUrl(int shareIndex, QuarkApi.ShareData shareInfo, List<Item> videos, List<Item> subtitles) throws ExecutionException, InterruptedException {
        QuarkApi.ShareData shareData = shareInfo instanceof String ? getShareData((String) shareInfo) : (QuarkApi.ShareData) shareInfo;
        if (shareData == null) return Collections.emptyList();
        getShareToken(shareData);
        if (shareTokenCache.get(shareData.shareId) == null) return Collections.emptyList();
        List<Item> items = listFile(shareIndex, shareData, videos, subtitles, shareData.shareId, shareData.folderId);
        if (subtitles.size() > 0) {
            videos.forEach(item -> {
                JSONObject matchSubtitle = findBestLCS(item, subtitles);
                if (matchSubtitle != null) {
                    item.subtitle = matchSubtitle.getJSONObject("target");
                }
            });
        }
        return items;
    }

    public void clean() {
        Map<String, String> saves = new HashMap<>(saveFileIdCaches);
        for (String save : saves.keySet()) {
            saveFileIdCaches.remove(save);
        }
    }

    public void clearSaveDir() throws ExecutionException, InterruptedException {
        JSONObject listData = api("file/sort?" + pr + "&pdir_fid=" + saveDirId + "&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", null, null, "get");
        if (listData != null && listData.has("data") && listData.getJSONObject("data").has("list") && listData.getJSONObject("data").getJSONArray("list").length() > 0) {
            api("file/delete?" + pr, Map.of("action_type", 2, "filelist", listData.getJSONObject("data").getJSONArray("list").toList().stream().map(f -> f.getString("fid")).collect(Collectors.toList()), "exclude_fids", new JSONArray()));
        }
    }

    public void createSaveDir(boolean clean) throws ExecutionException, InterruptedException {
        if (saveDirId != null) {
            if (clean) clearSaveDir();
            return;
        }
        JSONObject listData = api("file/sort?" + pr + "&pdir_fid=0&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", null, null, "get");
        if (listData != null && listData.has("data") && listData.getJSONObject("data").has("list")) {
            for (JSONObject item : listData.getJSONObject("data").getJSONArray("list").toList()) {
                if (item.getString("file_name").equals(saveDirName)) {
                    saveDirId = item.getString("fid");
                    clearSaveDir();
                    break;
                }
            }
        }
        if (saveDirId == null) {
            JSONObject create = api("file?" + pr, Map.of("pdir_fid", "0", "file_name", saveDirName, "dir_path", "", "dir_init_lock", false));
            if (create != null && create.has("data") && create.getJSONObject("data").has("fid")) {
                saveDirId = create.getJSONObject("data").getString("fid");
            }
        }
    }

    public String save(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws ExecutionException, InterruptedException {
        createSaveDir(clean);
        if (clean) clean();
        if (saveDirId == null) return null;
        if (stoken == null) {
            getShareToken(Map.of("shareId", shareId));
            if (shareTokenCache.get(shareId) == null) return null;
        }
        JSONObject saveResult = api("share/sharepage/save?" + pr, Map.of("fid_list", new JSONArray().put(fileId), "fid_token_list", new JSONArray().put(fileToken), "to_pdir_fid", saveDirId, "pwd_id", shareId, "stoken", stoken != null ? stoken : shareTokenCache.get(shareId).getJSONObject("stoken").toString(), "pdir_fid", "0", "scene", "link"));
        if (saveResult != null && saveResult.has("data") && saveResult.getJSONObject("data").has("task_id")) {
            int retry = 0;
            while (true) {
                JSONObject taskResult = api("task?" + pr + "&task_id=" + saveResult.getJSONObject("data").getString("task_id") + "&retry_index=" + retry, null, null, "get");
                if (taskResult != null && taskResult.has("data") && taskResult.getJSONObject("data").has("save_as") && taskResult.getJSONObject("data").getJSONObject("save_as").has("save_as_top_fids") && taskResult.getJSONObject("data").getJSONObject("save_as").getJSONArray("save_as_top_fids").length() > 0) {
                    return taskResult.getJSONObject("data").getJSONObject("save_as").getJSONArray("save_as_top_fids").getJSONObject(0).getString("fid");
                }
                retry++;
                if (retry > 2) break;
                TimeUnit.SECONDS.sleep(1);
            }
        }
        return "false";
    }

    public List<JSONObject> getLiveTranscoding(String shareId, String stoken, String fileId, String fileToken) throws ExecutionException, InterruptedException {
        if (!saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, true);
            if (saveFileId == null) return null;
            saveFileIdCaches.put(fileId, saveFileId);
        }
        JSONObject transcoding = api("file/v2/play?" + pr, Map.of("fid", saveFileIdCaches.get(fileId), "resolutions", "normal,low,high,super,2k,4k", "supports", "fmp4"));
        if (transcoding != null && transcoding.has("data") && transcoding.getJSONObject("data").has("video_list")) {
            return transcoding.getJSONObject("data").getJSONArray("video_list").toList();
        }
        return null;
    }

    public JSONObject getDownload(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws ExecutionException, InterruptedException {
        if (!saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, clean);
            if (saveFileId == null) return null;
            saveFileIdCaches.put(fileId, saveFileId);
        }
        JSONObject down = api("file/download?" + pr + "&uc_param_str", Map.of("fids", new JSONArray().put(saveFileIdCaches.get(fileId))));
        if (down != null && down.has("data")) {
            return down.getJSONObject("data").getJSONObject(0);
        }
        return null;
    }

    public static class ShareData {
        private String shareId;
        private String folderId;
        private String sharePwd;

        public ShareData(String shareId, String folderId, String sharePwd) {
            this.shareId = shareId;
            this.folderId = folderId;
            this.sharePwd = sharePwd;
        }
    }


}

*/
