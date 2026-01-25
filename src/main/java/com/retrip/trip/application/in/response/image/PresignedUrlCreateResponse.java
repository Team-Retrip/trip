package com.retrip.trip.application.in.response.image;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 Presigned Url 생성 Response")
public record PresignedUrlCreateResponse(

        @Schema(description = "이미지 업로드용 presigned url")
        String presignedUrl,

        @Schema(description = "읽기용 이미지 url (presignedUrl로 이미지 업로드 했으면 해당 imageUrl로 이미지 다운로드 가능 하며 여행 생성시 해당 imageUrl 추가하면 됨")
        String readImageUrl
) {
    public static PresignedUrlCreateResponse of(String presignedUrl, String readImageUrl) {
        return new PresignedUrlCreateResponse(presignedUrl,  readImageUrl);
    }
}