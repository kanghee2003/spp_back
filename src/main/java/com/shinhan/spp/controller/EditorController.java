package com.shinhan.spp.controller;

import com.shinhan.spp.dto.out.EditorImageUploadOutDto;
import com.shinhan.spp.model.ApiResponse;
import com.shinhan.spp.service.EditorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Tag(name = "EditorController", description = "Editor 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cm/editor")
public class EditorController {

    private final EditorService editorService;

    @Operation(summary = "Editor 이미지 업로드", description = "Editor 이미지 업로드")
    @PostMapping("/image/upload")
    public EditorImageUploadOutDto uploadEditorImage(@RequestParam("file") MultipartFile file) throws Exception {
        return editorService.saveEditorImage(file);
    }

    @Operation(summary = "Editor 이미지 보기", description = "Editor 이미지 보기")
    @GetMapping({"/image/download/{fileName:.+}"})
    public ResponseEntity<Resource> getEditorImage(@PathVariable("fileName") String fileName) throws IOException {
       return editorService.getEditorImage(fileName);
    }
}
