package com.github.catvod.api;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.quark.*;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private final Cache cache;
    private ScheduledExecutorService service;
    private AlertDialog dialog;
    private String serviceTicket;

    private static class Loader {
        static volatile QuarkApi INSTANCE = new QuarkApi();
    }

    public static QuarkApi get() {
        return QuarkApi.Loader.INSTANCE;
    }

    public void setRefreshToken(String token) {
        this.cookie = token;
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

    private Map<String, String> getWebHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch");
        headers.put("Referer", "https://pan.quark.cn/");
        headers.put("Cookie", this.cookie);
        return headers;
    }

    public void initQuark(String cookie) throws Exception {
        this.ckey = Util.MD5(cookie);
        this.cookie = cookie;
        this.isVip = getVip();
    }

    private QuarkApi() {
        Init.checkPermission();

        cache = Cache.objectFrom(Path.read(getCache()));
    }

    public File getCache() {
        return Path.tv("quark");
    }

    public Vod getVod(ShareData shareData) throws Exception {
        getShareToken(shareData);
        List<Item> files = new ArrayList<>();
        List<Item> subs = new ArrayList<>();
        List<Item> listData = listFile(1, shareData, files, subs, shareData.getShareId(), shareData.getFolderId(), 1);

        List<String> playFrom = QuarkApi.get().getPlayFormatList();

        List<String> playUrl = new ArrayList<>();

        if (files.isEmpty()) {
            return null;
        }
        for (int i = 0; i < files.get(files.size() - 1).getShareIndex(); i++) {
            for (int index = 0; index < playFrom.size(); index++) {
                List<String> vodItems = new ArrayList<>();
                for (Item video_item : files) {
                    if (video_item.getShareIndex() == i + 1) {
                        vodItems.add(video_item.getEpisodeUrl("电影"));// + findSubs(video_item.getName(), subs));
                    }
                }
                playUrl.add(TextUtils.join("#", vodItems));
            }
        }


        Vod vod = new Vod();
        vod.setVodId("");
        vod.setVodContent("");
        vod.setVodPic("");
        vod.setVodName("");
        vod.setVodPlayUrl(TextUtils.join("$$$", playUrl));
        vod.setVodPlayFrom(TextUtils.join("$$$", playFrom));
        vod.setTypeName("夸克云盘");
        return vod;
    }

    public String playerContent(String[] split, String flag) throws Exception {

        String fileId = split[0], fileToken = split[1], shareId = split[2], stoken = split[3];
        String playUrl = "";
        if (flag.contains("原画")) {
            playUrl = this.getDownload(shareId, stoken, fileId, fileToken, true);
        } else {
            playUrl = this.getLiveTranscoding(shareId, stoken, fileId, fileToken, flag);
        }
        Map<String, String> header = getHeaders();
        header.remove("Host");
        header.remove("Content-Type");
        return Result.get().url(ProxyVideo.buildCommonProxyUrl(playUrl, header)).octet().header(header).string();
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
        if (StringUtils.isAllBlank(cookie)) {
            this.refreshAccessToken();
            return api(url, params, data, leftRetry - 1, method);
        }
        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.apiUrl + url, params, getHeaders());
        } else {
            okResult = OkHttp.post(this.apiUrl + url, Json.toJson(data), getHeaders());
        }


        if (okResult.getCode() != 200 && leftRetry > 0) {
            Thread.sleep(1000);
            return api(url, params, data, leftRetry - 1, method);
        }
        return okResult.getBody();
    }

    private boolean refreshAccessToken() {
        try {
            SpiderDebug.log("refreshCookie...");
            if (cookie.isEmpty()) {
                SpiderDebug.log("cookie为空");
                throw new RuntimeException("cookie为空");
            }
            String token = serviceTicket;
            OkResult result = OkHttp.get("https://pan.quark.cn/account/info?st=" + token + "&lw=scan", new HashMap<>(), getWebHeaders());
            AccountInfo json = Json.parseSafe(result.getBody(), AccountInfo.class);
            if (json.isSuccess()) {
                List<String> cookies = result.getResp().get("set-Cookie");
                List<String> cookieList = new ArrayList<>();
                for (String cookie : cookies) {
                    cookieList.add(cookie.split(";")[0]);
                }
                this.cookie += TextUtils.join(";", cookieList);

                cache.setUser(User.objectFrom(this.cookie));
                if (cache.getUser().getCookie().isEmpty()) throw new Exception(this.cookie);
                initQuark(this.cookie);
                return true;
            }
            return false;

        } catch (Exception e) {
            cache.getUser().clean();
            e.printStackTrace();
            stopService();
            startFlow();
            return true;
        } finally {
            while (cache.getUser().getCookie().isEmpty()) SystemClock.sleep(250);
        }
    }

    /**
     * 获取二维码登录的令牌
     *
     * @return 返回包含二维码登录令牌的字符串
     */
    private String getTokenForQrcodeLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("client_id", "386");
        params.put("v", "1.2");
        params.put("request_id", UUID.randomUUID().toString());
        OkResult res = OkHttp.get("https://uop.quark.cn/cas/ajax/getTokenForQrcodeLogin", params, new HashMap<>());
        if (this.cookie.isEmpty()) {
            List<String> cookies = res.getResp().get("set-Cookie");
            List<String> cookieList = new ArrayList<>();
            for (String cookie : cookies) {
                cookieList.add(cookie.split(";")[0]);
            }
            this.cookie = TextUtils.join(";", cookieList);
        }
        WebApiResponse json = Json.parseSafe(res.getBody(), WebApiResponse.class);
        if (json.getMessage().equals("ok")) {
            return json.getData().getMembers().getToken();
        }
        return "";
    }


    /**
     * 获取二维码内容
     * <p>
     * 此方法用于生成二维码的URL内容该URL用于二维码登录，包含了登录所需的token和客户端信息
     *
     * @return 返回包含token的二维码URL字符串
     */
    private String getQrCodeToken() {
        // 获取用于二维码登录的token
        String token = getTokenForQrcodeLogin();
        // 组装二维码URL，包含token和客户端标识
        return token;
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
        Map<String, Object> listData = Json.parseSafe(api("member?pr=ucpro&fr=pc&uc_param_str=&fetch_subscribe=true&_ch=home&fetch_identity=true", null, null, 0, "GET"), Map.class);
        return "EXP_SVIP".equals(((Map<String, String>) listData.get("data")).get("member_type"));
    }

    public List<String> getPlayFormatList() {
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
            Map<String, Object> shareToken = Json.parseSafe(api("share/sharepage/token?" + this.pr, Collections.emptyMap(), Map.of("pwd_id", shareData.getShareId(), "passcode", shareData.getSharePwd() == null ? "" : shareData.getSharePwd()), 0, "POST"), Map.class);
            if (shareToken.containsKey("data") && ((Map<String, Object>) shareToken.get("data")).containsKey("stoken")) {
                this.shareTokenCache.put(shareData.getShareId(), (Map<String, Object>) shareToken.get("data"));
            }
        }
    }

    private List<Item> listFile(int shareIndex, ShareData shareData, List<Item> videos, List<Item> subtitles, String shareId, String folderId, Integer page) throws Exception {
        int prePage = 200;
        page = page != null ? page : 1;
        Type type = new TypeToken<ApiResponse<Data>>() {
        }.getType();
        ApiResponse<Data> listData = Json.parseSafe(api("share/sharepage/detail?" + this.pr + "&pwd_id=" + shareId + "&stoken=" + encodeURIComponent((String) this.shareTokenCache.get(shareId).get("stoken")) + "&pdir_fid=" + folderId + "&force=0&_page=" + page + "&_size=" + prePage + "&_sort=file_type:asc,file_name:asc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), type);
        if (listData.getData() == null) return Collections.emptyList();
        List<Item> items = listData.getData().getList();
        if (items == null) return Collections.emptyList();
        List<Item> subDir = new ArrayList<>();
        for (Item item : items) {
            if (Boolean.TRUE.equals(item.isDir())) {
                subDir.add(item);
            } else if (Boolean.TRUE.equals(item.isFile()) && "video".equals(item.getObjCategory())) {
                if (item.getSize() < 1024 * 1024 * 5) continue;
                item.setShareToken((String) this.shareTokenCache.get(shareData.getShareId()).get("stoken"));
                videos.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            } else if ("file".equals(item.getFileType()) && this.subtitleExts.contains("." + Util.getExt(item.getFileName()))) {
                subtitles.add(Item.objectFrom(item, shareData.getShareId(), shareIndex));
            }
        }
        if (page < Math.ceil((double) listData.getMetadata().getTotal() / prePage)) {
            List<Item> nextItems = listFile(shareIndex, shareData, videos, subtitles, shareId, folderId, page + 1);
            items.addAll(nextItems);
        }
        for (Item dir : subDir) {
            List<Item> subItems = listFile(shareIndex, shareData, videos, subtitles, shareId, dir.getFid(), null);
            items.addAll(subItems);
        }
        return items;
    }

    private Map<String, Object> findBestLCS(Item mainItem, List<Item> targetItems) {
        List<Map<String, Object>> results = new ArrayList<>();
        int bestMatchIndex = 0;
        for (int i = 0; i < targetItems.size(); i++) {
            Util.LCSResult currentLCS = Util.lcs(mainItem.getFileName(), targetItems.get(i).getFileName());
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
        Type type = new TypeToken<ApiResponse<Data>>() {
        }.getType();
        ApiResponse<Data> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=" + this.saveDirId + "&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), type);
        if (listData.getData() != null && !listData.getData().getList().isEmpty()) {
            List<String> list = new ArrayList<>();
            for (Item stringStringMap : listData.getData().getList()) {
                list.add(stringStringMap.getFid());
            }
            api("file/delete?" + this.pr, Collections.emptyMap(), Map.of("action_type", "2", "filelist", Json.toJson(list), "exclude_fids", ""), 0, "POST");
        }
    }

    private void createSaveDir(boolean clean) throws Exception {
        if (this.saveDirId != null) {
            if (clean) clearSaveDir();
            return;
        }
        Type type = new TypeToken<ApiResponse<Data>>() {
        }.getType();
        ApiResponse<Data> listData = Json.parseSafe(api("file/sort?" + this.pr + "&pdir_fid=0&_page=1&_size=200&_sort=file_type:asc,updated_at:desc", Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), type);
        if (listData.getData() != null) {
            for (Item item : listData.getData().getList()) {
                if (this.saveDirName.equals(item.getFileName())) {
                    this.saveDirId = item.getFid();
                    clearSaveDir();
                    break;
                }
            }
        }
        if (this.saveDirId == null) {
            Map<String, Object> create = Json.parseSafe(api("file?" + this.pr, Collections.emptyMap(), Map.of("pdir_fid", "0", "file_name", this.saveDirName, "dir_path", "", "dir_init_lock", "false"), 0, "POST"), Map.class);
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
        Type type = new TypeToken<ApiResponse<HashMap<String, String>>>() {
        }.getType();
        ApiResponse<HashMap<String, String>> saveResult = Json.parseSafe(api("share/sharepage/save?" + this.pr, null, Map.of("fid_list", List.of(fileId), "fid_token_list", List.of(fileToken), "to_pdir_fid", this.saveDirId, "pwd_id", shareId, "stoken", stoken != null ? stoken : (String) this.shareTokenCache.get(shareId).get("stoken"), "pdir_fid", "0", "scene", "link"), 0, "POST"), type);
        if (saveResult.getData() != null && (saveResult.getData()).get("task_id") != null) {
            int retry = 0;
            while (true) {
                Type type2 = new TypeToken<ApiResponse<HashMap<String, Object>>>() {
                }.getType();
                ApiResponse<HashMap<String, Object>> taskResult = Json.parseSafe(api("task?" + this.pr + "&task_id=" + (saveResult.getData()).get("task_id") + "&retry_index=" + retry, Collections.emptyMap(), Collections.emptyMap(), 0, "GET"), type2);
                if (taskResult.getData() != null && taskResult.getData().get("save_as") != null && ((Map<String, Object>) taskResult.getData().get("save_as")).get("save_as_top_fids") != null && ((List<String>) ((Map<String, Object>) ((Map<String, Object>) taskResult.getData()).get("save_as")).get("save_as_top_fids")).size() > 0) {
                    return ((List<String>) ((Map<String, Object>) (taskResult.getData()).get("save_as")).get("save_as_top_fids")).get(0);
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
        Type type2 = new TypeToken<ApiResponse<HashMap<String, Object>>>() {
        }.getType();
        ApiResponse<HashMap<String, Object>> transcoding = Json.parseSafe(api("file/v2/play?" + this.pr, Collections.emptyMap(), Map.of("fid", this.saveFileIdCaches.get(fileId), "resolutions", "normal,low,high,super,2k,4k", "supports", "fmp4"), 0, "POST"), type2);
        if (transcoding.getData() != null && (transcoding.getData()).get("video_list") != null) {
            String flagId = flag.split("-")[flag.split("-").length - 1];
            int index = Util.findAllIndexes(getPlayFormatList(), flagId);
            String quarkFormat = getPlayFormatQuarkList().get(index);
            for (Map<String, Object> video : (List<Map<String, Object>>) (transcoding.getData()).get("video_list")) {
                if (video.get("resolution").equals(quarkFormat)) {
                    return (String) ((Map<String, Object>) video.get("video_info")).get("url");
                }
            }
            return (String) ((Map<String, Object>) ((List<Map<String, Object>>) (transcoding.getData()).get("video_list")).get(index).get("video_info")).get("url");
        }
        return null;
    }

    private String getDownload(String shareId, String stoken, String fileId, String fileToken, boolean clean) throws Exception {
        if (!this.saveFileIdCaches.containsKey(fileId)) {
            String saveFileId = save(shareId, stoken, fileId, fileToken, clean);
            if (saveFileId == null) return null;
            this.saveFileIdCaches.put(fileId, saveFileId);
        }
        Map<String, Object> down = Json.parseSafe(api("file/download?" + this.pr + "&uc_param_str=", Collections.emptyMap(), Map.of("fids", this.saveFileIdCaches.get(fileId)), 0, "POST"), Map.class);
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

    private void startFlow() {
        Init.run(this::showInput);
    }

    private void showInput() {
        try {
            int margin = ResUtil.dp2px(16);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            FrameLayout frame = new FrameLayout(Init.context());
            params.setMargins(margin, margin, margin, margin);
            EditText input = new EditText(Init.context());
            frame.addView(input, params);
            dialog = new AlertDialog.Builder(Init.getActivity()).setTitle("请输入cookie").setView(frame).setNeutralButton("QRCode", (dialog, which) -> onNeutral()).setNegativeButton(android.R.string.cancel, null).setPositiveButton(android.R.string.ok, (dialog, which) -> onPositive(input.getText().toString())).show();
        } catch (Exception ignored) {
        }
    }

    private void onNeutral() {
        dismiss();
        Init.execute(this::getQRCode);
    }

    private void onPositive(String text) {
        dismiss();
        Init.execute(() -> {
            if (text.startsWith("http")) setToken(OkHttp.string(text));
            else setToken(text);
        });
    }

    private void getQRCode() {
        String token = getQrCodeToken();

        Init.run(() -> openApp(token));
    }

    private void openApp(String token) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.alicloud.databox", "com.taobao.login4android.scan.QrScanActivity");
            intent.putExtra("key_scanParam", token);
            Init.getActivity().startActivity(intent);
        } catch (Exception e) {
            showQRCode("https://su.quark.cn/4_eMHBJ?uc_param_str=&token=" + token + "&client_id=532&uc_biz_str=S%3Acustom%7COPT%3ASAREA%400%7COPT%3AIMMERSIVE%401%7COPT%3ABACK_BTN_STYLE%400");
        } finally {
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            Init.execute(() -> startService(map));
        }
    }

    private void showQRCode(String content) {
        try {
            int size = ResUtil.dp2px(240);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            ImageView image = new ImageView(Init.context());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageBitmap(QRCode.getBitmap(content, size, 2));
            FrameLayout frame = new FrameLayout(Init.context());
            params.gravity = Gravity.CENTER;
            frame.addView(image, params);
            dialog = new AlertDialog.Builder(Init.getActivity()).setView(frame).setOnCancelListener(this::dismiss).setOnDismissListener(this::dismiss).show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Notify.show("请使用夸克网盘App扫描二维码");
        } catch (Exception ignored) {
        }
    }

    private void startService(Map<String, String> params) {
        params.put("client_id", "532");
        params.put("v", "1.2");
        params.put("request_id", UUID.randomUUID().toString());
        service = Executors.newScheduledThreadPool(1);
        service.scheduleWithFixedDelay(() -> {
            String result = OkHttp.string("https://uop.quark.cn/cas/ajax/getServiceTicketByQrcodeToken", params, getWebHeaders());

            WebApiResponse json = Json.parseSafe(result, WebApiResponse.class);
            if (json.getStatus() == 2000000) {

                setToken(json.getData().getMembers().getServiceTicket());
            }

        }, 1, 1, TimeUnit.SECONDS);
    }

    private void setToken(String value) {
        this.serviceTicket = value;
        SpiderDebug.log("ServiceTicket:" + value);
        Notify.show("ServiceTicket:" + value);
        refreshAccessToken();
        stopService();
    }

    private void stopService() {
        if (service != null) service.shutdownNow();
        Init.run(this::dismiss);
    }

    private void dismiss(DialogInterface dialog) {
        stopService();
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

}

