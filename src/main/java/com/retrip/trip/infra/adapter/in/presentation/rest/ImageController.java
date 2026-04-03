package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.image.PresignedUrlCreateRequest;
import com.retrip.trip.application.in.response.image.PresignedUrlCreateResponse;
import com.retrip.trip.application.in.usecase.ImageManageUseCase;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Image", description = "이미지 관련 API")
@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ImageController {

    private final ImageManageUseCase imageManageUseCase;

    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlCreateResponse> createImagePresignedUrl(
            @WithUserContext UserContext userContext,
            @RequestBody PresignedUrlCreateRequest request) {
        PresignedUrlCreateResponse response = imageManageUseCase.createImagePresignedUrl(userContext.memberId(), request.extension());
        return ApiResponse.created(response);
    }
}
