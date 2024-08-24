import android.app.Application
import com.dokar.quickjs.QuickJs
import com.dokar.quickjs.binding.function
import com.dokar.quickjs.quickJs
import com.github.catvod.net.OkHttp
import com.github.catvod.spider.ChangZhang
import com.github.catvod.spider.Init
import com.github.catvod.utils.FileUtil
import com.github.catvod.utils.Util
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.FileWriter
import kotlin.io.encoding.Base64


class CompileJSTest {
    // @Mock


    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @Test
    @Throws(Exception::class)
    fun homeContent(): Unit {

        val content =
            OkHttp.string("https://androidcatvodspider.pages.dev/json/js/jpyy2.js")/* val bytes = context!!.compileModule(content, "newvision.js")
         val result = "//bb" + Util.base64Encode(bytes)*/

        val scope = CoroutineScope(Dispatchers.Default)

        fun startTask() = runBlocking {
            launch {
                quickJs {

                    val bytecode = compile(
                        code = content,
                        filename = "jpyy",
                        asModule = true,
                    )
                    val str = org.bouncycastle.util.encoders.Base64.encode(bytecode);
                    FileWriter("jpyy.j").write("//bb" +String(str))
                    System.out.println("//bb" +String(str))
                    //assertEquals("Hi from the hello module!", result)
                }
            }
        }
        startTask()

    }
}