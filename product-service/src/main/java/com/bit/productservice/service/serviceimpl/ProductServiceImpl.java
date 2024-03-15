package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.entity.Image;
import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.negativefield.NegativeFieldException;
import com.bit.productservice.exceptions.nulloremptyfield.NullOrEmptyFieldException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.ImageRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.CloudinaryService;
import com.bit.productservice.service.ProductService;
import com.bit.productservice.validators.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;
    private final ProductValidator productValidator;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public List<ProductDTO> getProducts() {
        logger.info("Fetching all products");

        List<Product> products = productRepository.findAll(Sort.by("name").ascending());

        logger.info("Products fetched successfully");

        return products.stream()
                .filter(product -> !product.isDeleted())
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> searchProductByProductCode(String productCode ,Integer pageNo, Integer pageSize) {
        logger.info("Fetching product with code {} ", productCode);

        Page<Product> pagingProduct = productRepository.findByProductCodeStartingWith(productCode, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));

        logger.info("Product fetched successfully");

        return pagingProduct.stream()
                .filter(product -> !product.isDeleted())
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> searchProductByBarcode(String barcode, Integer pageNo, Integer pageSize) {
        logger.info("Fetching product with code {} ", barcode);

        Page<Product> pagingProduct = productRepository.findByBarcodeStartingWith(barcode, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));

        logger.info("Product fetched successfully");

        return pagingProduct.stream()
                .filter(product -> !product.isDeleted())
                .map(this::convertToDTO)
                .toList();
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

        logger.info("Products filtering by criteria");

        Page<Product> pagingProduct = productRepository.findAll(specification, PageRequest.of(pageNo, pageSize, Sort.by("name").ascending()));

        logger.info("Products filtered successfully");

        return pagingProduct.getContent().stream()
                .filter(product -> !product.isDeleted())
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public void addProduct(AddProductReq addProductReq, MultipartFile file) throws IOException {
        logger.info("Adding product...");

        List<String> errors = productValidator.validateAddProductReq(addProductReq, file);

        List<String> negativeFieldErrors = errors.stream()
                .filter(error -> error.contains("cannot be negative"))
                .toList();

        if (!negativeFieldErrors.isEmpty()) {
            throw new NegativeFieldException(String.join(", ", negativeFieldErrors));
        }

        errors.removeAll(negativeFieldErrors);
        if (!errors.isEmpty()) {
            throw new NullOrEmptyFieldException(String.join(", ", errors));
        }

        productValidator.validateProduct(addProductReq, null);

        String id = UUID.randomUUID().toString();

        Image image = uploadImage(file, id);

        Product product = buildProduct(id, addProductReq, image);

        productRepository.save(product);

        logger.info("Product added successfully");
    }

    @Override
    public void updateProduct(String productId, UpdateProductReq updateProductReq, MultipartFile multipartFile) throws IOException{
        logger.info("Updating product...");

        Product product = getProductById(productId);
        productValidator.validateProduct(null, updateProductReq);

        Image image = product.getImage();

        if (multipartFile != null){
            product.setImage(null);
            productRepository.save(product);

            cloudinaryService.delete(image.getImageId());
            var result = cloudinaryService.upload(multipartFile);

            image.setName(result.get("original_filename"));
            image.setImageUrl(result.get("url"));
            image.setImageId(result.get("public_id"));
            imageRepository.save(image);

            product.setImage(image);
        }

        updateProductDetails(product, updateProductReq);
    }

    @Override
    public void deleteProduct(String productId){
        logger.info("Deleting product...");

        Product product = getProductById(productId);

        product.setDeleted(true);
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);

        logger.info("Product deleted successfully");
    }

    @Override
    public void reAddProduct(String productId) {
        logger.info("Re-adding product...");

        Product product = getProductById(productId);
        product.setDeleted(false);
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);

        logger.info("Product re-added successfully");
    }

    private ProductDTO convertToDTO(Product product) {
        String code = product.getProductCode() != null ? product.getProductCode() : product.getBarcode();
        return new ProductDTO(
                code,
                product.getName(),
                product.getImage().getImageUrl(),
                product.getPrice(),
                product.getCategory()
        );
    }

    private Image uploadImage(MultipartFile file, String id) throws IOException {
        var result = cloudinaryService.upload(file);

        logger.info(("Saving image to repository..."));

        Image image = new Image(id,
                (result.get("original_filename")),
                (result.get("url")),
                (result.get("public_id")));

        imageRepository.save(image);

        logger.info(("Image saved to repository"));

        return image;
    }

    private Product buildProduct(String id, AddProductReq addProductReq, Image image) {
        logger.info("Building product with ID {}", id);

        Product product = new Product(
                id,
                addProductReq.getBarcode(),
                addProductReq.getProductCode(),
                addProductReq.getName(),
                addProductReq.getPrice(),
                image,
                addProductReq.getCategory(),
                addProductReq.getStockAmount(),
                LocalDate.now(),
                addProductReq.getStockAmount() > 0
        );

        logger.info("Product built successfully with ID {}", id);

        return product;
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
            if (updateProductReq.getPrice() < 0) {
                throw new NegativeFieldException("Price cannot be negative");
            }
            product.setPrice(updateProductReq.getPrice());
        }
        if (updateProductReq.getCategory() != null){
            product.setCategory(updateProductReq.getCategory());
        }
        if (updateProductReq.getStockAmount() != null){
            if (updateProductReq.getStockAmount() < 0) {
                throw new NegativeFieldException("Stock amount cannot be negative");
            }
            product.setStockAmount(updateProductReq.getStockAmount());
            product.setInStock(updateProductReq.getStockAmount() > 0);
        }
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);

        logger.info("Product details updated successfully for product ID {}", product.getId());
    }

    private Product getProductById(String productId) {
        logger.info("Fetching product with ID {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        logger.info("Product fetched successfully with ID {}", productId);

        return product;
    }
}