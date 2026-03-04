package com.comichub.catalog.infrastructure.config;

import com.comichub.catalog.domain.port.out.CoverDownloadPort;
import com.comichub.catalog.domain.port.out.ImageStoragePort;
import com.comichub.catalog.infrastructure.adapter.http.HttpCoverDownloadAdapter;
import com.comichub.catalog.infrastructure.adapter.s3.S3ImageStorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.net.http.HttpClient;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            @Value("${aws.region:us-east-1}") String region,
            @Value("${localstack.url:}") String localstackUrl) {

        var builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create());

        if (!localstackUrl.isBlank()) {
            builder.endpointOverride(URI.create(localstackUrl))
                   .serviceConfiguration(S3Configuration.builder()
                           .pathStyleAccessEnabled(true)
                           .build());
        }

        return builder.build();
    }

    @Bean
    public ImageStoragePort imageStoragePort(
            S3Client s3Client,
            @Value("${aws.s3.covers-bucket:comichub-covers}") String bucketName) {
        return new S3ImageStorageAdapter(s3Client, bucketName);
    }

    @Bean
    public CoverDownloadPort coverDownloadPort() {
        return new HttpCoverDownloadAdapter(HttpClient.newHttpClient());
    }
}
