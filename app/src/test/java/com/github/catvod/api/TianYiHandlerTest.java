package com.github.catvod.api;

import cn.hutool.core.io.FileUtil;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URLEncoder;

@RunWith(RobolectricTestRunner.class)
public class TianYiHandlerTest {

    private TianYiHandler tianYiHandler;


    @Before
    public void setUp() {
        tianYiHandler = new TianYiHandler();

    }

    @Test
    public void startScan() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test
        FileUtil.writeBytes(tianYiHandler.startScan(), "c://qrcode.png");

        while (true) {

        }

    }  @Test
    public void refreshCookie() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test
  //tianYiHandler.refreshCookie("JSESSIONID=E51C209D80B1366B16A45163B3AFE2AA; COOKIE_LOGIN_USER=F86A554EF86A391CB68C48DA4E47148EB3354576B91DA6D3546E70D25F31BA1937550084088849A354FB056D024D8B04");

        while (true) {

        }

    }

    @Test
    public void download() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test


    }

    @Test
    public void testgetUUID() throws Exception {
        JsonObject uuid = tianYiHandler.getUUID();
        System.out.println(uuid);
    }

    @Test
    public void testdownloadQRCode() throws Exception {
/*
        JsonObject uuidInfo = tianYiHandler.getUUID();
        String uuid = uuidInfo.get("uuid").getAsString();
        byte[] qrCode = tianYiHandler.downloadQRCode(uuid);
        FileUtil.writeBytes(qrCode, "c://qrcode.png");

        System.out.println(uuid);*/

        String url = "https://cloud.189.cn/api/portal/callbackUnify.action?browserId=dff95dced0b03d9d972d920f03ddd05e&redirectURL=https%3A%2F%2Fcloud.189.cn%2Fweb%2Fredirect.html";

        String encode = "https%3A%2F%2Fcloud.189.cn%2Fapi%2Fportal%2FcallbackUnify.action%3FbrowserId%3Ddff95dced0b03d9d972d920f03ddd05e%26redirectURL%3Dhttps%253A%252F%252Fcloud.189.cn%252Fweb%252Fredirect.html";
        assert URLEncoder.encode(url, "UTF-8").equals(encode);
    }


}