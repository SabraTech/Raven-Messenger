import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class TestHTTP {

    public static void main(String arg[]){
        String URL = "http://0.0.0.0:8080/emoji?message=hi+my+name+is+mohamed";
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(request);
            String responseAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            System.out.println(responseAsString);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
