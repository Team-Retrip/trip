package com.retrip.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "Trip API", version = "1.0", description = "여행 관련 API 문서")
)
public class TripApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripApplication.class, args);
    }

}
