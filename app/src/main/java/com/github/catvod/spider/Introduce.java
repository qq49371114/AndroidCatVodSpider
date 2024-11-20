package com.github.catvod.spider;

import android.content.Context;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;

import java.util.ArrayList;
import java.util.List;

public class Introduce extends Spider {


    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);

    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Vod> list = new ArrayList<>();
        String pic = "https://s21.ax1x.com/2024/11/20/pAW8wwV.jpg";
        String name = "请关注公众号获取最新更新";
        list.add(new Vod("https://s21.ax1x.com/2024/11/20/pAW8wwV.jpg", name, pic));
        String pic2 = "https://s21.ax1x.com/2024/11/20/pAW8wwV.jpg";
        String name2 = "本接口不收费，请不要付费，谢谢！";
        list.add(new Vod("https://s21.ax1x.com/2024/11/20/pAW8wwV.jpg", name2, pic2));
        return Result.string(list);
    }

}