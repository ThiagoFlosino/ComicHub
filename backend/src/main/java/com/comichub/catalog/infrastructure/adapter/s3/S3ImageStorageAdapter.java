package com.comichub.catalog.infrastructure.adapter.s3;

import com.comichub.catalog.domain.port.out.ImageStoragePort;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Adapter OUT: grava a imagem no Amazon S3 com o caminho "covers/{isbn}.webp".
 *
 * Nota: a conversão real para WebP requer uma biblioteca nativa (ex: webp-imageio).
 * Por ora, os bytes são armazenados como-estão com Content-Type "image/webp",
 * mantendo a arquitetura pronta para plugar um conversor real.
 */
public class S3ImageStorageAdapter implements ImageStoragePort {

    private static final String KEY_PREFIX = "covers/";
    private static final String KEY_SUFFIX = ".webp";
    private static final String CONTENT_TYPE = "image/webp";

    private final S3Client s3Client;
    private final String bucketName;

    public S3ImageStorageAdapter(S3Client s3Client, String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String store(String isbn, byte[] imageBytes) {
        String key = KEY_PREFIX + isbn + KEY_SUFFIX;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(CONTENT_TYPE)
                        .contentLength((long) imageBytes.length)
                        .build(),
                RequestBody.fromBytes(imageBytes)
        );

        return key;
    }
}
