package webserver;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        requestDivide("Http_GET.txt", "GET");
    }

    @Test
    public void request_POST() throws Exception {
        requestDivide("Http_POST.txt", "POST");
    }

    private void requestDivide(String s, String get) throws IOException {
        FileInputStream fis = new FileInputStream(new File(testDirectory + s));
        HttpRequest httpRequest = new HttpRequest(fis);

        Assert.assertEquals(get, httpRequest.getMethod());
        Assert.assertEquals("/user/create", httpRequest.getPath());
        Assert.assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        Assert.assertEquals("kbss27", httpRequest.getParameter("userId"));
    }
}
