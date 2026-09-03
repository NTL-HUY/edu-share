package com.nbh.edushare.modules.media.dto;

public record UploadResult(
        String url,
        String publicId,
        Integer width,
        Integer height,
        String format
) {}