import android.app.Application;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.server.Server;
import com.github.catvod.utils.Json;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class ProxyTest {
    // @Mock
    private Application mockContext;


    @org.junit.Before
    public void setUp() throws Exception {
        Server.get().start();
    }

    @org.junit.Test
    public void homeContent() throws Exception {


        // OkResult result = OkHttp.get("http://127.0.0.1:9978/proxy?do=proxy&url=aHR0cHM6Ly92aWRlby1wbGF5LWMtemIuZHJpdmUucXVhcmsuY24vRHc0aEVCZ0wvODU3OTQ5NzA5MC82NmM5NWUyYTMzMDA1MDMzZTUzYTQ5YTg4MmQ5YTdhOTlkNTRhZDcwLzY2Yzk1ZTJhOWM5MmVhYjRjNDI3NGI3NDkwMmZlMDBiYWJkMDI5Mzk/YXV0aF9rZXk9MTcyNDk5NjI1NS0xNDgwMzEtMTA4MDAtMDYzMDQ5MDY2NTFlMDU4MDE2ZDY0NjA5ZmU4ODY5YzUmc3A9MTkxJnRva2VuPTMtNDEwOTA1M2JjMjMzM2E3NmM3NjM0YjgzZmE3YzRlMWMtOC0yLTI4Ni0yNjllM2Y2YzFiZWM0ZDliOGZlNDZjZmM3MzQzNzkyNi0wLTAtMC0wLWQ4ZWQ2ODVlMTNmMzViYWQ5MWM1YzFjODdiNGU0MGUwJnVkPTE2LTQtMS0yLTEtMy03LU4tMS0xNi0yLU4=&header=eyJDb29raWUiOiJiLXVzZXItaWRcdTAwM2Q4OWVkZTM0ZS0wZWZjLWUxZGQtYzk5Ny1mMTZhYWE3OTJkMGM7IF9VUF9BNEFfMTFfXHUwMDNkd2I5NjYxYzZkZmI2NDJmODhmNzNkOGUwYzdlZGQzOTg7IGItdXNlci1pZFx1MDAzZDg5ZWRlMzRlLTBlZmMtZTFkZC1jOTk3LWYxNmFhYTc5MmQwYzsgY3Rva2VuXHUwMDNkd2xhNnAzRVVPTHluMUZTQjhJS3AxU0VXOyBncmV5LWlkXHUwMDNkNTU4M2UzMmItMzlkZi00YmYwLWYzOWYtMWFkZjgzZjYwNGEyOyBncmV5LWlkLnNpZ1x1MDAzZHA4UmVCSU1HMkJlWnUxc1l2c3VPQVp4WWJ4LU1WcnNmS0VpQ3Y4N01zVE07IGlzUXVhcmtcdTAwM2R0cnVlOyBpc1F1YXJrLnNpZ1x1MDAzZGhVZ3FPYnlrcUZvbTVZMDlibGw5NFQxc1M5YWJUMVgtNERmX2x6Z2w4bk07IF9VUF9GN0VfOERfXHUwMDNkWmt5dlZIbnJCTHAxQTFORkpJaldpMFB3S0xPVmJ4SlBjZzBSelFQSTZLbUJ0VjZaTWdQaDM4bDkzcGd1YmdIRFFxaGFaMlNmYzBxdiUyQlJhbnRiZmcxbVdHQVVwUk1QNFJxWFA3OFd2dSUyRkNmdmtXV0djNU5oQ1RWNzF0R09JR2dEQlIzJTJCdTYlMkZqajQ0S2xFNWJpU05ET1dXN0JpZ2N6Mjdsdk9UaWR6Tnc4cyUyRld0S0FJeFdibkN6Wm40JTJGSk1CVXViMVNJTWNXODlnNTdrNG1mUG1EbENncFpLenh3bDZiZVNmZHRaNFJVV1htWk9uNXY1Tmt4VktoVTR3UjBQcTdOa2xjekVHZFJxMm5JQWN1N3YyMlV3Mm8lMkZ4TVkweEJkZUM5S29ybTUlMkZOSG54bDZLJTJCZDZGWFNvVDlhM1hJTVFPMzU5YXVaUGlaV3pyTmxaZSUyQnFuT2FoWGN4N0tBaFFJUnFTT2FwU21MNHlnSm9yNHI1aXNKaFJ1RG9YeTd2SkFWdUglMkZSRHRFSko4clpUcTBCZEMyM0J6JTJCME1yc2RnYkslMkJpVzsgX1VQX0RfXHUwMDNkcGM7IF9fd3BrcmVwb3J0ZXJ3aWRfXHUwMDNkM2QzZjc0YTctOTliNy00OTE2LTNmNzgtOTExZmMyZWI5ZDg3OyB0ZnN0a1x1MDAzZGZJb1pOeGpuYmhLd1BPdTBUV1o0THNhUnFpclRjdWRTU21OYm54RDBDNVZnQ2xNbTh4TXlCLUdzblN1NHRqcE9mbEFPbVNELTlQTmlHbDEyMFhyZ2tWTmIxU3JxSGJKQk4zdFNCQUVZb1FPV1ZVVWc5cVo4bjFiR0drRDNDcUdZSU5LU0JBQmhqblhncDNfVnl3ejZnU2MwU3lqM0JXZjBtcjJETFcyNGVaZmlpb3ZFS1dlZmoxcTBzd3EzRTgyaU5FTWluTXk3U0xyY3BBNEZoM3pfWkFWaUNmaWgzUGJ0ZFc1Tl9EdVU3N0FhVGlqbVlSa0wyV3E1NEVOb3k1YTdaWHhDYm9rMzNYelM3UVNaZ3hELW95b1ZzZEdvdHFsMHAyZFZ1N3VtQzRuTFN0YmlMbVBhcmM0RkVMSHJJLWMwdTJkUFZScnM4em9aV0tDbkliTlpybEhmVUNNVXoyejhLeVhWU2xnU0ZtVW9qaDU4T3plcVR6Z3dhR2xsNFlDWUt3Y3REVjVjb1AyTEw3OWVLSHhwTlRYSG1yZTFrWlUzMkpQV0NSX0FrUDJMTDc5ZUxaUVktV2VVTmR3MS47IF9fcHVzXHUwMDNkMjA1MWM4MjI4NTE5OWQ4YmU1NTNiZTQxZGQ1YTIxMDBBQVErbW12MzVHNEZERFo1eCszTWhlMk9NYk5nd2VRMU9EYlc4ekR0OVl1UDFMUVZxSFV1QUF6OUtXTHNQanBOdGltMEFWR0h1c040TUNvc1RtYnEva2hNOyBfX2twXHUwMDNkZTY2MDQxMjAtNjA1MS0xMWVmLWJmZTQtYzMxYjZjZGQwNzY2OyBfX2twc1x1MDAzZEFBVGNaQXJWZ1M3NkVQbjBGTWFWNEhFajsgX19rdGRcdTAwM2RzaWkvaXo0ZVB6RWFvVmlyWHVsN1FRXHUwMDNkXHUwMDNkOyBfX3VpZFx1MDAzZEFBVGNaQXJWZ1M3NkVQbjBGTWFWNEhFajsgX19pdHJhY2Vfd2lkXHUwMDNkNTgyOWI5NWQtZGFjMS00OGQzLWJmZDUtZjYwY2Q5NDYyNzg2OyBfX3B1dXNcdTAwM2RjMGZkYWYwYmQ2MjAwY2EyMmI1OWVlOTllMTlkODdkOEFBVHAvcTgvUXVwVDdJaUJSMUdXcVpoeElxVDdlYzRCeDRSWmVGZ3FVZEhyNFNoTUthYlZVNlByZFdscENZYlkxOHdqekVpbThYNDNNNmNhUVVzL0Y0OU5FRlJDdWlVMDI1VkgrUitzUGw3bGJiUlVCV0pCL21FcFpmc3JMYTdPUGpoSUdhUXg4Z1FMNUlXdW1mUTUwY3FYbW1ISmIwMyt0aTAyZEM0dmR2REFNbUhQYU8yQlIyOHlRUE8rcnIvaUFiQ1RFVEtlYWRtaWNYVm9nUEZYdmpvZyIsIlVzZXItQWdlbnQiOiJNb3ppbGxhLzUuMCAoV2luZG93cyBOVCAxMC4wOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBxdWFyay1jbG91ZC1kcml2ZS8yLjUuMjAgQ2hyb21lLzEwMC4wLjQ4OTYuMTYwIEVsZWN0cm9uLzE4LjMuNS40LWI0Nzg0OTExMDAgU2FmYXJpLzUzNy4zNiBDaGFubmVsL3Bja2tfb3RoZXJfY2giLCJSZWZlcmVyIjoiaHR0cHM6Ly9wYW4ucXVhcmsuY24vIn0=", null, null);
        //System.out.println(result);

        while (true) {
            continue;
        }
    }


}