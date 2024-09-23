import android.app.Application;

import com.github.catvod.spider.Ddrk;
import com.github.catvod.spider.HkTv;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class DdrkTest {
    // @Mock
    private Application mockContext;

    private Ddrk spider;

    @org.junit.Before
    public void setUp() throws Exception {
        mockContext = RuntimeEnvironment.application;
        Init.init(mockContext);
        spider = new Ddrk();

        spider.init(mockContext, "{\"site\":\"https://ddys.info/\" ,\"cookie\":\"b-user-id=89ede34e-0efc-e1dd-c997-f16aaa792d0c; _UP_A4A_11_=wb9661c6dfb642f88f73d8e0c7edd398; b-user-id=89ede34e-0efc-e1dd-c997-f16aaa792d0c; ctoken=wla6p3EUOLyn1FSB8IKp1SEW; grey-id=5583e32b-39df-4bf0-f39f-1adf83f604a2; grey-id.sig=p8ReBIMG2BeZu1sYvsuOAZxYbx-MVrsfKEiCv87MsTM; isQuark=true; isQuark.sig=hUgqObykqFom5Y09bll94T1sS9abT1X-4Df_lzgl8nM; _UP_F7E_8D_=ZkyvVHnrBLp1A1NFJIjWi0PwKLOVbxJPcg0RzQPI6KmBtV6ZMgPh38l93pgubgHDQqhaZ2Sfc0qv%2BRantbfg1mWGAUpRMP4RqXP78Wvu%2FCfvkWWGc5NhCTV71tGOIGgDBR3%2Bu6%2Fjj44KlE5biSNDOWW7Bigcz27lvOTidzNw8s%2FWtKAIxWbnCzZn4%2FJMBUub1SIMcW89g57k4mfPmDlCgpZKzxwl6beSfdtZ4RUWXmZOn5v5NkxVKhU4wR0Pq7NklczEGdRq2nIAcu7v22Uw2o%2FxMY0xBdeC9Korm5%2FNHnxl6K%2Bd6FXSoT9a3XIMQO359auZPiZWzrNlZe%2BqnOahXcx7KAhQIRqSOapSmL4ygJor4r5isJhRuDoXy7vJAVuH%2FRDtEJJ8rZTq0BdC23Bz%2B0MrsdgbK%2BiW; _UP_D_=pc; __wpkreporterwid_=3d3f74a7-99b7-4916-3f78-911fc2eb9d87; tfstk=fIoZNxjnbhKwPOu0TWZ4LsaRqirTcudSSmNbnxD0C5VgClMm8xMyB-GsnSu4tjpOflAOmSD-9PNiGl120XrgkVNb1SrqHbJBN3tSBAEYoQOWVUUg9qZ8n1bGGkD3CqGYINKSBABhjnXgp3_Vywz6gSc0Syj3BWf0mr2DLW24eZfiiovEKWefj1q0swq3E82iNEMinMy7SLrcpA4Fh3z_ZAViCfih3PbtdW5N_DuU77AaTijmYRkL2Wq54ENoy5a7ZXxCbok33XzS7QSZgxD-oyoVsdGotql0p2dVu7umC4nLStbiLmParc4FELHrI-c0u2dPVRrs8zoZWKCnIbNZrlHfUCMUz2z8KyXVSlgSFmUojh58OzeqTzgwaGll4YCYKwctDV5coP2LL79eKHxpNTXHmre1kZU32JPWCR_AkP2LL79eLZQY-WeUNdw1.; __pus=2051c82285199d8be553be41dd5a2100AAQ+mmv35G4FDDZ5x+3Mhe2OMbNgweQ1ODbW8zDt9YuP1LQVqHUuAAz9KWLsPjpNtim0AVGHusN4MCosTmbq/khM; __kp=e6604120-6051-11ef-bfe4-c31b6cdd0766; __kps=AATcZArVgS76EPn0FMaV4HEj; __ktd=sii/iz4ePzEaoVirXul7QQ==; __uid=AATcZArVgS76EPn0FMaV4HEj; __itrace_wid=5829b95d-dac1-48d3-bfd5-f60cd9462786; __puus=7da0b96cb710fa1b376934485f977e05AATp/q8/QupT7IiBR1GWqZhxlIRT677smMvoHlLxQA0Lk6CkP0YJBOTl+p9DZgzlMz6w4hPXPgWsokukk8PW7ZfhFfPmv8tKMgLpCGLW+tk57luhNghmSdTeVPkAF59STtyCPBEtiNzNAd/zZJ6qILJDi5ywEBAAAg+gOyWHoLHNUR+QxeHRuQa8g5WWA95J8jebIlrr8rCvI1vjTbtiYktT\",\"token\":\"26fc6787afff43e78b78992e782502f1\"}");

    }



    @org.junit.Test
    public void homeContent() throws Exception {
        String content = spider.homeContent(true);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("homeContent--" + gson.toJson(map));

        //Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void homeVideoContent() throws Exception {
        String content = spider.homeVideoContent();
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("homeVideoContent--" + gson.toJson(map));

        //Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void categoryContent() throws Exception {
        String content = spider.categoryContent("https://ddys.mov/drama/western-drama", "2", true, null);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("categoryContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void detailContent() throws Exception {

        String content = spider.detailContent(Arrays.asList("https://ddys.pro/dahmer-monster/"));
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("detailContent--" + content);
        System.out.println("detailContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void playerContent() throws Exception {
        String froms = "第1季";
        String urls =  "第01集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E01.mp4#第02集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E02.mp4#第03集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E03.mp4#第04集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E04.mp4#第05集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E05.mp4#第06集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E06.mp4#第07集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E07.mp4#第08集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E08.mp4#第09集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E09.mp4#第10集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E10.mp4#第11集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E11.mp4#第12集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E12.mp4#第13集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E13.mp4#第14集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E14.mp4#第15集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E15.mp4#第16集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E16.mp4#第17集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E17.mp4#第18集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E18.mp4#第19集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E19.mp4#第20集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E20.mp4#第21集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E21.mp4#第22集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E22.mp4#第23集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E23.mp4#第24集$https://v.ddys.pro/v/Anime/The_Fable/The_Fable_S01E24.mp4";
        for (int i = 0; i < urls.split("\\$\\$\\$").length; i++) {
            for (int i1 = 0; i1 < urls.split("\\$\\$\\$")[i].split("#").length; i1++) {
                String content = spider.playerContent(froms.split("\\$\\$\\$")[i], urls.split("\\$\\$\\$")[i].split("#")[i1].split("\\$")[1], new ArrayList<>());
                JsonObject map = Json.safeObject(content);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                System.out.println("playerContent--" + content);
                System.out.println("playerContent--" + gson.toJson(map));
                Assert.assertFalse(map.getAsJsonPrimitive("url").getAsString().isEmpty());
            }

        }
    }

    @org.junit.Test
    public void searchContent() throws Exception {
        String content = spider.searchContent("海", false);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("searchContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }
}