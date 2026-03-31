package com.retrip.trip.infra.config;

import com.retrip.trip.infra.adapter.out.client.webclient.api.AlarmApiClient;
import com.retrip.trip.infra.adapter.out.client.webclient.api.AuthApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class WebClientConfig {

    @Value("${apis.alarm.url}")
    private String alarmApiUrl;

    @Value("${apis.auth.url}")
    private String authApiUrl;

    @Bean
    public AlarmApiClient alarmApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(alarmApiUrl)
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AlarmApiClient.class);
    }

    @Bean
    public AuthApiClient authApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(authApiUrl)
                .filter((request, next) -> {
                    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attrs != null) {
                        String authorization = attrs.getRequest().getHeader("Authorization");
                        if (authorization != null) {
                            return next.exchange(ClientRequest.from(request)
                                    .header("Authorization", authorization)
                                    .build());
                        }
                    }
                    return next.exchange(request);
                })
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AuthApiClient.class);
    }
}
