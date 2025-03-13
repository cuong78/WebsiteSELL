package com.example.demo.repository;

import com.example.demo.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileStorageRepository extends JpaRepository<UploadedFile, String> {
    Optional<UploadedFile> findByFileName(String fileName);
}
