package com.github.catvod.api;

import android.text.TextUtils;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.tianyi.Cache;
import com.github.catvod.bean.tianyi.Item;
import com.github.catvod.bean.tianyi.ShareData;
import com.github.catvod.bean.tianyi.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.Proxy;
import com.github.catvod.utils.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TianyiApi {
    private String apiUrl = "https://cloud.189.cn/api/open/share/";
    public static final String URL_START = "https://cloud.189.cn/";
    private User user = null;


    private Map<String, JsonObject> shareTokenCache = new HashMap<>();


    private final Cache cache;
    private ScheduledExecutorService service;
    private String sessionKey = "";
    private TianYiHandler tianYiHandler;


    public String[] getPlayFormatList() {
        return new String[]{"天意"};
    }

    private static class Loader {
        static volatile TianyiApi INSTANCE = new TianyiApi();
    }

    public static TianyiApi get() {
        return TianyiApi.Loader.INSTANCE;
    }

    public void setCookie(String token) throws Exception {
        if (StringUtils.isNoneBlank(token)) {
            this.user = Json.parseSafe(token, User.class);
            initUserInfo();
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch");

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("accept", "application/json;charset=UTF-8");

        if (StringUtils.isNotBlank(sessionKey)) {
            headers.put("sessionKey", sessionKey);
        }

        return headers;
    }


    public void init(String cookie) throws Exception {


        getUserSizeInfo();
        this.sessionKey = getUserBriefInfo();
    }

    private TianyiApi() {
        Init.checkPermission();
        cache = Cache.objectFrom(Path.read(getCache()));
        tianYiHandler = new TianYiHandler();
    }

    public File getCache() {
        return Path.tv("tianyi");
    }

    public Vod getVod(ShareData shareData) throws Exception {
        getShareToken(shareData);
        List<Item> files = new ArrayList<>();
        List<Item> subs = new ArrayList<>();
        try {
            JsonArray listData = listFile(1, shareData, files, subs, shareData.getShareId(), shareData.getFolderId(), 1);

        } catch (Exception e) {
            SpiderDebug.log("资源已取消:" + e.getMessage());
            Notify.show("资源已取消");
            throw new RuntimeException(e);
        }


        List<String> playFromtmp = new ArrayList<>();
        playFromtmp.add("天意");


        List<String> playUrl = new ArrayList<>();

        if (files.isEmpty()) {
            return null;
        }

        for (int index = 0; index < playFromtmp.size(); index++) {
            List<String> vodItems = new ArrayList<>();
            for (Item video_item : files) {
                vodItems.add(video_item.getEpisodeUrl("电影"));// + findSubs(video_item.getName(), subs));
            }
            playUrl.add(TextUtils.join("#", vodItems));
        }


        Vod vod = new Vod();
        vod.setVodId("");
        vod.setVodContent("");
        vod.setVodPic("");
        vod.setVodName("");
        vod.setVodPlayUrl(TextUtils.join("$$$", playUrl));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFromtmp));
        vod.setTypeName("天意云盘");
        return vod;
    }

    public String playerContent(String[] split, String flag) throws Exception {

        String fileId = split[0], shareId = split[1];
        String playUrl = "";

        playUrl = this.getDownload(shareId, fileId);
        Map<String, String> header = getHeaders();
        header.remove("Host");
        header.remove("Content-Type");
        return Result.get().url(ProxyVideo.buildCommonProxyUrl(playUrl, header)).octet().header(header).string();
    }


    private String proxyVideoUrl(String url, Map<String, String> header) {
        return String.format(Proxy.getUrl() + "?do=quark&type=video&url=%s&header=%s", Util.base64Encode(url.getBytes(Charset.defaultCharset())), Util.base64Encode(Json.toJson(header).getBytes(Charset.defaultCharset())));
    }

    /**
     * @param url
     * @param params get 参数
     * @param data   post json
     * @param retry
     * @param method
     * @return
     * @throws Exception
     */
    private String api(String url, Map<String, String> params, Map<String, Object> data, Integer retry, String method) throws Exception {


        int leftRetry = retry != null ? retry : 3;
        if (Objects.isNull(this.user)) {
            this.initUserInfo();
            return api(url, params, data, leftRetry - 1, method);
        }
        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.apiUrl + url, params, getHeaders());
        } else {
            okResult = OkHttp.post(this.apiUrl + url, Json.toJson(data), getHeaders());
        }
       /* if (okResult.getResp().get("Set-Cookie") != null) {
            Matcher matcher = Pattern.compile("__puus=([^;]+)").matcher(StringUtils.join(okResult.getResp().get("Set-Cookie"), ";;;"));
            if (matcher.find()) {
                Matcher cookieMatcher = Pattern.compile("__puus=([^;]+)").matcher(this.cookie);
                if (cookieMatcher.find() && !cookieMatcher.group(1).equals(matcher.group(1))) {
                    this.cookie = this.cookie.replaceAll("__puus=[^;]+", "__puus=" + matcher.group(1));
                } else {
                    this.cookie = this.cookie + ";__puus=" + matcher.group(1);
                }
            }
        }
*/
        if (okResult.getCode() != 200 && leftRetry > 0) {
            SpiderDebug.log("api error code:" + okResult.getCode());
            Thread.sleep(1000);
            return api(url, params, data, leftRetry - 1, method);
        }
        return okResult.getBody();
    }

    private void initUserInfo() {
        try {
            SpiderDebug.log("initUserInfo...");

            //extend没有cookie，从缓存中获取
            if (Objects.isNull(this.user)) {
                SpiderDebug.log(" cookie from ext is empty...");
                user = cache.getUser();
            }
            // init(cookie);

        } catch (Exception e) {
            cache.getUser().clean();
            e.printStackTrace();

        }
    }


    public ShareData getShareData(String url, String accessCode) {
        String shareCode = "";
        // 第一种匹配规则：使用预编译的 regex
        Matcher matcher = Pattern.compile("https:\\/\\/cloud\\.189\\.cn\\/web\\/share\\?code=([^&]+)").matcher(url);
        if (matcher.find() && matcher.group(1) != null) {
            shareCode = matcher.group(1);
            // 从shareCode中提取访问码
            Matcher accessMatcher = Pattern.compile("访问码：([a-zA-Z0-9]+)").matcher(shareCode);
            if (accessMatcher.find()) {
                accessCode = accessMatcher.group(1);

            } else {
                accessCode = "";
            }
        } else {
            // 第二种匹配规则：直接匹配 cloud.189.cn/t/ 格式
            Matcher fallbackMatcher = Pattern.compile("https://cloud\\.189\\.cn/t/([^&]+)").matcher(url);
            if (fallbackMatcher.find()) {
                shareCode = fallbackMatcher.group(1);
            } else {
                shareCode = null;
            }
            // 再次尝试从shareCode提取访问码
            if (shareCode != null) {
                Matcher accessMatcher = Pattern.compile("访问码：([a-zA-Z0-9]+)").matcher(shareCode);
                accessCode = accessMatcher.find() ? accessMatcher.group(1) : "";
            } else {
                accessCode = "";
            }
        }

        //shareCode = shareCode.split("（访问码")[0].trim();
        ShareData shareData = new ShareData(shareCode, "0");
        shareData.setSharePwd(accessCode);
        return shareData;

    }


    private String getUserBriefInfo() throws Exception {
        OkResult result = OkHttp.get("https://cloud.189.cn/api/portal/v2/getUserBriefInfo.action", new HashMap<>(), getHeaders());
        JsonObject obj = Json.safeObject(result.getBody());
        return obj.get("sessionKey").getAsString();
    }

    private String getUserSizeInfo() throws Exception {
        OkResult result = OkHttp.get("https://cloud.189.cn/api/portal/getUserSizeInfo.action", new HashMap<>(), getHeaders());
        JsonObject res = Json.safeObject(result.getBody());
        if (res.isEmpty() || (Objects.nonNull(res.get("errorCode")) && res.get("errorCode").getAsString().equals("InvalidSessionKey"))) {
            // tianYiHandler.startScan();
            //tianYiHandler.refreshCookie(cookie);
            tianYiHandler.startScan();
        }
        return "";

    }


    private void getShareToken(ShareData shareData) throws Exception {
        if (!this.shareTokenCache.containsKey(shareData.getShareId())) {
            this.shareTokenCache.remove(shareData.getShareId());
            JsonObject shareToken = Json.safeObject(api("getShareInfoByCodeV2.action?noCache=0.8886566349412803&shareCode=" + shareData.getShareId(), new HashMap<>(), new HashMap<>(), 0, "GET"));
            /**
             * {
             *   "res_code" : 0.0,
             *   "res_message" : "成功",
             *   "accessCode" : "",
             *   "creator" : {
             *     "iconURL" : "",
             *     "oper" : false,
             *     "ownerAccount" : "185****1601@189.cn",
             *     "superVip" : 33.0,
             *     "vip" : 0.0
             *   },
             *   "expireTime" : 6.0,
             *   "expireType" : 1.0,
             *   "fileCreateDate" : "2025-03-20 13:49:18",
             *   "fileId" : "12350115314094",
             *   "fileLastOpTime" : "2025-03-20 13:49:19",
             *   "fileName" : "05_如何制作动感影集.mp4等",
             *   "fileSize" : 0.0,
             *   "fileType" : "batchShare",
             *   "isFolder" : true,
             *   "needAccessCode" : 1.0,
             *   "reviewStatus" : 1.0,
             *   "shareDate" : 1.742449758E12,
             *   "shareMode" : 1.0,
             *   "shareType" : 1.0
             * }
             */
            if (Objects.nonNull(shareToken.get("res_code")) && shareToken.get("res_code").getAsInt() == 0) {
                shareData.setShareId(shareToken.get("shareId").getAsString());
                shareData.setShareMode(shareToken.get("shareMode").getAsInt());
                shareData.setFolder(shareToken.get("isFolder").getAsBoolean());
                shareData.setFileId(shareToken.get("fileId").getAsString());
                shareData.setFolderId(shareToken.get("fileId").getAsString());

                this.shareTokenCache.put(shareData.getShareId(), shareToken);
            }
        }
    }

    private JsonArray listFile(int shareIndex, ShareData shareData, List<Item> videos, List<Item> subtitles, String shareId, String folderId, Integer page) throws Exception {
        int prePage = 200;
        page = page != null ? page : 1;
        String url = "listShareDir.action?" + "pageNum=" + page + "&pageSize=" + prePage + "&fileId=" + folderId + "&shareDirFileId=" + folderId + "&isFolder=" + shareData.getFolder() + "&shareId=" + shareId + "&shareMode=" + shareData.getShareMode() + "&iconOption=5" + "&orderBy=filename" + "&descending=false" + "&accessCode=" + shareData.getSharePwd();

        JsonObject listData = Json.safeObject(api(url, Collections.emptyMap(), Collections.emptyMap(), 0, "GET"));
        if (listData.get("res_code").getAsInt() != 0) return new JsonArray();
        if (listData.get("fileListAO").getAsJsonObject().get("count").getAsInt() == 0 && listData.get("fileListAO").getAsJsonObject().get("fileListSize").getAsInt() == 0)
            return new JsonArray();

        JsonArray items = listData.get("fileListAO").getAsJsonObject().get("fileList").getAsJsonArray();
        JsonArray subDir = listData.get("fileListAO").getAsJsonObject().get("folderList").getAsJsonArray();

        for (JsonElement item : items) {
            if (item.getAsJsonObject().get("mediaType").getAsInt() == 3) {
                if (item.getAsJsonObject().get("size").getAsInt() < 1024 * 1024 * 5) continue;

                videos.add(Item.objectFrom(item.getAsJsonObject(), shareData.getShareId(), shareIndex));
            } /*else if ("file".equals(item.get("type")) && this.subtitleExts.contains("." + Util.getExt((String) item.get("file_name")))) {
                subtitles.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            }*/
        }
        if (listData.get("fileListAO").getAsJsonObject().get("count").getAsInt() > (items.size() + subDir.size())) {
            JsonArray nextItems = listFile(shareIndex, shareData, videos, subtitles, shareId, folderId, page + 1);
            items.addAll(nextItems);
        }
        for (JsonElement dir : subDir) {
            String subfolderId = dir.getAsJsonObject().get("id").getAsString();
            JsonArray subItems = listFile(shareIndex, shareData, videos, subtitles, shareId, subfolderId, null);
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
        ShareData shareData = getShareData((String) shareInfo, "");
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


    private String getDownload(String shareId, String fileId) throws Exception {
        Map<String, String> headers = getHeaders();
        Map<String, String> param = new HashMap<>(Map.of("shareId", shareId, "dt", "1", "fileId", fileId, "type", "4", "key", "noCache"));
        String timeStamp=String.valueOf(System.currentTimeMillis());
        param.put("AccessToken",this.user.getAccessToken());
        param.put("Timestamp",timeStamp);
        param.put("params", encryptParams(param));
        headers.putAll(tianYiHandler.clientSuffix());
        Map<String, String> header = signatureHeader("https://cloud.189.cn/api/portal/getNewVlcVideoPlayUrl.action", "GET", encryptParams(param));
        headers.putAll(header);
       /* headers.put("accessToken", this.user.getAccessToken());
        headers.put("Sign-Type", "1");
        headers.put("Timestamp", timeStamp);
        headers.put("Signature", signatureOfMd5(param));*/

        //headers.remove("sessionKey");
        OkResult result = OkHttp.get("https://cloud.189.cn/api/portal/getNewVlcVideoPlayUrl.action?shareId=" + shareId + "&dt=1&fileId=" + fileId + "&type=4&key=noCache", new HashMap<>(), headers);
        JsonObject res = Json.safeObject(result.getBody());
        if (Objects.nonNull(res.get("res_code")) && res.get("res_code").getAsInt() == 0) {

            if (res.get("normal") != null) {
                String normal = res.get("normal").getAsJsonObject().get("url").getAsString();
                //String downloadUrl = OkHttp.getLocation(normal, headers);
                SpiderDebug.log("获取天翼下载地址成功:" + normal);
                return normal;
            }
        } else {
            SpiderDebug.log("获取下载地址失败:" + result.getBody());
        }
        return "";
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

    public String signatureOfMd5(Map<String, String> params) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            keys.add(entry.getKey() + "=" + entry.getValue());
        }

        Collections.sort(keys);
        String signStr = String.join("&", keys);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(signStr.getBytes());
            byte[] digest = md.digest();
            return bytesToHex(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }


    // HMAC 签名
    public String signatureOfHmac(String sessionSecret, String sessionKey, String operate, String fullUrl, String dateOfGmt, String param) throws Exception {
        Matcher matcher = Pattern.compile("://[^/]+((/[^/\\s?#]+)*)").matcher(fullUrl);
        matcher.find();
        String urlPath = matcher.group(1);

        String data = String.format("SessionKey=%s&Operate=%s&RequestURI=%s&Date=%s", sessionKey, operate, urlPath, dateOfGmt);
        //if (!param.isEmpty()) data += "&params=" + param;

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(sessionSecret.getBytes(), "HmacSHA1"));
        return bytesToHex(mac.doFinal(data.getBytes())).toUpperCase();
    }


    // AES ECB 加密
    public String aesECBEncrypt(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"));
        return bytesToHex(cipher.doFinal(pkcs7Padding(data.getBytes(), 16))).toUpperCase();
    }

    private byte[] pkcs7Padding(byte[] data, int blockSize) {
        int padding = blockSize - (data.length % blockSize);
        byte[] padBytes = new byte[data.length + padding];
        System.arraycopy(data, 0, padBytes, 0, data.length);
        Arrays.fill(padBytes, data.length, padBytes.length, (byte) padding);
        return padBytes;
    }

    public String getHttpDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }


    public Map<String, String> signatureHeader(String url, String method, String params) {
        String dateOfGmt = getHttpDateStr();
        User token = this.user;

        String sessionKey = token.getSessionKey();
        String sessionSecret = token.getSessionSecret();

        Map<String, String> header = new HashMap<>();
        header.put("Date", dateOfGmt);
        header.put("SessionKey", sessionKey);
        header.put("X-Request-ID", UUID.randomUUID().toString());
        try {
            header.put("Signature", signatureOfHmac(sessionSecret, sessionKey, method, url, dateOfGmt, params));
        } catch (Exception e) {
            SpiderDebug.log("生成签名失败: " + e.getMessage());
        }
        return header;
    }

    public String encryptParams(Map<String, String> params) throws Exception {
        User token = this.user;
        String sessionSecret = token.getSessionSecret();

        if (params != null && !params.isEmpty()) {
            String secretKey = sessionSecret.length() >= 16 ? sessionSecret.substring(0, 16) : sessionSecret;
            return aesECBEncrypt(buildQueryString(params), secretKey);
        }
        return "";
    }

    private String buildQueryString(Map<String, String> params) {
        List<String> paramList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramList.add(encodeURIComponent(entry.getKey()) + "=" + encodeURIComponent(entry.getValue()));
        }
        return String.join("&", paramList);
    }

}

