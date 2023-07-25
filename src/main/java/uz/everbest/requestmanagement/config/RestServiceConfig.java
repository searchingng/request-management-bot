package uz.everbest.requestmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uz.everbest.requestmanagement.service.RestService;

@Configuration
public class RestServiceConfig {

    @Value("${telegram.url}")
    private String TELEGRAM_URL;

    @Value("${telegram.token}")
    private String TOKEN;

    private final RestTemplate restTemplate = new RestTemplate();

    @Bean(name = "adminRestService")
    RestService adminRestService() {
        return new RestService(TELEGRAM_URL, TOKEN, restTemplate);
    }

}
