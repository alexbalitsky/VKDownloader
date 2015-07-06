package sample.manager;

/**
 * Created by Alexandr on 21.04.2015.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * клас, який надсилає запити
 */
public class Executor {
    private static Executor executor;


    private Executor() {

    }

    public static Executor getInstance() {
        if (executor == null) {
            executor = new Executor();
        }
        return executor;
    }

    /**
     * Посилання запросів на сервери «Вконтакті»
     * @param request - запит
     * @return - відповідь
     * @throws IOException
     */
    public String execute(String request) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(request);

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }


}
