package com.freemarket.freemarket.global.file.exception;

import com.freemarket.freemarket.global.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileException extends BaseException {

    public static class FileUploadException extends FileException {
        public FileUploadException(String fileName) {
            super("파일 업로드에 실패했습니다: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED");
        }

        public FileUploadException(String fileName, Throwable cause) {
            super("파일 업로드에 실패했습니다: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED");
        }
    }

    public static class ThumbnailCreationException extends FileException {
        public ThumbnailCreationException(String fileName) {
            super("썸네일 생성에 실패했습니다: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR, "THUMBNAIL_CREATION_FAILED");
        }

        public ThumbnailCreationException(String fileName, Throwable cause) {
            super("썸네일 생성에 실패했습니다: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR, "THUMBNAIL_CREATION_FAILED");
        }
    }

    public static class FileNotFoundException extends FileException {
        public FileNotFoundException(String fileName) {
            super("파일을 찾을 수 없습니다: " + fileName, HttpStatus.NOT_FOUND, "FILE_NOT_FOUND");
        }
    }

    public static class DirectoryCreationException extends FileException {
        public DirectoryCreationException(String path) {
            super("디렉토리 생성에 실패했습니다: " + path, HttpStatus.INTERNAL_SERVER_ERROR, "DIRECTORY_CREATION_FAILED");
        }

        public DirectoryCreationException(String path, Throwable cause) {
            super("디렉토리 생성에 실패했습니다: " + path, HttpStatus.INTERNAL_SERVER_ERROR, "DIRECTORY_CREATION_FAILED");
        }
    }

    public static class FileDeletionException extends FileException {
        public FileDeletionException(String path) {
            super("파일 삭제에 실패했습니다: " + path, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DELETION_FAILED");
        }

        public FileDeletionException(String path, Throwable cause) {
            super("파일 삭제에 실패했습니다: " + path, HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DELETION_FAILED");
        }
    }

    public static class EmptyFileException extends FileException {
        public EmptyFileException() {
            super("업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST, "EMPTY_FILE");
        }
    }

    public FileException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }
}
