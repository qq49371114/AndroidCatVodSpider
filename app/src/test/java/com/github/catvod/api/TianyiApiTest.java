package com.github.catvod.api;

import com.github.catvod.bean.tianyi.ShareData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class TianyiApiTest {

//    @Test
//    public void getShareData() {
//
//
//        ShareData shareData = QuarkApi.get().getShareData("https://pan.quark.cn/s/1e386295b8ca");
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//        System.out.println("getShareData--" + gson.toJson(shareData));
//    }

    @Test
    public void getShareData() throws Exception {

        com.github.catvod.bean.tianyi.ShareData shareData = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=ZvEjUvq6FNr2", "");
        // TianyiApi.get().getVod(shareData);
        com.github.catvod.bean.tianyi.ShareData shareData1 = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=2eyARfBzURZj（访问码：kz6y）", "");

        //  TianyiApi.get().getVod(shareData1);
        ShareData shareData2 = TianyiApi.get().getShareData("https://cloud.189.cn/t/ZvEjUvq6FNr2", "");
        ShareData shareData3 = TianyiApi.get().getShareData("https://cloud.189.cn/t/maqmaijayqQn（访问码：qis7）", "");
        // TianyiApi.get().getVod(shareData2);


    }


    @Test
    public void getVod() throws Exception {

        com.github.catvod.bean.tianyi.ShareData shareData1 = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=ZvEjUvq6FNr2", "");
        TianyiApi api = TianyiApi.get();
        api.setCookie("{\"res_code\":0,\"res_message\":\"成功\",\"accessToken\":\"4ac9176e6b624f6eaa80f1ab0946b5bf\",\"familySessionKey\":\"caf57b24-7cc1-40a6-9cb2-5fba83c97e79_family\",\"familySessionSecret\":\"FEC5CC05873575A90BCCF8967A11F681\",\"getFileDiffSpan\":60,\"getUserInfoSpan\":600,\"keepAlive\":1000,\"loginName\":\"18506241601@189.cn\",\"refreshToken\":\"4e3949ccc59b43c9962ceb9b734d868f\",\"sessionKey\":\"d47c4fbe-54cf-415e-93f1-16e5e35ba4e1\",\"sessionSecret\":\"FEC5CC05873575A90BCCF8967A11F681\"}");
        api.getVod(shareData1);


    }

    @Test
    public void signatureHeader() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", "851f74df-9fdc-4d34-b337-6560310da555");
        param.put("shareId", "12536115513594");
        param.put("fileId", "8139432799826548");
        param.put("isFolder", "false");
        param.put("iconOption", "5");
        param.put("pageSize", "1");
        param.put("pageNum", "1");
        param.put("shareMode", "1");
        param.put("accessCode", "gsf3");
        TianyiApi api = TianyiApi.get();
        api.setCookie("{\"res_code\":0,\"res_message\":\"成功\",\"accessToken\":\"4ac9176e6b624f6eaa80f1ab0946b5bf\",\"familySessionKey\":\"caf57b24-7cc1-40a6-9cb2-5fba83c97e79_family\",\"familySessionSecret\":\"FEC5CC05873575A90BCCF8967A11F681\",\"getFileDiffSpan\":60,\"getUserInfoSpan\":600,\"keepAlive\":1000,\"loginName\":\"18506241601@189.cn\",\"refreshToken\":\"4e3949ccc59b43c9962ceb9b734d868f\",\"sessionKey\":\"d47c4fbe-54cf-415e-93f1-16e5e35ba4e1\",\"sessionSecret\":\"FEC5CC05873575A90BCCF8967A11F681\"}");
        Map<String, String> header = api.signatureHeader("https://api.cloud.189.cn/open/share/listShareDir.action", "GET", api.encryptParams(param));
        System.out.println(header.get("Signature"));


    }
}