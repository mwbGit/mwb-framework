import com.alibaba.fastjson.JSONObject;
import com.mwb.framework.http.client.SimpleHttpClient;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {

    public static void main(String[] argv) {
        Map<String, String> params = new HashMap<String, String>();

        try {
            SimpleHttpClient httpClient = new SimpleHttpClient(1, 10000, 10000);

//            String httpResponse = httpClient.get("http://www.baidu.com", params, null);
            String httpResponse = httpClient.get("http://www.webxml.com.cn/webservices/ChinaTVprogramWebService.asmx?op=getAreaDataSet", params, null);

            System.out.println(httpResponse);
            System.out.println("------------------");
            System.out.println(JSONObject.parse(httpResponse));

            String fileName = "test.html";

            HttpTest testMain = new HttpTest();

//            testMain.setContent(testMain.getFilePath(fileName), httpResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFilePath(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        /**
         getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
         */
        URL url = classLoader.getResource(fileName);
        /**
         * url.getFile() 得到这个文件的绝对路径
         */
        if (url != null) {
            System.out.println(url.getFile());
            File file = new File(url.getFile());
            System.out.println(file.exists());

        } else {
            return null;
        }

        return url.getFile();
    }

    private void setContent(String filePath, String content) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            bw.flush();
            bw.write(content);
            bw.close();// 使用后记得关闭
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
