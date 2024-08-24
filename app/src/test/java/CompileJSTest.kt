import android.app.Application;

import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.ChangZhang;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.Util;
import com.whl.quickjs.android.QuickJSLoader;
import com.whl.quickjs.wrapper.QuickJSContext;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class CompileJSTest {
    // @Mock
    private Application mockContext;

    private ChangZhang spider;
    QuickJSContext context;

    @org.junit.Before
    public void setUp() throws Exception {
        QuickJSLoader.init();
        mockContext = RuntimeEnvironment.application;
        Init.init(mockContext);
        context = QuickJSContext.create();


    }

    @org.junit.Test
    public void homeContent() throws Exception {
        String content = OkHttp.string("https://androidcatvodspider.pages.dev/json/js/newvision.js");
        byte[] bytes = context.compileModule(content, "newvision.js");
        String result = "//bb" + Util.base64Encode(bytes);

        //Assert.assertFalse(map.getAsJsonArray("list").isEmpty());
    }


}