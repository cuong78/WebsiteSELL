package com.example.demo.entity.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    public String name;

    @Min(value = 0)
    public float price;

    @Min(value = 0)
    public int quantity;

    @NotBlank
    public MultipartFile image;

    //PD00001
    @Pattern(regexp = "PD\\d{5}", message = "Code must be PDxxxxx!")
    @Column(unique = true)
    public String code;

    @NotNull
    public long categoryId;


}
