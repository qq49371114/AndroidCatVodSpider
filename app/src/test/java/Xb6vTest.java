import android.app.Application;

import com.github.catvod.spider.DaGongRen;
import com.github.catvod.spider.Init;
import com.github.catvod.spider.Xb6v;
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
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class Xb6vTest {
    // @Mock
    private Application mockContext;

    private Xb6v spider;

    @org.junit.Before
    public void setUp() throws Exception {
        mockContext = RuntimeEnvironment.application;
        Init.init(mockContext);
        spider = new Xb6v();
        spider.init(mockContext, "");
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
        String content = spider.categoryContent("/xijupian/", "2", true, new HashMap<>());
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("categoryContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void detailContent() throws Exception {

        String content = spider.detailContent(Arrays.asList("/dongzuopian/24134.html"));
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("detailContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void playerContent() throws Exception {
        String froms = "磁力线路1";
        String urls = "1080p.HD国语中字无水印.mkv$magnet:?xt\u003durn:btih:45683678b2364a56cd85f7cf4df3f85c57af1545\u0026dn\u003d%e6%ae%8a%e6%ad%bb%e4%b8%80%e6%90%8f.6v%e7%94%b5%e5%bd%b1%20%e5%9c%b0%e5%9d%80%e5%8f%91%e5%b8%83%e9%a1%b5%20www.6v123.net%20%e6%94%b6%e8%97%8f%e4%b8%8d%e8%bf%b7%e8%b7%af\u0026tr\u003dudp%3a%2f%2fopentracker.i2p.rocks%3a6969%2fannounce\u0026tr\u003dudp%3a%2f%2ftracker.altrosky.nl%3a6969%2fannounce#2160p.HD国语中字无水印.mkv$magnet:?xt\u003durn:btih:4e4a1742388ada37d3624ed13cb632ab470c8ea3\u0026dn\u003d%e6%ae%8a%e6%ad%bb%e4%b8%80%e6%90%8f.2160p.6v%e7%94%b5%e5%bd%b1%20%e5%9c%b0%e5%9d%80%e5%8f%91%e5%b8%83%e9%a1%b5%20www.6v123.net%20%e6%94%b6%e8%97%8f%e4%b8%8d%e8%bf%b7%e8%b7%af\u0026tr\u003dudp%3a%2f%2ftracker.altrosky.nl%3a6969%2fannounce\u0026tr\u003dudp%3a%2f%2fopentracker.i2p.rocks%3a6969%2fannounce#2160p.60fps.HD国语中字无水印.mkv$magnet:?xt\u003durn:btih:987330707598afa11648134dbb4e5c99b127d5c6\u0026dn\u003d%e6%ae%8a%e6%ad%bb%e4%b8%80%e6%90%8f.2160p.60fps.6v%e7%94%b5%e5%bd%b1%20%e5%9c%b0%e5%9d%80%e5%8f%91%e5%b8%83%e9%a1%b5%20www.6v123.net%20%e6%94%b6%e8%97%8f%e4%b8%8d%e8%bf%b7%e8%b7%af\u0026tr\u003dudp%3a%2f%2fopentracker.i2p.rocks%3a6969%2fannounce\u0026tr\u003dudp%3a%2f%2ftracker.altrosky.nl%3a6969%2fannounce#2160p高码版.HD国语中字无水印.mkv$magnet:?xt\u003durn:btih:8c82a5fad387bb36cdc785e5483cff88e8887857\u0026dn\u003d%e6%ae%8a%e6%ad%bb%e4%b8%80%e6%90%8f.2160p%e9%ab%98%e7%a0%81%e7%89%88.6v%e7%94%b5%e5%bd%b1%20%e5%9c%b0%e5%9d%80%e5%8f%91%e5%b8%83%e9%a1%b5%20www.6v123.net%20%e6%94%b6%e8%97%8f%e4%b8%8d%e8%bf%b7%e8%b7%af\u0026tr\u003dudp%3a%2f%2ftracker.altrosky.nl%3a6969%2fannounce\u0026tr\u003dudp%3a%2f%2fopentracker.i2p.rocks%3a6969%2fannounce";
        for (int i = 0; i < urls.split("\\$\\$\\$").length; i++) {
            String content = spider.playerContent(froms.split("\\$\\$\\$")[i], urls.split("\\$\\$\\$")[i].split("\\$")[1], new ArrayList<>());
            JsonObject map = Json.safeObject(content);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println("playerContent--" + gson.toJson(map));
            Assert.assertFalse(map.getAsJsonPrimitive("url").getAsString().isEmpty());
        }
    }

    @org.junit.Test
    public void searchContent() throws Exception {
        String content = spider.searchContent("红海", false);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("searchContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }
}