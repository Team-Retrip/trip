package com.retrip.trip.application.in.base;

import com.retrip.trip.infra.config.GatewayTestConfig;
import com.retrip.trip.infra.config.QuerydslConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import({QuerydslConfig.class, GatewayTestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.sql.init.mode=never")
public abstract class BaseServiceTest {
}
