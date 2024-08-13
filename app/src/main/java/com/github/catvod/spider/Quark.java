package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.api.AliYun;
import com.github.catvod.api.QuarkApi;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.bean.quark.ShareData;
import com.github.catvod.crawler.Spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author ColaMint & Adam & FongMi
 */
public class Quark extends Spider {


    @Override
    public void init(Context context, String extend) {

        //QuarkApi.get().setRefreshToken(extend);
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
      /*  List<Item> videoItems = new ArrayList<>();
        List<Item> subItems = new ArrayList<>();

        try {
            quarkApi.getFilesByShareUrl(1, ids.get(0), videoItems, subItems);

            if (!videoItems.isEmpty()) {
                SpiderDebug.log("获取播放链接成功,分享链接为:" + String.join("\t", ids));
            } else {
                SpiderDebug.log("获取播放链接失败,检查分享链接为:" + String.join("\t", ids));
            }
        } catch (InterruptedException e) {
            SpiderDebug.log("获取夸克视频失败,失败原因为:" + e.getMessage() + " 行数为:" + e.getStackTrace()[0].getLineNumber());
        }

*/
        ShareData shareData = QuarkApi.get().getShareData(ids.get(0));
        return Result.string(QuarkApi.get().getVod(shareData));
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        return AliYun.get().playerContent(id.split("\\+"), flag);
    }

    private Vod parseVod(Matcher matcher, String id) {
        String shareId = matcher.group(2);
        String fileId = matcher.groupCount() == 4 ? matcher.group(4) : "";
        return AliYun.get().getVod(id, shareId, fileId);
    }

    /**
     * 獲取詳情內容視頻播放來源（多 shared_link）
     *
     * @param ids share_link 集合
     * @return 詳情內容視頻播放來源
     */
    public String detailContentVodPlayFrom(List<String> ids) {
        List<String> playFrom = new ArrayList<>();
        if (ids.size() < 2)
            return TextUtils.join("$$$", Arrays.asList("轉存原畫", "分享原畫", "代理普畫"));
        for (int i = 1; i <= ids.size(); i++) {
            playFrom.add(String.format(Locale.getDefault(), "轉存原畫#%02d", i));
            playFrom.add(String.format(Locale.getDefault(), "分享原畫#%02d", i));
            playFrom.add(String.format(Locale.getDefault(), "代理普畫#%02d", i));
        }
        return TextUtils.join("$$$", playFrom);
    }

    /**
     * 獲取詳情內容視頻播放地址（多 share_link）
     *
     * @param ids share_link 集合
     * @return 詳情內容視頻播放地址
     */
    public String detailContentVodPlayUrl(List<String> ids) {
       /* List<String> playUrl = new ArrayList<>();
        for (String id : ids) {
            Matcher matcher = pattern.matcher(id);
            if (matcher.find()) playUrl.add(parseVod(matcher, id).getVodPlayUrl());
        }
        return TextUtils.join("$$$", playUrl);*/
        return null;
    }

    public static Object[] proxy(Map<String, String> params) throws Exception {
        String type = params.get("type");
        if ("video".equals(type)) return AliYun.get().proxyVideo(params);
        if ("sub".equals(type)) return AliYun.get().proxySub(params);
        return null;
    }
}
