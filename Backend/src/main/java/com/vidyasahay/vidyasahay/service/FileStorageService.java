package com.vidyasahay.vidyasahay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDirectory;

    public FileStorageService(
            @Value("${app.document.storage-dir}")
            String storageDirectory
    ) {
        try {
            this.storageDirectory = Paths
                    .get(storageDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(
                    this.storageDirectory
            );

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not create document storage directory",
                    exception
            );
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Uploaded file cannot be empty"
            );
        }

        String originalFileName =
                getCleanOriginalFileName(file);

        String storedFileName =
                UUID.randomUUID()
                        + "_"
                        + originalFileName;

        Path targetPath = storageDirectory
                .resolve(storedFileName)
                .normalize();

        if (!targetPath.startsWith(storageDirectory)) {
            throw new RuntimeException(
                    "Invalid document storage path"
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            if (!Files.exists(targetPath)) {
                throw new RuntimeException(
                        "File was not stored successfully"
                );
            }

            /*
             * This path is stored in
             * student_documents.file_path.
             */
            return targetPath.toString();

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to store file: "
                            + originalFileName,
                    exception
            );
        }
    }

    public Resource load(String storedFilePath) {
        if (storedFilePath == null ||
                storedFilePath.isBlank()) {
            throw new RuntimeException(
                    "Stored file path is empty"
            );
        }

        try {
            Path filePath = Paths
                    .get(storedFilePath)
                    .toAbsolutePath()
                    .normalize();

            if (!filePath.startsWith(storageDirectory)) {
                throw new RuntimeException(
                        "Invalid document file path"
                );
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() ||
                    !resource.isReadable()) {
                throw new RuntimeException(
                        "Document file not found: "
                                + filePath
                );
            }

            return resource;

        } catch (MalformedURLException exception) {
            throw new RuntimeException(
                    "Could not load document file",
                    exception
            );
        }
    }

    public boolean exists(String storedFilePath) {
        if (storedFilePath == null ||
                storedFilePath.isBlank()) {
            return false;
        }

        try {
            Path filePath = Paths
                    .get(storedFilePath)
                    .toAbsolutePath()
                    .normalize();

            return filePath.startsWith(storageDirectory)
                    && Files.exists(filePath)
                    && Files.isRegularFile(filePath);

        } catch (Exception exception) {
            return false;
        }
    }

    public String getCleanOriginalFileName(
            MultipartFile file
    ) {
        String originalFileName =
                file.getOriginalFilename();

        if (originalFileName == null ||
                originalFileName.isBlank()) {
            originalFileName = "document";
        }

        String cleanedFileName =
                StringUtils.cleanPath(originalFileName);

        if (cleanedFileName.contains("..")) {
            throw new RuntimeException(
                    "Invalid file name: "
                            + cleanedFileName
            );
        }

        return cleanedFileName;
    }
}