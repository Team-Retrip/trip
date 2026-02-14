package com.retrip.trip.application.in.service;

import com.retrip.trip.application.in.response.image.PresignedUrlCreateResponse;
import com.retrip.trip.application.in.usecase.ImageManageUseCase;
import com.retrip.trip.domain.vo.image.ImageFileExtension;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import static java.util.Locale.ENGLISH;

@RequiredArgsConstructor
@Transactional
@Service
public class ImageService implements ImageManageUseCase {

    private static final String IMAGE_DOMAIN_URL = "https://retrip-media.s3.ap-northeast-2.amazonaws.com";
    private static final String FORDER_NAME = "media"; // 기존 오타 유지
    private static final Duration PRESIGN_DURATION = Duration.ofMinutes(5);

    @Value("${cloud.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public PresignedUrlCreateResponse createImagePresignedUrl(UUID memberId, ImageFileExtension extension) {
        String uuid = UUID.randomUUID().toString(); // 기존 형식 유지
        String fileName = createFileName(uuid, extension);
        String presignedUrl = generatePresignedUrl(fileName);
        String readImageUrl = createReadImageUrl(uuid, extension);

        return PresignedUrlCreateResponse.of(presignedUrl, readImageUrl);
    }

    private String generatePresignedUrl(String fileName) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGN_DURATION)
                .putObjectRequest(putRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private String createReadImageUrl(String uuid, ImageFileExtension extension) {
        return IMAGE_DOMAIN_URL + "/" + createFileName(uuid, extension);
    }

    private String createFileName(String uuid, ImageFileExtension extension) {
        return FORDER_NAME + "/" + uuid + "." + extension.name().toLowerCase(ENGLISH);
    }
}
