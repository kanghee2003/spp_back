package com.shinhan.spp.service;

import com.shinhan.spp.dto.out.EditorImageUploadOutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Service
public class EditorService {

    private final Path uploadDir;
    private final String downloadUri;


    public EditorService(@Value("${app.editor.image.upload-dir}") String uploadDir, @Value("${app.editor.image.download-uri}") String downloadUri) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.downloadUri = downloadUri;
    }

    public EditorImageUploadOutDto saveEditorImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 다운로드 가능합니다.");
        }

        Files.createDirectories(uploadDir);

        String originalName = file.getOriginalFilename();
        String ext = findExt(originalName);
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path target = uploadDir.resolve(storedName).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("이미지 경로가 올바르지 않습니다.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String url = downloadUri + storedName;

        return EditorImageUploadOutDto.builder()
                .url(url)
                .originalName(originalName)
                .storedName(storedName)
                .size(file.getSize())
                .build();
    }

    public ResponseEntity<Resource> getEditorImage(String fileName) throws IOException {
        Path filePath = resolvePath(fileName);

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = toResource(filePath);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sanitizeFilename(fileName) + "\"")
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(resource);
    }

    private String findExt(String originalName) {
        if (originalName == null) return "";
        String n = originalName.trim();
        int idx = n.lastIndexOf('.');
        if (idx < 0 || idx == n.length() - 1) return "";
        String ext = n.substring(idx).toLowerCase(Locale.ROOT);
        // 너무 긴 확장자 방지
        if (ext.length() > 10) return "";
        return ext;
    }

    private Path resolvePath(String fileName) {
        Path target = uploadDir.resolve(fileName).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("이미지 경로가 올바르지 않습니다.");
        }
        return target;
    }

    private Resource toResource(Path filePath) throws MalformedURLException {
        return new UrlResource(filePath.toUri());
    }

    private String sanitizeFilename(String name) {
        return name == null ? "" : name.replaceAll("[\\r\\n\\\\\"]", "_");
    }
}
