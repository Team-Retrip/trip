package com.retrip.trip.application.in.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.retrip.trip.application.in.response.image.PresignedUrlCreateResponse;
import com.retrip.trip.application.in.usecase.ImageManageUseCase;
import com.retrip.trip.domain.vo.image.ImageFileExtension;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static com.amazonaws.HttpMethod.PUT;
import static com.amazonaws.services.s3.Headers.S3_CANNED_ACL;
import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;
import static java.util.Locale.ENGLISH;

@RequiredArgsConstructor
@Transactional
@Service
public class ImageService implements ImageManageUseCase {

    private static final String IMAGE_DOMAIN_URL = "https://retrip-media.s3.ap-northeast-2.amazonaws.com";
    private static final String FORDER_NAME = "media";

    @Value("${cloud.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    @Override
    public PresignedUrlCreateResponse createImagePresignedUrl(UUID memberId, ImageFileExtension extension) {
        String uuid = UUID.randomUUID().toString();
        String presignedUrl = createPresignedUrl(uuid, extension);
        String readImageUrl = createReadImageUrl(uuid, extension);
        return PresignedUrlCreateResponse.of(presignedUrl, readImageUrl);
    }

    private String createPresignedUrl(String uuid, ImageFileExtension extension) {
        String fileName = createFileName(uuid, extension);
        GeneratePresignedUrlRequest request = createGeneratePresignedUrlRequest(fileName);
        return amazonS3.generatePresignedUrl(request).toString();
    }

    private String createReadImageUrl(String uuid, ImageFileExtension extension) {
        return IMAGE_DOMAIN_URL
                + "/" + createFileName(uuid, extension);
    }

    private String createFileName(String uuid, ImageFileExtension extension) {
        return FORDER_NAME
                + "/" + uuid
                + "." + extension.name().toLowerCase(ENGLISH);
    }

    private GeneratePresignedUrlRequest createGeneratePresignedUrlRequest(String fileName) {
        var request = new GeneratePresignedUrlRequest(bucket, fileName, PUT)
                .withExpiration(createPresignedUrlExpiration());
        request.addRequestParameter(S3_CANNED_ACL, PublicRead.toString());
        return request;
    }

    private Date createPresignedUrlExpiration() {
        Date expiration = new Date();
        var expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
