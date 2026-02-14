package com.retrip.trip.infra.adapter.in.presentation.rest;

import com.retrip.trip.application.in.request.*;
import com.retrip.trip.application.in.request.context.UserContext;
import com.retrip.trip.application.in.request.context.WithUserContext;
import com.retrip.trip.application.in.request.image.PresignedUrlCreateRequest;
import com.retrip.trip.application.in.response.*;
import com.retrip.trip.application.in.response.image.PresignedUrlCreateResponse;
import com.retrip.trip.application.in.usecase.*;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExample;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.domain.vo.TripCategory;
import com.retrip.trip.domain.vo.TripStatus;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.retrip.trip.domain.exception.common.ErrorCode.*;

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
