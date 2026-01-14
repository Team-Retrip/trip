package com.retrip.trip.infra.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    @Value("${cloud.aws.region:ap-northeast-2}")
    private String region;

    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }
//
//    @Bean
//    public AmazonS3Client amazonS3Client() {
//        InstanceProfileCredentialsProvider provider= new InstanceProfileCredentialsProvider(true);
//        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
//                .withCredentials(provider)
//                .build();
//    }
}
