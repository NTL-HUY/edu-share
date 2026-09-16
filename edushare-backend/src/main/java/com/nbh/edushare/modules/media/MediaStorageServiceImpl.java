package com.nbh.edushare.modules.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.common.exception.ErrorCode;
import com.nbh.edushare.modules.media.dto.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
class MediaStorageServiceImpl implements MediaStorageService {

    private final Cloudinary cloudinary;

    @Override
    public UploadResult upload(MultipartFile file, String folder) {

        if (file.isEmpty()) {
            throw new AppException(MediaErrorCode.FILE_IS_EMPTY);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new AppException(MediaErrorCode.FILE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new AppException(MediaErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );
            return new UploadResult(
                    (String) result.get("secure_url"),
                    (String) result.get("public_id"),
                    (Integer) result.get("width"),
                    (Integer) result.get("height"),
                    (String) result.get("format")
            );
        } catch (IOException e) {
            throw new AppException(MediaErrorCode.UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new AppException(MediaErrorCode.DELETE_FAILED);
        }
    }
}
