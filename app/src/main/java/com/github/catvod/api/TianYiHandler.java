package com.github.catvod.api;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.github.catvod.bean.tianyi.Cache;
import com.github.catvod.bean.tianyi.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.*;
import com.google.gson.JsonObject;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TianYiHandler {

    public static final String API_URL = "https://open.e.189.cn";
    private ScheduledExecutorService service;
    private AlertDialog dialog;
    private final Cache cache;
    private final Cache ecache;
    public static final String AppID = "8025431004";
    public static final String ReturnURL = "https://m.cloud.189.cn/zhuanti/2020/loginErrorPc/index.html";

    public File getCache() {
        return Path.tv("tianyi");
    }

    public File geteCache() {
        return Path.tv("tianyie");
    }

    private String indexUrl = "";

    private String reqId;
    private String lt;
    private Map<String, String> cookieMap;
    private Map<String, String> ecookieMap;
    private String cookie;
    private String ecookie;
    private String sessionKey;
    private String sessionSecret;
    private String refreshToken;
    private String accessToken;

    public TianYiHandler() {

        cookieMap = new HashMap<>();
        ecookieMap = new HashMap<>();
        cache = Cache.objectFrom(Path.read(getCache()));
        ecache = Cache.objectFrom(Path.read(geteCache()));
        cookie = cache.getUser().getCookie();
        sessionKey = cache.getUser().getAccessToken();
        sessionSecret = cache.getUser().getRefreshToken();
        refreshToken = cache.getUser().getSessionKey();
        accessToken = cache.getUser().getSessionSecret();
        ecookie = ecache.getUser().getCookie();
    }

    public JsonObject getSessionForPC(Map<String, String> params) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("appId", AppID); // 替换为实际的AppID常量
        requestParams.putAll(clientSuffix()); // 需要实现clientSuffix方法
        requestParams.putAll(params);

        OkResult result = OkHttp.post("https://api.cloud.189.cn/getSessionForPC.action", requestParams, Map.of("Cookie", this.ecookie, "Referer", API_URL, "accept", "application/json;charset=UTF-8"));

        SpiderDebug.log("getSessionForPC response: " + result.getBody());
        return Json.safeObject(result.getBody());
    }

    public JsonObject loginBySsoCookie(String cookie) throws IOException {
        ecookie = cookie;
        SpiderDebug.log("loginBySsoCookie...");

        Map<String, String> params = new HashMap<>();
        params.put("appId", AppID);
        params.put("clientType", "TELEPC");
        params.put("returnURL", ReturnURL);
        params.put("timeStamp", String.valueOf(System.currentTimeMillis()));

        String loginUrl = OkHttp.getLocation("https://cloud.189.cn/api/portal/unifyLoginForPC.action?" + mapToParamString(params), new HashMap<>());
        Map<String, String> headers = Map.of("Cookie", ecookie);
        String redirectUrl = OkHttp.getLocation(loginUrl, headers);

        return getSessionForPC(Map.of("redirectURL", redirectUrl));
    }

    // 新增参数映射方法
    private String mapToParamString(Map<String, String> params) throws UnsupportedEncodingException {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return StringUtils.join(list, "&");
    }

    public JsonObject refreshToken(String refreshToken) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("clientId", AppID);
        params.put("refreshToken", refreshToken);
        params.put("grantType", "refresh_token");
        params.put("format", "json");

        OkResult result = OkHttp.post("https://api.cloud.189.cn/api/oauth2/refreshToken.do", params, new HashMap<>());
        return Json.safeObject(result.getBody());
    }

    public Map<String, String> clientSuffix() {
        return Map.of("clientType", "TELEPC", "version", "6.2", "channelId", "web_cloud.189.cn", "rand", String.valueOf(System.currentTimeMillis()));
    }

    public void refreshCookie(String coo, String eco) throws IOException {
        this.cookie = coo;
        this.ecookie = eco;
        getCookieMap(List.of(cookie.split(";")));
        geteCookieMap(List.of(ecookie.split(";")));
        Map<String, String> headers = new HashMap<>();
        //  headers.put("Host", "cloud.189.cn");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Sec-Fetch-Site", "same-site");
        headers.put("Sec-Fetch-Mode", "navigate");
        headers.put("Sec-Fetch-Dest", "iframe");
        headers.put("Sec-Ch-Ua", "\"Not:A-Brand\";v=\"24\", \"Chromium\";v=\"134\"");
        headers.put("Sec-Ch-Ua-Mobile", "?0");
        headers.put("Sec-Ch-Ua-Platform", "\"Windows\"");
        // headers.put("Referer", "https://cloud.189.cn/");
        headers.put("Priority", "u=0, i");
        headers.put("Connection", "keep-alive");
        /*
         * apm_key=6A6B13F8887686E883B1226B183B489B; apm_uid=3BAED3AB37D76BB95AC568FFCFEFF0E3; apm_ct=20250318090421000; apm_ua=6DBB10952A38C11D19E2648023D5055B; share_2eyARfBzURZj=kz6y; JSESSIONID=FF2191ED1A23F307A8A58502874E177E; COOKIE_LOGIN_USER=E8A9178705F8CDDFB78A2036131A0D0A2908728B5AB2D4292CB56748AE2B8B904AEB21BDBE6E0D907D52E0B076BE29D5
         */
        headers.put("Cookie", cookie);
        String url = "https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https%3A%2F%2Fcloud.189.cn%2Fweb%2Fredirect.html&defaultSaveName=3&defaultSaveNameCheck=uncheck&browserId=05ceccc253cd2d6e28bc563970f9f6ce";
        String index = OkHttp.getLocation(url, headers);
        SpiderDebug.log("index：" + index);
        SpiderDebug.log("index red: " + index);
        /*
         * GUID=ad7562961e1a44adb24381e3928e4029; GRAYNUMBER=27CD201807E6E6172F3BEF66F3D635B7; pageOp=9fedf699a3178ef7ac45032864abf551; LT=1b84f76ec003c490; SSON=dc466c8192e3109eaea837c1d136c1fd065253ce1c7d3a66ca1520d7d6d6307b10a1fe65c7becac73b95f24a6e681e654ec4f47c39533ebcc48bb78d6d6e63d1bbf3334e6e97eaa7092d34f87bf1209e36d759f9a7d7bc09b34795372d42a82a302d1eaec39a397d35abb256498f85dbfd53f6f31854ee7959aebcce2b8a85fdaa2ea367dbe01456a4cbe9f07c6fab1d4b39495e41bbd34157312b67d03bba9f84a93a2946705c477cf0f4342a7b8b555ba1f098921b572d5eef0f7fe154aaed13e52ae3ee0347c11850a7a8d11410a4612bb428c6adbfc59b13ae9990ddbf4a23570e0f99a4aea9; OPENINFO=33c28688ef52ce9e3a9ef87388047efbde5e3e2e4c7ef6ef267632468c7dfaf294ff59fa59d34801
         */
        //https://open.e.189.cn/api/logbox/oauth2/unifyAccountLogin.do   sson cookie
        headers.put("Cookie", ecookie);
        Map<String, List<String>> resHeaderMap = OkHttp.getLocationHeader(index, headers);

        getCookieMap(resHeaderMap.get("Set-Cookie"));
        this.cookie = mapToCookie(cookieMap);
        indexUrl = resHeaderMap.get("Location").get(0);
        SpiderDebug.log("indexUrl red: " + indexUrl);
        /*
        apm_key=6A6B13F8887686E883B1226B183B489B; apm_uid=3BAED3AB37D76BB95AC568FFCFEFF0E3; apm_ct=20250318090421000; apm_ua=6DBB10952A38C11D19E2648023D5055B; share_2eyARfBzURZj=kz6y; JSESSIONID=FF2191ED1A23F307A8A58502874E177E; COOKIE_LOGIN_USER=E8A9178705F8CDDFB78A2036131A0D0A2908728B5AB2D4292CB56748AE2B8B904AEB21BDBE6E0D907D52E0B076BE29D5
         */
        //https://cloud.189.cn/api/portal/callbackUnify.action
        headers.put("Cookie", cookie);
        OkResult okResult = OkHttp.get(indexUrl, new HashMap<>(), headers);

        SpiderDebug.log("refreshCookie header：" + Json.toJson(okResult.getResp()));
        if (okResult.getResp().containsKey("set-cookie")) {
            getCookieMap(okResult.getResp().get("set-cookie"));
            this.cookie = mapToCookie(cookieMap);
            cache.setTianyiUser(User.objectFrom(cookie));
            SpiderDebug.log("获取cookie成功：" + cookie);

        }
    }

    public byte[] startScan() throws Exception {

       /* OkResult okResult1 = OkHttp.get("https://ux.21cn.com/api/htmlReportRest/getJs.js?pid=25577E0DEEDF48ADBD4459911F5825E4", new HashMap<>(), new HashMap<>());

        getCookieMap(okResult1.getResp().get("Set-Cookie"));
        this.cookie = mapToCookie(cookieMap);*/
        SpiderDebug.log("index ori: " + "https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https%3A%2F%2Fcloud.189.cn%2Fweb%2Fredirect.html&defaultSaveName=3&defaultSaveNameCheck=uncheck&browserId=dff95dced0b03d9d972d920f03ddd05e");
        String index = OkHttp.getLocation("https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https://cloud.189.cn/web/redirect.html&defaultSaveName=3&defaultSaveNameCheck=uncheck&browserId=8d38da4344fba4699d13d6e6854319d7", Map.of("Cookie", ""));
        SpiderDebug.log("index red: " + index);
        Map<String, List<String>> resHeaderMap = OkHttp.getLocationHeader(index, new HashMap<>());
        indexUrl = resHeaderMap.get("Location").get(0);
        SpiderDebug.log("indexUrl red: " + indexUrl);


        getCookieMap(resHeaderMap.get("Set-Cookie"));
        this.cookie = mapToCookie(cookieMap);
        SpiderDebug.log("secondCookie: " + cookie);

        HttpUrl httpParams = HttpUrl.parse(indexUrl);
        reqId = httpParams.queryParameter("reqId");
        lt = httpParams.queryParameter("lt");

        Result result = appConf(this.cookie);

        // Step 1: Get UUID
        JsonObject uuidInfo = getUUID();
        String uuid = uuidInfo.get("uuid").getAsString();
        String encryuuid = uuidInfo.get("encryuuid").getAsString();
        String encodeuuid = uuidInfo.get("encodeuuid").getAsString();

        // Step 2: Get QR Code
        byte[] byteStr = downloadQRCode(encodeuuid, reqId, cookie);

        Init.run(() -> showQRCode(byteStr));
        // Step 3: Check login status
        // return
        Init.execute(() -> startService(uuid, encryuuid, reqId, lt, result.paramId, result.returnUrl, cookie));
        /*Map<String, Object> result = new HashMap<>();
        result.put("qrcode", "data:image/png;base64," + qrCode);
        result.put("status", "NEW");*/
        return byteStr;

    }


    private String api(String url, Map<String, String> params, Map<String, String> headers, Integer retry, String method) throws InterruptedException {


        int leftRetry = retry != null ? retry : 3;

        OkResult okResult;
        if ("GET".equals(method)) {
            okResult = OkHttp.get(this.API_URL + url, params, headers);
        } else {
            okResult = OkHttp.post(this.API_URL + url, params, headers);
        }
        if (okResult.getResp().get("Set-Cookie") != null) {
            geteCookieMap(okResult.getResp().get("Set-Cookie"));
            this.ecookie = mapToCookie(ecookieMap);
            SpiderDebug.log("cookie: " + this.ecookie);
        }

        if (okResult.getCode() != 200 && leftRetry > 0) {
            SpiderDebug.log("请求" + url + " failed;");
            Thread.sleep(1000);
            return api(url, params, headers, leftRetry - 1, method);
        }
        SpiderDebug.log("请求" + url + " 成功;" + "返回结果:" + okResult.getBody());
        return okResult.getBody();
    }

    /**
     * 获取appConf
     *
     * @param secondCookie
     * @return
     */

    private @NotNull Result appConf(String secondCookie) throws Exception {
        Map<String, String> tHeaders = new HashMap<>();
        tHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        tHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/76.0");
        tHeaders.put("Referer", indexUrl);
        tHeaders.put("origin", API_URL);
        tHeaders.put("Lt", lt);
        tHeaders.put("Reqid", reqId);
        tHeaders.put("Cookie", secondCookie);
        Map<String, String> param = new HashMap<>();

        param.put("version", "2.0");
        param.put("appKey", "cloud");
        String paramId;
        String returnUrl;
        String body = api("/api/logbox/oauth2/appConf.do", param, tHeaders, 3, "POST");

        paramId = Json.safeObject(body).get("data").getAsJsonObject().get("paramId").getAsString();
        returnUrl = Json.safeObject(body).get("data").getAsJsonObject().get("returnUrl").getAsString();

        SpiderDebug.log("paramId: " + paramId);
        SpiderDebug.log("returnUrl: " + returnUrl);
        return new Result(paramId, returnUrl);
    }

    private static class Result {
        public final String paramId;
        public final String returnUrl;

        public Result(String paramId, String returnUrl) {
            this.paramId = paramId;
            this.returnUrl = returnUrl;
        }
    }


    private static @NotNull List<String> getCookieList(List<String> cookie) {
        List<String> cookieList = new ArrayList<>();
        for (String s : cookie) {
            String[] split = s.split(";");
            String cookie1 = split[0];
            cookieList.add(cookie1);
        }
        return cookieList;
    }

    // 现有方法：List转Map（已优化）
    private void getCookieMap(List<String> cookie) {

        for (String s : cookie) {
            String[] split = s.split(";");
            String cookieItem = split[0].trim();
            int equalsIndex = cookieItem.indexOf('=');
            if (equalsIndex > 0) {
                String key = cookieItem.substring(0, equalsIndex);
                String value = equalsIndex < cookieItem.length() - 1 ? cookieItem.substring(equalsIndex + 1) : "";
                cookieMap.put(key, value);
            }
        }

    }

    private void geteCookieMap(List<String> cookie) {

        for (String s : cookie) {
            String[] split = s.split(";");
            String cookieItem = split[0].trim();
            int equalsIndex = cookieItem.indexOf('=');
            if (equalsIndex > 0) {
                String key = cookieItem.substring(0, equalsIndex);
                String value = equalsIndex < cookieItem.length() - 1 ? cookieItem.substring(equalsIndex + 1) : "";
                ecookieMap.put(key, value);
            }
        }

    }

    // 新增方法：Map转Cookie字符串
    private String mapToCookie(Map<String, String> map) {
        if (map == null || map.isEmpty()) return "";
        List<String> joiner = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        return StringUtils.join(joiner, ";");
    }

    public JsonObject getUUID() throws InterruptedException {
        Map<String, String> params = new HashMap<>();
        params.put("appId", "cloud");
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        headers.put("lt", lt);
        headers.put("reqId", reqId);
        headers.put("referer", indexUrl);


        String body = api("/api/logbox/oauth2/getUUID.do", params, headers, 3, "POST");
        return Json.safeObject(body);

    }

    public byte[] downloadQRCode(String uuid, String reqId, String cookie) throws IOException {


        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");

        headers.put("referer", indexUrl);
        headers.put("cookie", cookie);
        //  OkResult okResult = OkHttp.get("https://open.e.189.cn/api/logbox/oauth2/image.do", params, headers);
//.addQueryParameter("uuid", uuid).addQueryParameter("REQID", reqId)
        HttpUrl url = HttpUrl.parse(API_URL + "/api/logbox/oauth2/image.do?uuid=" + uuid + "&REQID=" + reqId).newBuilder().build();

        Request request = new Request.Builder().url(url).headers(Headers.of(headers)).build();
        Response response = OkHttp.newCall(request);
        if (response.code() == 200) {
            return response.body().bytes();
        }
        return null;
    }


    private Map<String, Object> checkLoginStatus(String uuid, String encryuuid, String reqId, String lt, String paramId, String returnUrl, String secondCookie) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appId", "cloud");
        params.put("encryuuid", encryuuid);
        params.put("uuid", uuid);
        params.put("date", DateFormatUtils.format(new Date(), "yyyy-MM-ddHH:mm:ss") + new Random().nextInt(24));
        params.put("returnUrl", URLEncoder.encode(returnUrl, "UTF-8"));
        params.put("clientType", "1");
        params.put("timeStamp", (System.currentTimeMillis() / 1000 + 1) + "000");
        params.put("cb_SaveName", "0");
        params.put("isOauth2", "false");
        params.put("state", "");
        params.put("paramId", paramId);
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        headers.put("referer", indexUrl);
        headers.put("Reqid", reqId);
        headers.put("cookie", secondCookie);
        String body = api("/api/logbox/oauth2/qrcodeLoginState.do", params, headers, 3, "POST");
        //  OkResult okResult = OkHttp.post(API_URL + "/api/logbox/oauth2/qrcodeLoginState.do", params, headers);
        SpiderDebug.log("qrcodeLoginState result------" + body);

        JsonObject obj = Json.safeObject(body).getAsJsonObject();
        if (Objects.nonNull(obj.get("status")) && obj.get("status").getAsInt() == 0) {

            SpiderDebug.log("扫码成功------" + obj.get("redirectUrl").getAsString());
            String redirectUrl = obj.get("redirectUrl").getAsString();


            //  fetchUserInfo(redirectUrl, secondCookie);
            JsonObject result = loginBySsoCookie(ecookie);
            //  JsonObject result = getSessionForPC(Map.of("redirectURL", redirectUrl));
            SpiderDebug.log("ty  cookie info------" + Json.toJson(result));
            this.refreshToken = result.get("refreshToken").getAsString();
            this.accessToken = result.get("accessToken").getAsString();
            this.sessionKey = result.get("sessionKey").getAsString();
            this.sessionSecret = result.get("sessionSecret").getAsString();
            User user = new User(result.get("accessToken").getAsString(), result.get("refreshToken").getAsString(), result.get("sessionKey").getAsString(), result.get("sessionSecret").getAsString());
            cache.setTianyiUser(user);
            //停止检验线程，关闭弹窗
            stopService();
        } else {
            SpiderDebug.log("扫码失败------" + body);
        }


        return null;
    }

    private void fetchUserInfo(String redirectUrl, String secondCookie) throws IOException {


        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", secondCookie);
        Map<String, List<String>> okResult = OkHttp.getLocationHeader(redirectUrl, headers);
        SpiderDebug.log("扫码返回数据：" + Json.toJson(okResult));
        if (okResult.containsKey("set-cookie")) {
            getCookieMap(okResult.get("Set-Cookie"));
            this.cookie = mapToCookie(cookieMap);


            cache.setTianyiUser(User.objectFrom(cookie));
            ecache.setTianyieUser(User.objectFrom(ecookie));
            SpiderDebug.log("获取cookie成功：" + cookie);
            SpiderDebug.log("获取ecookie成功：" + ecookie);
            //停止检验线程，关闭弹窗
            stopService();
        }


       /* if (okResult.getCode() == 200) {
            okResult.getBody();
        }*/
        return;

    }


    /**
     * 显示qrcode
     *
     * @param bytes
     */
    public void showQRCode(byte[] bytes) {
        try {
            int size = ResUtil.dp2px(240);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            ImageView image = new ImageView(Init.context());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageBitmap(QRCode.Bytes2Bimap(bytes));
            FrameLayout frame = new FrameLayout(Init.context());
            params.gravity = Gravity.CENTER;
            frame.addView(image, params);
            dialog = new AlertDialog.Builder(Init.getActivity()).setView(frame).setOnCancelListener(this::dismiss).setOnDismissListener(this::dismiss).show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Notify.show("请使用天翼网盘App扫描二维码");
        } catch (Exception ignored) {
        }
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    private void dismiss(DialogInterface dialog) {
        stopService();
    }

    private void stopService() {
        if (service != null) service.shutdownNow();
        Init.run(this::dismiss);
    }

    public void startService(String uuid, String encryuuid, String reqId, String lt, String paramId, String returnUrl, String secondCookie) {
        SpiderDebug.log("----start  checkLoginStatus  service");

        service = Executors.newScheduledThreadPool(1);

        service.scheduleWithFixedDelay(() -> {
            SpiderDebug.log("----checkLoginStatus ing....");
            try {
                checkLoginStatus(uuid, encryuuid, reqId, lt, paramId, returnUrl, secondCookie);
            } catch (Exception e) {
                SpiderDebug.log("----checkLoginStatus error" + e.getMessage());
                throw new RuntimeException(e);
            }

        }, 1, 3, TimeUnit.SECONDS);
    }

}