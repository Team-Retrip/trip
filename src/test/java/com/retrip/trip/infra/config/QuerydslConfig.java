package com.retrip.trip.infra.config;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.ItineraryQuerydslRepository;
import com.retrip.trip.infra.adapter.out.persistence.mysql.query.TripQuerydslRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@TestConfiguration
public class QuerydslConfig {

    @Autowired
    EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Bean
    public ItineraryQuerydslRepository itineraryQuerydslRepository(
        JPAQueryFactory jpaQueryFactory) {
        return new ItineraryQuerydslRepository(jpaQueryFactory);
    }

    @Bean
    public TripQuerydslRepository tripQuerydslRepository(JPAQueryFactory jpaQueryFactory) {
        return new TripQuerydslRepository(jpaQueryFactory);
    }
}
