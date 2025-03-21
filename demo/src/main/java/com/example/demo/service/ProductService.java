package com.example.demo.service;

import com.example.demo.entity.Brand;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.UploadedFile;
import com.example.demo.dto.request.ProductRequest;
import com.example.demo.exception.exceptions.NotFoundException;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.FileStorageRepository;
import com.example.demo.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findProductsByIsDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsPaginated(Pageable pageable) {
        return productRepository.findByIsDeletedFalse(pageable);
    }

    @Transactional
    public Product createProduct(ProductRequest productRequest, MultipartFile imageFile) {
        // Validate ảnh
        if ((imageFile == null || imageFile.isEmpty()) && productRequest.getExistingFileId() == null) {
            throw new IllegalArgumentException("Product image is required");
        }

        Product product = modelMapper.map(productRequest, Product.class);

        // Xử lý ảnh
        UploadedFile uploadedFile = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            uploadedFile = fileStorageService.storeFile(imageFile);
        } else if (productRequest.getExistingFileId() != null) {
            uploadedFile = fileStorageRepository.findById(productRequest.getExistingFileId())
                    .orElseThrow(() -> new NotFoundException("File not found with id: " + productRequest.getExistingFileId()));
        }
        product.setImageFile(uploadedFile);

        // Xử lý danh mục và thương hiệu
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        product.setCategory(category);

        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));
        product.setBrand(brand);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        // Cập nhật thông tin cơ bản
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        product.setDiscountPercentage(request.getDiscountPercentage());

        // Cập nhật danh mục nếu có
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Cập nhật thương hiệu nếu có
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found with id: " + request.getBrandId()));
            product.setBrand(brand);
        }

        // Cập nhật hình ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            // Tải lên file mới
            UploadedFile uploadedFile = fileStorageService.storeFile(imageFile);
            product.setImageFile(uploadedFile);
        } else if (request.getExistingFileId() != null) {
            // Sử dụng file đã có
            UploadedFile existingFile = fileStorageRepository.findById(request.getExistingFileId())
                    .orElseThrow(() -> new NotFoundException("File not found with id: " + request.getExistingFileId()));
            product.setImageFile(existingFile);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product softDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        product.setIsDeleted(true);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword, Long categoryId, Long brandId) {
        if (keyword == null && categoryId == null && brandId == null) {
            return productRepository.findByIsDeletedFalse();
        }
        return productRepository.searchProducts(keyword, categoryId, brandId);
    }

    @Transactional
    public Product updateDiscount(Long productId, Float discountPercentage) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        product.setDiscountPercentage(discountPercentage);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }
}