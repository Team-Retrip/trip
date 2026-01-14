package com.retrip.trip.application.in.usecase;

import com.retrip.trip.application.in.response.image.PresignedUrlCreateResponse;
import com.retrip.trip.domain.vo.image.ImageFileExtension;

import java.util.UUID;

public interface ImageManageUseCase {
    PresignedUrlCreateResponse createImagePresignedUrl(UUID uuid, ImageFileExtension extension);
}