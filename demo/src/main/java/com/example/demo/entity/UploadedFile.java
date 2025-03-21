package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uploaded_files")
public class UploadedFile {

    @Id
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    private Long fileSize;

    @Column(name = "upload_path", nullable = false)
    private String uploadPath;

    @CreationTimestamp
    private LocalDateTime uploadDate;

    // Tham chiếu ngược để dễ dàng tìm tất cả Product sử dụng file này
    @OneToMany(mappedBy = "imageFile")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();
}