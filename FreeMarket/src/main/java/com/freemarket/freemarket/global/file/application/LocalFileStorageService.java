package com.freemarket.freemarket.global.file.application;

import com.freemarket.freemarket.global.file.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    private final String uploadDir;
    private final String thumbnailDir;
    private final int thumbnailWidth = 200; // 썸네일 너비 고정값

    public LocalFileStorageService(@Value("${file.upload-dir}") String uploadDir,
                                   @Value("${file.thumbnail-dir}") String thumbnailDir) {
        this.uploadDir = uploadDir;
        this.thumbnailDir = thumbnailDir;
        createDirectoriesIfNotExists(Paths.get(uploadDir));
        createDirectoriesIfNotExists(Paths.get(thumbnailDir));
    }

    @Override
    public FileUploadResult storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new FileException.EmptyFileException();
        }

        String originalFileName = file.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);
        String thumbnailStoreFileName = "thumb_" + storeFileName;

        Path uploadPath = Paths.get(uploadDir);
        Path thumbnailPath = Paths.get(thumbnailDir);
        Path originalFilePath = uploadPath.resolve(storeFileName);
        Path thumbnailFilePath = thumbnailPath.resolve(thumbnailStoreFileName);

        File originalFile = null;
        File thumbnailFile = null;

        try {
            originalFile = originalFilePath.toFile();
            file.transferTo(originalFile);
            log.info("원본 파일 저장 완료: {}", originalFilePath);

            thumbnailFile = thumbnailFilePath.toFile();
            createThumbnail(originalFile, thumbnailFile, thumbnailWidth);
            log.info("썸네일 생성 완료: {}", thumbnailFilePath);

            String webOriginalPath = "/images/" + storeFileName;
            String webThumbnailPath = "/thumbnails/" + thumbnailStoreFileName;
            return new FileUploadResult(webOriginalPath, webThumbnailPath, originalFileName);
        } catch (IOException e) {
            log.error("파일 저장 또는 썸네일 생성 실패: {}", originalFileName, e);
            if (originalFile != null && originalFile.exists()) deleteFileIfExists(originalFilePath);
            if (thumbnailFile != null && thumbnailFile.exists()) deleteFileIfExists(thumbnailFilePath);
            throw new FileException.FileUploadException(originalFileName, e);
        }
    }

    @Override
    public void deleteFile(String webFilePath) {
        if (webFilePath == null || webFilePath.isBlank()) return;
        Path filePath = null;
        try {
            if (webFilePath.startsWith("/images/")) filePath = Paths.get(uploadDir).resolve(webFilePath.substring("/images/".length()));
            else if (webFilePath.startsWith("/thumbnails/")) filePath = Paths.get(thumbnailDir).resolve(webFilePath.substring("/thumbnails/".length()));
            if (filePath != null) deleteFileIfExists(filePath);
            else log.warn("알 수 없거나 처리할 수 없는 파일 경로 형식입니다: {}", webFilePath);
        } catch (Exception e) {
            log.error("파일 경로 처리 또는 삭제 중 오류 발생: {}", webFilePath, e);
        }
    }

    private void createDirectoriesIfNotExists(Path path) {
        try {
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", path, e);
            throw new FileException.DirectoryCreationException(path.toString(), e);
        }
    }

    private String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + (ext.isEmpty() ? "" : "." + ext); // 확장자가 없는 경우 고려
    }

    private String extractExt(String originalFilename) {
        if (originalFilename == null) return "";
        int pos = originalFilename.lastIndexOf(".");
        return (pos == -1 || pos == originalFilename.length() - 1) ? "" : originalFilename.substring(pos + 1).toLowerCase();
    }

    private void createThumbnail(File inputFile, File outputFile, int width) throws IOException {
        if (!inputFile.exists()) {
            throw new FileException.FileNotFoundException(inputFile.getPath());
        }
        try {
            Thumbnails.of(inputFile).width(width).keepAspectRatio(true).toFile(outputFile);
        } catch (IOException e) {
            throw new FileException.ThumbnailCreationException(inputFile.getName(), e);
        }
    }

    private void deleteFileIfExists(Path filePath) {
        try {
            if (Files.exists(filePath)) Files.delete(filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
            throw new FileException.FileDeletionException(filePath.toString(), e);
        }
    }
}
