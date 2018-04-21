package webserver;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        FileInputStream fis = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest httpRequest = new HttpRequest(fis);

        Assert.assertEquals("GET", httpRequest.getMethod());
        Assert.assertEquals("/user/create", httpRequest.getPath());
        Assert.assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        Assert.assertEquals("kbss27", httpRequest.getParameter("userId"));
    }
}
