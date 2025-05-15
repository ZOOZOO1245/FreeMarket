package com.freemarket.freemarket.global.file.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    record FileUploadResult(String originalFilePath, String thumbnailFilePath, String originalFileName) {}
    FileUploadResult storeFile(MultipartFile file) throws IOException;
    void deleteFile(String webFilePath);
}
