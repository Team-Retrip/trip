package com.retrip.trip.infra.config;

import com.retrip.trip.infra.adapter.out.client.webclient.api.AlarmApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    @Value("${apis.alarm.url}")
    private String alarmApiUrl;

    @Bean
    public AlarmApiClient alarmApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(alarmApiUrl)
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AlarmApiClient.class);
    }
}
