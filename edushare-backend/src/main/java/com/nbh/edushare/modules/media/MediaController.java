package com.nbh.edushare.modules.media;

import com.nbh.edushare.modules.media.dto.MediaUploadRequest;
import com.nbh.edushare.modules.media.dto.UploadResult;
import com.nbh.edushare.modules.media.enums.MediaFolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaStorageService storageService;

    @PostMapping("/upload")
    public UploadResult upload(@ModelAttribute @Valid MediaUploadRequest request) {
        return storageService.upload(request.getFile(), request.getFolder().getPath());
    }
}