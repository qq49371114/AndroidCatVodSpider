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
        spider.init(mockContext, " {\"site\":\"https://ddys.info/\"}");
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
        String content = spider.categoryContent("drama/western-drama", "2", true, null);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("categoryContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void detailContent() throws Exception {

        String content = spider.detailContent(Arrays.asList("https://ddys.pro/the-fable/"));
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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