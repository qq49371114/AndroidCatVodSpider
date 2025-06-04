package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.api.TianYiHandler;
import com.github.catvod.api.UCTokenHandler;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Introduce extends Spider {


    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);

    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        classes.add(new Class("1", "UC"));
        classes.add(new Class("2", "天意"));
        List<Vod> list = new ArrayList<>();
        String pic = "http://47.109.61.116:86/logo/yylx.gif";
        String name = "遥遥领先";
        list.add(new Vod("http://47.109.61.116:86/logo/yylx.gif", name, pic));
        String pic2 = "http://47.109.61.116:86/logo/yylx.gif";
        String name2 = "本接口专用，谢谢！";
        list.add(new Vod("http://47.109.61.116:86/logo/yylx.gif", name2, pic2));
        String pic3 = "http://47.109.61.116:86/logo/yylx.gif";
        String name3 = "2025-04-14 11:00";
        list.add(new Vod("http://47.109.61.116:86/logo/yylx.gif", name3, pic3));
        return Result.string(classes, list);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        List<Vod> vodList = new ArrayList<>();
        //UC
        if (tid.equals("1")) {
            String pic = "http://47.109.61.116:86/logo/yylx.gif";
            String name = "点击设置Token";
            vodList.add(new Vod("UCToken", name, pic));
        }
        //天翼
        if (tid.equals("2")) {
            String pic = "http://47.109.61.116:86/logo/yylx.gif";
            String name = "点击设置cookie";
            vodList.add(new Vod("天翼cookie", name, pic));
            String pic1 = "http://47.109.61.116:86/logo/yylx.gif";
            String name1 = "清除cookie";
            vodList.add(new Vod("clean天翼cookie", name1, pic1));
        }
        return Result.get().vod(vodList).page().string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);

        //UC Token 扫码
        if (vodId.equals("UCToken")) {
            UCTokenHandler qrCodeHandler = new UCTokenHandler();
            qrCodeHandler.startUC_TOKENScan();
        }
        if (vodId.equals("天翼cookie")) {
            TianYiHandler qrCodeHandler = TianYiHandler.get();
            qrCodeHandler.startFlow();
        }

        if (vodId.equals("clean天翼cookie")) {
            TianYiHandler qrCodeHandler = TianYiHandler.get();
            qrCodeHandler.cleanCookie();
        }
        Vod item = new Vod();
        item.setVodId(vodId);
        item.setVodName("遥遥领先");
        item.setVodPic("http://47.109.61.116:86/logo/yylx.gif");
        item.setVodRemarks("");
        item.setVodPlayFrom("遥遥领先");
        item.setVodPlayUrl("https://test-streams.mux.dev/x36xhzz/url_6/193039199_mp4_h264_aac_hq_7.m3u8");
        item.setVodDirector("遥遥领先");
        return Result.string(item);
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return Result.get().url("https://test-streams.mux.dev/x36xhzz/url_6/193039199_mp4_h264_aac_hq_7.m3u8").string();
    }
}
