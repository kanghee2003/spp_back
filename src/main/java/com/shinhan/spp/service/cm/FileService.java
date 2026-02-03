package com.shinhan.spp.service.cm;

import com.shinhan.spp.dto.cm.out.FileUploadItemOutDto;
import com.shinhan.spp.dto.cm.out.FileUploadOutDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileService {

    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    private final Path uploadRootDir;

    private final Map<String, FileMeta> metaStore = new ConcurrentHashMap<>();

    public FileService(@Value("${app.sample.file.upload-dir}") String uploadDir) {
        this.uploadRootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public FileUploadOutDto saveFiles(List<MultipartFile> files, String title, Integer seq) throws IOException {
        if (files == null || files.isEmpty() || files.stream().allMatch(f -> f == null || f.isEmpty())) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        Files.createDirectories(uploadRootDir);

        String ym = YearMonth.now().format(YM_FMT);
        Path ymDir = uploadRootDir.resolve(ym).normalize();
        if (!ymDir.startsWith(uploadRootDir)) {
            throw new IllegalArgumentException("업로드 경로가 올바르지 않습니다.");
        }
        Files.createDirectories(ymDir);

        List<FileUploadItemOutDto> saved = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            saved.add(saveOne(file, ym, ymDir));
        }

        return FileUploadOutDto.builder()
                .title(title)
                .seq(seq)
                .fileCount(saved.size())
                .files(saved)
                .build();
    }

    private FileUploadItemOutDto saveOne(MultipartFile file, String ym, Path ymDir) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = findExt(originalName);

        String savedFileName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path target = ymDir.resolve(savedFileName).normalize();
        if (!target.startsWith(ymDir)) {
            throw new IllegalArgumentException("파일 경로가 올바르지 않습니다.");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String fileId = UUID.randomUUID().toString().replace("-", "");

        String filePath = ym;

        metaStore.put(fileId, new FileMeta(filePath, savedFileName, originalName));

        return FileUploadItemOutDto.builder()
                .fileId(fileId)
                .originalName(originalName)
                .storedName(savedFileName)
                .size(file.getSize())
                .build();
    }


    public ResponseEntity<Resource> getFile(String fileId) throws IOException {
        if (fileId == null || fileId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        FileMeta meta = metaStore.get(fileId);
        if (meta == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = resolvePath(meta.filePath, meta.savedFileName);

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = toResource(filePath);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);

        String downloadName = (meta.orgFileName != null && !meta.orgFileName.isBlank())
                ? meta.orgFileName
                : meta.savedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, buildContentDisposition(downloadName))
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(resource);
    }

    private Path resolvePath(String filePathFromDb, String savedFileName) {
        if (savedFileName == null || savedFileName.isBlank()) {
            throw new IllegalArgumentException("SAVED_FILE_NAME이 없습니다.");
        }

        String relDir = (filePathFromDb == null) ? "" : filePathFromDb.trim().replace("\\", "/");
        while (relDir.startsWith("/")) relDir = relDir.substring(1);

        Path dir = relDir.isEmpty()
                ? uploadRootDir
                : uploadRootDir.resolve(relDir).normalize();

        if (!dir.startsWith(uploadRootDir)) {
            throw new IllegalArgumentException("FILE_PATH가 올바르지 않습니다.");
        }

        Path target = dir.resolve(savedFileName).normalize();

        if (!target.startsWith(dir)) {
            throw new IllegalArgumentException("파일 경로가 올바르지 않습니다.");
        }

        return target;
    }

    private String findExt(String originalName) {
        if (originalName == null) return "";
        String n = originalName.trim();
        int idx = n.lastIndexOf('.');
        if (idx < 0 || idx == n.length() - 1) return "";
        String ext = n.substring(idx).toLowerCase(Locale.ROOT);
        if (ext.length() > 10) return "";
        return ext;
    }

    private Resource toResource(Path filePath) throws MalformedURLException {
        return new UrlResource(filePath.toUri());
    }

    private String buildContentDisposition(String filename) {
        String safe = sanitizeFilename(filename);
        String ascii = safe.replaceAll("[^\\x20-\\x7E]", "_");

        String encoded = URLEncoder.encode(safe, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + encoded;
    }

    private String sanitizeFilename(String name) {
        return name == null ? "" : name.replaceAll("[\\r\\n\\\\\"]", "_");
    }

    private static class FileMeta {
        final String filePath;
        final String savedFileName;
        final String orgFileName;

        FileMeta(String filePath, String savedFileName, String orgFileName) {
            this.filePath = filePath;
            this.savedFileName = savedFileName;
            this.orgFileName = orgFileName;
        }
    }
}
