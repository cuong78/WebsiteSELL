package com.example.demo.controller;

import com.example.demo.entity.UploadedFile;
import com.example.demo.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@SecurityRequirement(name = "api")
@Tag(name = "File Controller", description = "APIs for managing files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Upload a file")
    public ResponseEntity<UploadedFile> uploadFile(
            @RequestParam("file") MultipartFile file) {
        UploadedFile uploadedFile = fileStorageService.storeFile(file);
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Get all uploaded files")
    public ResponseEntity<List<UploadedFile>> getAllFiles() {
        return ResponseEntity.ok(fileStorageService.getAllFiles());
    }

    @GetMapping("/{fileId}")
    @Operation(summary = "Get file by ID")
    public ResponseEntity<UploadedFile> getFileById(@PathVariable String fileId) {
        return ResponseEntity.ok(fileStorageService.getFileById(fileId));
    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download a file")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        UploadedFile fileInfo = fileStorageService.getFileById(fileId);

        // Determine content type
        String contentType = fileInfo.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Delete a file")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }
}