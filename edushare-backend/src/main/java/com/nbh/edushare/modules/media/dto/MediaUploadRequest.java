package com.nbh.edushare.modules.media.dto;

import com.nbh.edushare.modules.media.enums.MediaFolder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MediaUploadRequest {
    @NotNull(message = "File không được để trống")
    private MultipartFile file;

    @NotNull(message = "Folder không được để trống")
    private MediaFolder folder;
}
