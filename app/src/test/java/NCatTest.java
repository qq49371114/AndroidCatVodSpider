import android.app.Application;

import com.github.catvod.spider.Init;
import com.github.catvod.spider.Jianpian;
import com.github.catvod.spider.NCat;
import com.github.catvod.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class NCatTest {
    @Mock
    private Application mockContext;

    private NCat spider;

    @org.junit.Before
    public void setUp() throws Exception {
        Init.init(mockContext);
        spider = new NCat();
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
        String content = spider.categoryContent("1", "2", true, null);
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("categoryContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void detailContent() throws Exception {

        String content = spider.detailContent(Arrays.asList("241982.html"));
        JsonObject map = Json.safeObject(content);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("detailContent--" + gson.toJson(map));
        Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }

    @org.junit.Test
    public void playerContent() throws Exception {
        String froms = "超清$$$4K(高峰不卡)$$$FF线路$$$蓝光3$$$蓝光2$$$蓝光2-1$$$蓝光2-2$$$蓝光9$$$蓝光9-1$$$蓝光9-2";
        String urls = "粤语$241982-32-1020215.html#国语$241982-32-1020217.html$$$粤语$241982-35-1020141.html#国语$241982-35-1020143.html$$$谈判专家粤语版$241982-4-788565.html#谈判专家国语版$241982-4-788566.html$$$（普通话版）$241982-2-578532.html#（粤语版）$241982-2-578533.html$$$1$241982-31-1011262.html$$$1$241982-31-1011263.html$$$1$241982-31-1016415.html$$$正片$241982-36-407941.html$$$正片$241982-36-405254.html$$$正片$241982-36-405255.html";
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

    @org.junit.Test
    public void decryptUrl() throws Exception {
        String content = spider.decryptUrl("e4JFW3eh7Qx08kqoBbbzDmIAgrNUuvyyDxRE/QGyWRN32TtEEuPSJPW9l/bkGgEl6XS/fKy/9a7xwkPKoaYyxeAVsrRY2svY/PTdY6Oc1eKEzQ2xPKlEaMlF8PzAgAM5WNiKGP+kI4eewIMtQlESkSBmXuFlImt+GPSTqBV3RdRc0jkuyz9ZHyTpLVOp8Teqn+7owvvyOAq4rCoaAMkYUw==");
        System.out.println("decryptUrl--" + content);

    }
}