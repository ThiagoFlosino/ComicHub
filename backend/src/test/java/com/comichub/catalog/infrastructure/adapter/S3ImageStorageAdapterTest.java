package com.comichub.catalog.infrastructure.adapter;

import com.comichub.AbstractIntegrationTest;
import com.comichub.catalog.infrastructure.adapter.s3.S3ImageStorageAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

/**
 * Task #003 – Teste de integração do S3ImageStorageAdapter com LocalStack.
 *
 * Verifica que:
 *   1. O arquivo é gravado no bucket S3 com o caminho correto.
 *   2. O Content-Type do objeto é "image/webp".
 *   3. O método retorna a chave S3 no formato "covers/{isbn}.webp".
 *
 * Usa LocalStack via Testcontainers (singleton compartilhado em AbstractIntegrationTest).
 * Desabilitado automaticamente quando Docker não está disponível.
 */
@DisabledIf("isDockerUnavailable")
class S3ImageStorageAdapterTest extends AbstractIntegrationTest {

    private static final String BUCKET = "comichub-covers-test";

    private S3Client s3Client;
    private S3ImageStorageAdapter adapter;

    static boolean isDockerUnavailable() {
        return !isDockerAvailable();
    }

    @BeforeAll
    static void createBucket() {
        if (localstack == null) return;
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
                .endpointOverride(localstack.getEndpointOverride(S3))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
