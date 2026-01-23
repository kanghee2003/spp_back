package com.shinhan.spp.controller;

import com.shinhan.spp.dto.out.EditorImageUploadOutDto;
import com.shinhan.spp.model.ApiResponse;
import com.shinhan.spp.service.EditorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cm/editor")
public class EditorController {

    private final EditorService editorService;

    @PostMapping("/image/upload")
    public ApiResponse<EditorImageUploadOutDto> uploadEditorImage(@RequestParam("file") MultipartFile file) throws Exception {
        return ApiResponse.ok(editorService.saveEditorImage(file));
    }


    @GetMapping({"/image/download/{fileName:.+}"})
    public ResponseEntity<Resource> getEditorImage(@PathVariable("fileName") String fileName) throws IOException {
       return editorService.getEditorImage(fileName);
    }
}
