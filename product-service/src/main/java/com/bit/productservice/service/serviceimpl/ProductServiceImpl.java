package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.entity.Image;
import com.bit.productservice.entity.Product;
import com.bit.productservice.repository.ImageRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.CloudinaryService;
import com.bit.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;

    @Override
    public List<ProductDTO> getProducts() {
        List<Product> products = productRepository.findAll(Sort.by("name").ascending());
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProductByProductCode(String productCode ,Integer pageNo, Integer pageSize) {
        Page<Product> pagingProduct = productRepository.findByProductCodeStartingWith(productCode, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));
        return pagingProduct.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize) {
        Page<Product> pagingProduct = productRepository.findByBarcodeStartingWith(barcode, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));
        return pagingProduct.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByNullBarcodeWithFilter(String letter, Integer pageNo, Integer pageSize) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            switch (letter) {
                case "A" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "a%");
                }
                case "B" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "b%");
                }
                case "C-D" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "c", "d"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "C", "D")
                    );
                }
                case "E-F" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "e", "f"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "E", "F")
                    );
                }
                case "G-I" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "g", "i"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "G", "I")
                    );
                }
                case "K" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "k%");
                }
                case "L-N" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "l", "n"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "L", "N")
                    );
                }
                case "P" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "p%");
                }
                case "R-S" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "r", "s"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "R", "s")
                    );
                }
                case "Ş-T" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ş", "t"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ş", "T")
                    );
                }
                case "Ü-Z" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ü", "z"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ü", "Z")
                    );
                }
                default -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), letter + "%");
                }
            }
        };

        Page<Product> pagingProduct = productRepository.findAll(specification, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));
        return pagingProduct.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void addProduct(AddProductReq addProductReq, MultipartFile file) throws IOException {
        validateFieldsWhileAddingProduct(addProductReq);

        String id = UUID.randomUUID().toString();

        Image image = uploadImage(file, id);

        Product product = buildProduct(id, addProductReq, image);

        productRepository.save(product);
    }

    @Override
    public void updateProduct(String productId, UpdateProductReq updateProductReq, MultipartFile multipartFile) throws IOException{
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        Image image = product.getImage();

        if (image == null){
            throw new IllegalArgumentException("Image not found to be updated");
        }

        if (multipartFile != null){
            product.setImage(null);
            productRepository.save(product);

            cloudinaryService.delete(image.getImageId());
            Map result = cloudinaryService.upload(multipartFile);

            image.setName((String) result.get("original_filename"));
            image.setImageUrl((String) result.get("url"));
            image.setImageId((String) result.get("public_id"));
            imageRepository.save(image);

            product.setImage(image);
        }

        updateProductDetails(product, updateProductReq);
    }

    @Override
    public void deleteProduct(String productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalStateException("Product not found"));
        product.setDeleted(true);
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getBarcode(),
                product.getProductCode(),
                product.getName(),
                product.getImage().getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }

    private Image uploadImage(MultipartFile file, String id) throws IOException {
        Map result = cloudinaryService.upload(file);
        Image image = new Image(id,
                (String) result.get("original_filename"),
                (String) result.get("url"),
                (String) result.get("public_id"));
        return imageRepository.save(image);
    }

    private Product buildProduct(String id, AddProductReq addProductReq, Image image) {
        return Product.builder()
                .id(id)
                .barcode(addProductReq.getBarcode())
                .productCode(addProductReq.getProductCode())
                .name(addProductReq.getName())
                .price(addProductReq.getPrice())
                .image(image)
                .category(addProductReq.getCategory())
                .stockAmount(addProductReq.getStockAmount())
                .creationDate(LocalDate.now())
                .inStock(addProductReq.getStockAmount() > 0)
                .build();
    }

    private void updateProductDetails(Product product, UpdateProductReq updateProductReq){
        if (updateProductReq.getBarcode() != null){
            product.setBarcode(updateProductReq.getBarcode());
        }
        if (updateProductReq.getProductCode() != null){
            product.setProductCode(updateProductReq.getProductCode());
        }
        if (updateProductReq.getName() != null){
            product.setName(updateProductReq.getName());
        }
        if (updateProductReq.getPrice() != null){
            product.setPrice(updateProductReq.getPrice());
        }
        if (updateProductReq.getCategory() != null){
            product.setCategory(updateProductReq.getCategory());
        }
        if (updateProductReq.getStockAmount() != null){
            product.setStockAmount(updateProductReq.getStockAmount());
            product.setInStock(updateProductReq.getStockAmount() > 0);
        }
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);
    }

    private void validateFieldsWhileAddingProduct(AddProductReq addProductReq){
        if(addProductReq.getName() == null || addProductReq.getName().isEmpty()){
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if(addProductReq.getPrice() == null){
            throw new IllegalArgumentException("price cannot be null");
        }
        else{
            if(addProductReq.getPrice() < 0){
                throw new IllegalArgumentException("price cannot be negative");
            }
        }
        if(addProductReq.getCategory() == null || addProductReq.getCategory().isEmpty()){
            throw new IllegalArgumentException("category cannot be null or empty");
        }
        if(addProductReq.getStockAmount() == null){
            throw new IllegalArgumentException("stockAmount cannot be null");
        }
        else{
            if(addProductReq.getStockAmount() < 0){
                throw new IllegalArgumentException("stockAmount cannot be negative");
            }
        }
    }
}