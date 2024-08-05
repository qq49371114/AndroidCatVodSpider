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
        String urls =  "第01集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfOrVlYXtr0IrFW8lVeTT91H%2BUSu5z72Gkx6JAGA8EXlJLvetAfVTXXDhxuKCGJ6Q3Ngws821BcBwndpb%2B2GPjbo%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E01.ddr#第02集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfML0vn1KlTxF0sG%2BEHO21R1cpLbSt%2BXLLxl1R4ROccMqgxKrfB%2BNEqcywjLdgdnFeNOYu8Jp86q1LceYrzyfRNA%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E02.ddr#第03集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfLSbJjBcNaYf0d9KYtSt4Dc4guy5jGVmk1NGoRUN%2FTKNo3wIPi9zaht0T3iaVrIG9aowYlrg4M3ZhF5tpKa5a3Q%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E03.ddr#第04集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfOhtDTTFt4SsSqDAXc29K%2FjRaWjRW4rFJYEkL3dGQ8lunDdVoVDCdwLM3MRdkE5GKZiwJnFV4yokt%2FJTlu6W9B0%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E04.ddr#第05集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfOekWLffYiwPueR2d%2F2jlsdoB2MFrn7KZ7RZxnTO2Cl79FQ6JxdhLFZVKQMUfXjl61OPbX6mB2pwuMC8EZDTZM0%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E05.ddr#第06集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfBlB%2BSpxUCnlGPrINcu%2F1SBXw9E48vZepi2rbznJEL%2B7KaG%2FemNxcMt6i2tonlv%2Fj7Yiciu6%2FJp%2FnYHm9azBMMg%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E06.ddr#第07集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfA3oc9ABVnVTvqzAM188IauJwfyfj0mjiLGQipukzelI2Q3Wr%2F8jwBx1eB8yXqjb8GyGOSE4HjylzNNZEeNIPOE%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E07.ddr#第08集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfJOKQJ6QC7TwGBw%2B7XCHqWtRHmOW78EwIA2WdpWoLK6LZMorbbBF7F8CY0%2F8%2BybxkKutVTxcbwJHLYeq5KfaWXE%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E08.ddr#第09集$https://ddys.pro/getvddr/video?id\u003dKqivU7FCez3AzaByqNMjDfGnF9itLkgWcRnE0NAeMTe7Z8DVPXWR%2FJvYQBGf52fOhv3jBlQ5LihxM6O5JO%2FANOmvP8XvKojMNTHvRaasviQ%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E09.ddr#第10集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfFuxdVAN%2F0ZWpHmPYouX2tGpQafvtO9opv55jVEdqxgnUQwDcXdACwZgP4oMbk0O41aQjab0YOv7F6a2%2BcyCxfk%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E10.ddr#第11集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfB7AzCUzGnvni8EaQgDUAeruvMOEP%2By29vrNL0bgRjV9I4o2IIifgYqTV8EVqhjFogonUNDLNcMFJ5w408hLH3k%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E11.ddr#第12集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfNEKVB%2FplZ4P8g2xyropO3Y344w4x0tjIUr6ZONvPl8QALRIdahfpQFpp5qbK4ufOGYGLHq6zXREpfmhWe0HAkI%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E12.ddr#第13集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfBFMTt9r5WqhZpmc4QQgU0iRjSqc8YQDAEEFCzIar44C%2FwGy4p9PfsBWfynJpu42%2BRv4DgCcpqB%2BXLsv8X24NDQ%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E13.ddr#第14集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfNSmR2YMg3lNOPuwjam%2BOxI4BpWDMdwFRRHLh4PixAzLN5296mDjoA763gkjeEDNYsn6HIU597ge05ZI%2BgWizC8%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E14.ddr#第15集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfI4TiWm%2FCMVOWx2lxRgpl%2BVtwmPeyHyP%2FtdIdfekVgRV69u4HkYZYbLrCHFMLabFTSn3jdgVM5TyCIjypo9O%2F0E%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E15.ddr#第16集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfMieUkuUIEB34HJ4yRt0c18%2F%2BfEyUXCCGYReb9sKVXc8X%2FfcV1nHUDLMb9HeDSZSuf14zDDFEk9DHQNpUgvFa8A%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E16.ddr#第17集$https://ddys.pro/getvddr/video?id\u003dfBG%2Flj6cvF9dk4FfQ5csfKeqB9W4XjNkL%2FsKDi7jnfOhHRtJsT4E1o9nXo8o28wnphO1591NKKbPAWn8Sf%2FFcmrKbymYzH4wwwzlKCL1IQY%3D\u0026type\u003dmix|https://ddys.pro/subddr//v/Anime/The_Fable/The_Fable_S01E17.ddr";
        for (int i = 0; i < urls.split("\\$\\$\\$").length; i++) {
            for (int i1 = 0; i1 < urls.split("\\$\\$\\$")[i].split("#").length; i1++) {
                String content = spider.playerContent(froms.split("\\$\\$\\$")[i], urls.split("\\$\\$\\$")[i].split("#")[i1].split("\\$")[1], new ArrayList<>());
                JsonObject map = Json.safeObject(content);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
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