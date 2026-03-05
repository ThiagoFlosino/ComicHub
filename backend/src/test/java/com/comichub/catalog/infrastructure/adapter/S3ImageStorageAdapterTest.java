package com.comichub.catalog.infrastructure.adapter;

import com.comichub.catalog.infrastructure.adapter.s3.S3ImageStorageAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task #003 – Teste de integração do S3ImageStorageAdapter com LocalStack.
 *
 * Verifica que:
 *   1. O arquivo é gravado no bucket S3 com o caminho correto.
 *   2. O Content-Type do objeto é "image/webp".
 *   3. O método retorna a chave S3 no formato "covers/{isbn}.webp".
 *
 * Usa LocalStack externo via LOCALSTACK_URL (padrão: http://host.docker.internal:4566).
 */
class S3ImageStorageAdapterTest {

    private static final URI LOCALSTACK_URI = URI.create(
            System.getenv().getOrDefault("LOCALSTACK_URL", "http://host.docker.internal:4566")
    );
    private static final String BUCKET = "comichub-covers-test";

    private S3Client s3Client;
    private S3ImageStorageAdapter adapter;

    @BeforeAll
    static void createBucket() {
        try (var client = buildS3Client()) {
            client.createBucket(b -> b.bucket(BUCKET));
        } catch (S3Exception e) {
            if (!"BucketAlreadyOwnedByYou".equals(e.awsErrorDetails().errorCode())
                    && !"BucketAlreadyExists".equals(e.awsErrorDetails().errorCode())) {
                throw e;
            }
        }
    }

    @BeforeEach
    void setUp() {
        s3Client = buildS3Client();
        adapter = new S3ImageStorageAdapter(s3Client, BUCKET);
    }

    @AfterEach
    void tearDown() {
        s3Client.close();
    }

    @Test
    void shouldReturnCorrectS3Key_whenStoringImage() {
        var isbn = "9780930289232";
        var imageBytes = "fake-image-bytes".getBytes();

        var key = adapter.store(isbn, imageBytes);

        assertThat(key).isEqualTo("covers/" + isbn + ".webp");
    }

    @Test
    void shouldPersistObjectInS3_whenStoringImage() {
        var isbn = "9780000000001";
        var imageBytes = "fake-image-bytes".getBytes();

        var key = adapter.store(isbn, imageBytes);

        var metadata = s3Client.headObject(b -> b.bucket(BUCKET).key(key));
        assertThat(metadata.contentLength()).isEqualTo(imageBytes.length);
    }

    @Test
    void shouldStoreWithWebpContentType() {
        var isbn = "9781401248192";
        var imageBytes = "fake-image-bytes".getBytes();

        var key = adapter.store(isbn, imageBytes);

        var metadata = s3Client.headObject(b -> b.bucket(BUCKET).key(key));
        assertThat(metadata.contentType()).isEqualTo("image/webp");
    }

    private static S3Client buildS3Client() {
        return S3Client.builder()
                .endpointOverride(LOCALSTACK_URI)
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
