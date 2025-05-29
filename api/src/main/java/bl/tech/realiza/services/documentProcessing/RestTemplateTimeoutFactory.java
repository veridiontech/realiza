package bl.tech.realiza.services.documentProcessing;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateTimeoutFactory {
    public static RestTemplate create(int timeoutMillis) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(timeoutMillis))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(timeoutMillis))
                .setResponseTimeout(Timeout.ofMilliseconds(timeoutMillis))
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(client);

        return new RestTemplate(factory);
    }
}
