package com.example.demo.service;

import com.example.demo.entity.UploadedFile;
import com.example.demo.exception.exceptions.FileStorageException;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.FileStorageRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    @Transactional
    public UploadedFile storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        // Chuẩn hóa tên file
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // Kiểm tra tên file hợp lệ
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        try {
            // Tạo ID duy nhất cho file
            String fileId = UUID.randomUUID().toString();

            // Tạo tên file duy nhất để tránh trùng lặp
            String fileName = fileId + "_" + originalFilename;

            // Đường dẫn tới file
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // Lưu file vào hệ thống
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Lưu thông tin file vào database
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setId(fileId);
            uploadedFile.setFileName(originalFilename); // Lưu tên gốc để hiển thị
            uploadedFile.setUploadPath(fileName); // Lưu tên file thực tế trên đĩa
            uploadedFile.setContentType(file.getContentType());
            uploadedFile.setFileSize(file.getSize());

            return fileStorageRepository.save(uploadedFile);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + originalFilename, e);
        }
    }

    public Resource loadFileAsResource(String fileId) {
        try {
            UploadedFile uploadedFile = fileStorageRepository.findById(fileId)
                    .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));

            Path filePath = this.fileStorageLocation.resolve(uploadedFile.getUploadPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found: " + uploadedFile.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("File not found", e);
        }
    }

    @Transactional(readOnly = true)
    public UploadedFile getFileById(String fileId) {
        return fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));
    }

    @Transactional(readOnly = true)
    public List<UploadedFile> getAllFiles() {
        return fileStorageRepository.findAll();
    }

    @Transactional
    public void deleteFile(String fileId) {
        UploadedFile file = fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found with id: " + fileId));

        // Kiểm tra xem file có đang được sử dụng bởi sản phẩm nào không
        if (!file.getProducts().isEmpty()) {
            throw new FileStorageException("Cannot delete file as it is being used by " +
                    file.getProducts().size() + " product(s)");
        }

        // Xóa file khỏi hệ thống
        try {
            Path filePath = this.fileStorageLocation.resolve(file.getUploadPath()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not delete file: " + file.getFileName(), e);
        }

        // Xóa bản ghi từ database
        fileStorageRepository.delete(file);
    }
}