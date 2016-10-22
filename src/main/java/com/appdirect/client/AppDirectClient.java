package com.appdirect.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppDirectClient {
    @Autowired
    private RestTemplate restTemplate;

    public <T> T getNotification(final String url, Class<T> resultType) {
        return restTemplate.getForObject(url, resultType);
    }


}
