package com.nbh.edushare.modules.media;

import com.nbh.edushare.modules.media.dto.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {
    UploadResult upload(MultipartFile file, String folder);
    void delete(String publicId);
}