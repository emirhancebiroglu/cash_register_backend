package com.bit.productservice.service.serviceimpl;

import com.bit.productservice.dto.ProductDTO;
import com.bit.productservice.dto.ProductInfo;
import com.bit.productservice.dto.SpecifyStockNumberReq;
import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.entity.Image;
import com.bit.productservice.entity.Product;
import com.bit.productservice.exceptions.negativefield.NegativeFieldException;
import com.bit.productservice.exceptions.nulloremptyfield.NullOrEmptyFieldException;
import com.bit.productservice.exceptions.productalreadydeleted.ProductAlreadyDeletedException;
import com.bit.productservice.exceptions.productalreadyinstocks.ProductAlreadyInStocksException;
import com.bit.productservice.exceptions.productnotfound.ProductNotFoundException;
import com.bit.productservice.repository.ImageRepository;
import com.bit.productservice.repository.ProductRepository;
import com.bit.productservice.service.CloudinaryService;
import com.bit.productservice.service.ProductService;
import com.bit.productservice.utils.SortApplier;
import com.bit.productservice.validators.ProductValidator;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for managing products.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;
    private final ProductValidator productValidator;
    private final SortApplier sortApplier;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String  STOCK_AMOUNT_CANNOT_BE_NEGATIVE = "Stock amount cannot be negative";

    @Override
    public List<ProductDTO> getProducts(Integer pageNo, Integer pageSize, String searchTerm, String lettersToFilter, String existenceStatus, String stockStatus, String sortBy, String sortOrder) {
        logger.info("Fetching products");

        // Apply pagination and sorting
        Pageable pageable = sortApplier.applySortForProducts(pageNo, pageSize, sortBy, sortOrder);

        // Define the specification for querying products
        Specification<Product> specification = Specification.where((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add search term predicate
            if (searchTerm != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%"));
            }
            // Add existence status predicate
            if (existenceStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDeleted"), existenceStatus.equalsIgnoreCase("deleted")));
            }
            // Add stock status predicate
            if (stockStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("inStock"), stockStatus.equalsIgnoreCase("inStock")));
            }
            // Add letters to filter predicate (if applicable)
            if (lettersToFilter != null) {
                predicates.add(getProductSpecification(lettersToFilter).toPredicate(root, query, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        // Query products
        Page<Product> pagingProduct = productRepository.findAll(specification, pageable);

        // Convert products to DTOs
        List<ProductDTO> products = pagingProduct.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        logger.info("Products fetched successfully");

        return products;
    }

    @Override
    public void addProduct(AddProductReq addProductReq, MultipartFile file) throws IOException {
        logger.trace("Adding product...");

        // Validate the product request and file
        List<String> errors = productValidator.validateAddProductReq(addProductReq, file);

        // Handle negative field errors
        List<String> negativeFieldErrors = errors.stream()
                .filter(error -> error.contains("cannot be negative"))
                .toList();

        if (!negativeFieldErrors.isEmpty()) {
            logger.error("Cannot be negative");
            throw new NegativeFieldException(String.join(", ", negativeFieldErrors));
        }

        // Remove negative field errors
        errors.removeAll(negativeFieldErrors);
        if (!errors.isEmpty()) {
            logger.error("Cannot be null or empty");
            throw new NullOrEmptyFieldException(String.join(", ", errors));
        }

        // Validate the product
        productValidator.validateProduct(addProductReq, null);

        // Generate a unique ID for the product
        String id = UUID.randomUUID().toString();

        // Upload the image and retrieve its details
        Image image = uploadImage(file, id);

        // Build the product entity
        Product product = buildProduct(id, addProductReq, image);
        logger.debug("Product: {}", product);

        // Save the product
        productRepository.save(product);

        logger.trace("Product added successfully");
    }

    @Override
    public void updateProduct(String productId, UpdateProductReq updateProductReq, MultipartFile multipartFile) throws IOException{
        logger.trace("Updating product...");

        // Retrieve the product by ID
        Product product = getProductById(productId);

        // Validate the update request
        productValidator.validateProduct(null, updateProductReq);

        // Check if the product is deleted
        if (product.isDeleted()){
            logger.error(PRODUCT_NOT_FOUND);
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND);
        }

        // Handle image update if a new file is provided
        Image image = product.getImage();

        if (multipartFile != null){
            // Temporarily set the product image to null before updating
            product.setImage(null);
            productRepository.save(product);

            // Delete the old image from Cloudinary
            cloudinaryService.delete(image.getImageId());

            // Upload the new image to Cloudinary
            var result = cloudinaryService.upload(multipartFile);

            image.setName(result.get("original_filename"));
            image.setImageUrl(result.get("url"));
            image.setImageId(result.get("public_id"));
            imageRepository.save(image);

            // Reassign the updated image to the product
            product.setImage(image);
        }

        // Update the product details
        updateProductDetails(product, updateProductReq);
    }

    @Override
    public void deleteProduct(String productId){
        logger.trace("Deleting product...");

        // Retrieve the product by ID
        Product product = getProductById(productId);

        // Check if the product is already deleted
        if (!product.isDeleted()){
            // Mark the product as deleted
            product.setDeleted(true);
            product.setLastUpdateDate(LocalDate.now());
            product.setStockAmount(0);
            product.setInStock(false);

            // Save the updated product state
            productRepository.save(product);

            logger.trace("Product deleted successfully");
        }
        else{
            logger.error("Product already deleted");
            throw new ProductAlreadyDeletedException("Product already deleted");
        }
    }

    @Override
    public void reAddProduct(String productId, SpecifyStockNumberReq specifyStockNumberReq) {
        logger.trace("Re-adding product...");

        // Retrieve the product by ID
        Product product = getProductById(productId);

        // Validate the specified stock number
        if (specifyStockNumberReq.getStockNumber() < 0){
            logger.error("Stock amount cannot be negative for product ID {}", productId);
            throw new NegativeFieldException(STOCK_AMOUNT_CANNOT_BE_NEGATIVE);
        }

        // Check if the product is marked as deleted
       if (product.isDeleted()){
           // Re-add the product to stock
           product.setDeleted(false);
           product.setLastUpdateDate(LocalDate.now());
           product.setStockAmount(specifyStockNumberReq.getStockNumber());
           product.setInStock(specifyStockNumberReq.getStockNumber() > 0);

           // Save the updated product
           productRepository.save(product);

           logger.trace("Product re-added successfully");
       }
       else{
           logger.error("Product already in stocks");
           throw new ProductAlreadyInStocksException("Product already in stocks");
       }
    }

    @Override
    public Mono<ProductInfo> checkProduct(String code) {
        logger.trace("Checking product...");

        return Mono.fromCallable(() -> {
            // Find the product by barcode or product code
            Product product = productRepository.findByBarcode(code);
            if (product == null) {
                product = productRepository.findByProductCode(code);
            }

            // Determine product existence and retrieve its information
            boolean exists = product != null && !product.isDeleted();
            double price = exists ? product.getPrice() : 0.0;
            int stockAmount = exists ? product.getStockAmount() : 0;
            String name = exists ? product.getName() : "";

            logger.trace("Product checked successfully.");

            // Return product information wrapped in a ProductInfo object
            return new ProductInfo(exists, name, price, stockAmount);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public void updateStocks(Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease) {
        logger.trace("Updating stocks...");

        for (Map.Entry<String, Integer> entry : productsIdWithQuantity.entrySet()) {
            String code = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = null;

            if (code != null) {
                product = productRepository.findByProductCode(code);
            }

            if (product == null) {
                product = productRepository.findByBarcode(code);
            }

            if (product == null) {
                logger.error(PRODUCT_NOT_FOUND);
                throw new ProductNotFoundException(PRODUCT_NOT_FOUND);
            }

            int newStock;
            if (shouldDecrease) {
                newStock = product.getStockAmount() - quantity;
            } else {
                newStock = product.getStockAmount() + quantity;
            }

            product.setStockAmount(newStock);
            product.setInStock(product.getStockAmount() != 0);

            productRepository.save(product);

            logger.trace("Product stock updated successfully");
        }
    }

    /**
     * Converts a Product entity into a ProductDTO.
     *
     * @param product The Product entity to be converted.
     * @return The corresponding ProductDTO.
     */
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

    /**
     * Uploads an image to the cloud storage service and saves image information to the repository.
     *
     * @param file The image file to be uploaded.
     * @param id   The ID of the image.
     * @return The uploaded Image entity.
     * @throws IOException If an I/O error occurs during the upload process.
     */
    private Image uploadImage(MultipartFile file, String id) throws IOException {
        var result = cloudinaryService.upload(file);

        logger.trace(("Saving image to repository..."));

        Image image = new Image(id,
                (result.get("original_filename")),
                (result.get("url")),
                (result.get("public_id")));

        imageRepository.save(image);

        logger.trace(("Image saved to repository"));

        return image;
    }

    /**
     * Builds a Product entity based on the provided information.
     *
     * @param id            The ID of the product.
     * @param addProductReq The request object containing product details.
     * @param image         The image associated with the product.
     * @return The built Product entity.
     */
    private Product buildProduct(String id, AddProductReq addProductReq, Image image) {
        logger.trace("Building product with ID {}", id);

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

        logger.trace("Product built successfully with ID {}", id);

        return product;
    }

    /**
     * Updates the details of a Product entity.
     *
     * @param product          The Product entity to be updated.
     * @param updateProductReq The request object containing updated product details.
     */
    private void updateProductDetails(Product product, UpdateProductReq updateProductReq){
        if (updateProductReq.getBarcode() != null){
            product.setBarcode(updateProductReq.getBarcode());

            if (product.getProductCode() != null){
                product.setProductCode(null);
            }
        }
        if (updateProductReq.getProductCode() != null){
            product.setProductCode(updateProductReq.getProductCode());

            if (product.getBarcode() != null){
                product.setBarcode(null);
            }
        }
        if (updateProductReq.getName() != null){
            product.setName(updateProductReq.getName());
        }
        if (updateProductReq.getPrice() != null){
            if (updateProductReq.getPrice() < 0) {
                logger.error("Price cannot be negative");
                throw new NegativeFieldException("Price cannot be negative");
            }
            product.setPrice(updateProductReq.getPrice());
        }
        if (updateProductReq.getCategory() != null){
            product.setCategory(updateProductReq.getCategory());
        }
        if (updateProductReq.getStockAmount() != null){
            if (updateProductReq.getStockAmount() < 0) {
                logger.error(STOCK_AMOUNT_CANNOT_BE_NEGATIVE);
                throw new NegativeFieldException(STOCK_AMOUNT_CANNOT_BE_NEGATIVE);
            }
            product.setStockAmount(updateProductReq.getStockAmount());
            product.setInStock(updateProductReq.getStockAmount() > 0);
        }
        product.setLastUpdateDate(LocalDate.now());

        productRepository.save(product);

        logger.trace("Product details updated successfully for product ID {}", product.getId());
    }

    /**
     * Retrieves a Product entity by its ID.
     *
     * @param productId The ID of the product to be retrieved.
     * @return The retrieved Product entity.
     * @throws ProductNotFoundException If the product with the specified ID is not found.
     */
    private Product getProductById(String productId) {
        logger.trace("Fetching product with ID {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Product with ID {} not found", productId);
                    return new ProductNotFoundException(PRODUCT_NOT_FOUND);
                });

        logger.trace("Product fetched successfully with ID {}", productId);

        return product;
    }

    /**
     * Constructs a Specification for filtering products based on a given letter.
     *
     * @param letter The letter to filter products by.
     * @return A Specification that can be used to filter products based on the given letter.
     */
    private static Specification<Product> getProductSpecification(String letter) {
        return (root, query, criteriaBuilder) -> {
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
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "C", "D"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "c%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "d%")
                    );
                }
                case "E-F" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "e", "f"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "E", "F"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "e%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "f%")
                    );
                }
                case "G-I" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "g", "i"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "G", "I"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "g%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "i%")
                    );
                }
                case "K" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "k%");
                }
                case "L-N" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "l", "n"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "L", "N"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "l%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "n%")
                    );
                }
                case "P" -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "p%");
                }
                case "R-S" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "r", "s"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "R", "s"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "r%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "s%")
                    );
                }
                case "Ş-T" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ş", "t"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ş", "T"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "ş%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "t%")
                    );
                }
                case "Ü-Z" -> {
                    return criteriaBuilder.or(
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "ü", "z"),
                            criteriaBuilder.between(criteriaBuilder.lower(root.get("name")), "Ü", "Z"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "ü%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "z%")
                    );
                }
                default -> {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), letter + "%");
                }
            }
        };
    }
}