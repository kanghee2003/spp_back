package com.shinhan.spp.service.cm;

import com.shinhan.spp.dto.cm.out.EditorImageUploadOutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class EditorService {
    private static final DateTimeFormatter FORMAT_YM = DateTimeFormatter.ofPattern("yyyyMM");
    private static final Pattern RELATIVE_PATH_PATTERN = Pattern.compile("^\\d{6}/[0-9a-fA-F]{32}(\\.[a-zA-Z0-9]{1,10})?$");

    private final Path uploadDir;
    private final String downloadUri;

    public EditorService(
            @Value("${app.editor.image.upload-dir}") String uploadDir,
            @Value("${app.editor.image.download-uri}") String downloadUri
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.downloadUri = normalizeBaseUri(downloadUri);
    }

    public EditorImageUploadOutDto saveEditorImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 다운로드 가능합니다.");
        }

        String ym = LocalDate.now().format(FORMAT_YM);
        Path uploadTargetDir = uploadDir.resolve(ym).normalize();

        Files.createDirectories(uploadTargetDir);

        String originalName = file.getOriginalFilename();
        String ext = findExt(originalName);
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path target = uploadTargetDir.resolve(storedName).normalize();
        if (!target.startsWith(uploadDir)) {
            throw new IllegalArgumentException("이미지 경로가 올바르지 않습니다.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = ym + "/" + storedName;
        String url = downloadUri + relativePath;

        return EditorImageUploadOutDto.builder()
                .url(url)
                .originalName(originalName)
                .storedName(storedName)
                .size(file.getSize())
                .build();
    }

    public ResponseEntity<Resource> getEditorImage(String relativePath) throws IOException {
        Path filePath = resolvePath(relativePath);

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = toResource(filePath);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);

        String onlyName = filePath.getFileName() != null ? filePath.getFileName().toString() : relativePath;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sanitizeFilename(onlyName) + "\"")
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
        // 확장자에 이상한 문자 섞인 경우 차단 (점 포함)
        if (!ext.matches("^\\.[a-z0-9]{1,10}$")) return "";
        return ext;
    }

    private Path resolvePath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("이미지 경로가 올바르지 않습니다.");
        }

        String normalized = relativePath.replace('\\', '/');

        if (!RELATIVE_PATH_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("이미지 경로가 올바르지 않습니다.");
        }

        Path target = uploadDir.resolve(normalized).normalize();
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

    private String normalizeBaseUri(String uri) {
        if (uri == null) return "";
        String u = uri.trim();
        return u.endsWith("/") ? u : u + "/";
    }
}
