package com.bit.productservice.validators;

import com.bit.productservice.dto.addproduct.AddProductReq;
import com.bit.productservice.dto.updateproduct.UpdateProductReq;
import com.bit.productservice.exceptions.bothcodetypeprovided.BothCodeTypeProvidedException;
import com.bit.productservice.exceptions.nocodeprovided.NoCodeProvidedException;
import com.bit.productservice.exceptions.productwithsamebarcode.ProductWithSameBarcodeException;
import com.bit.productservice.exceptions.productwithsamename.ProductWithSameNameException;
import com.bit.productservice.exceptions.productwithsameproductcode.ProductWithSameProductCodeException;
import com.bit.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator class for product-related operations.
 */
@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;

    /**
     * Validates the fields of the AddProductReq object.
     *
     * @param addProductReq the AddProductReq object to validate
     * @param file          the image file associated with the product
     * @return a list of validation errors
     */
    public List<String> validateAddProductReq(AddProductReq addProductReq, MultipartFile file) {
        List<String> errors = new ArrayList<>();

        if (addProductReq.getName() == null || addProductReq.getName().isEmpty()) {
            errors.add("Name field is required");
        }

        if (addProductReq.getPrice() == null) {
            errors.add("Price field is required");
        }
        else if(addProductReq.getPrice() < 0){
            errors.add("Price field cannot be negative");
        }

        if (addProductReq.getCategory() == null || addProductReq.getCategory().isEmpty()) {
            errors.add("Category field is required");
        }

        if (addProductReq.getStockAmount() == null) {
            errors.add("Stock field is required");
        }
        else if(addProductReq.getStockAmount() < 0){
            errors.add("Stock field cannot be negative");
        }

        if (file == null || file.isEmpty()) {
            errors.add("Image field is required");
        }

        if (neitherCodeNorBarcodeProvided(addProductReq)) {
            throw new NoCodeProvidedException("Either barcode or product code should be provided");
        }

        return errors;
    }

    /**
     * Validates the product fields.
     *
     * @param addProductReq    the AddProductReq object
     * @param updateProductReq the UpdateProductReq object
     */
    public void validateProduct(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        validateProductName(addProductReq, updateProductReq);
        validateProductCodeAndBarcode(addProductReq, updateProductReq);
    }

    /**
     * Validates the product name to ensure uniqueness.
     *
     * @param addProductReq    the AddProductReq object
     * @param updateProductReq the UpdateProductReq object
     */
    public void validateProductName(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        if ((updateProductReq != null && productRepository.existsByName(updateProductReq.getName())) ||
                (addProductReq != null && productRepository.existsByName(addProductReq.getName()))) {
            throw new ProductWithSameNameException("A product with the same name already exists");
        }
    }

    /**
     * Validates the product code and barcode to ensure they are provided correctly.
     *
     * @param addProductReq    the AddProductReq object
     * @param updateProductReq the UpdateProductReq object
     */
    private void validateProductCodeAndBarcode(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        if (bothCodeAndBarcodeProvided(updateProductReq, addProductReq)) {
            throw new BothCodeTypeProvidedException("Either barcode or product code should be provided, not both");
        }

        if (duplicateProductCodeExists(updateProductReq, addProductReq)) {
            throw new ProductWithSameProductCodeException("A product with the same product code already exists");
        }

        if (duplicateBarcodeExists(updateProductReq, addProductReq)) {
            throw new ProductWithSameBarcodeException("A product with the same barcode already exists");
        }
    }

    /**
     * Checks if both product code and barcode are provided.
     *
     * @param updateProductReq the UpdateProductReq object
     * @param addProductReq    the AddProductReq object
     * @return true if both product code and barcode are provided, false otherwise
     */
    private boolean bothCodeAndBarcodeProvided(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getProductCode()) && isNotBlank(updateProductReq.getBarcode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getProductCode()) && isNotBlank(addProductReq.getBarcode()));
    }

    /**
     * Checks if neither product code nor barcode are provided.
     *
     * @param addProductReq    the AddProductReq object
     * @return true if neither product code nor barcode are provided, false otherwise
     */
    private boolean neitherCodeNorBarcodeProvided(AddProductReq addProductReq) {
        return (addProductReq == null || (isBlank(addProductReq.getProductCode()) && isBlank(addProductReq.getBarcode())));
    }

    /**
     * Checks if a duplicate product code exists.
     *
     * @param updateProductReq the UpdateProductReq object
     * @param addProductReq    the AddProductReq object
     * @return true if a duplicate product code exists, false otherwise
     */
    private boolean duplicateProductCodeExists(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getProductCode()) && productRepository.existsByProductCode(updateProductReq.getProductCode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getProductCode()) && productRepository.existsByProductCode(addProductReq.getProductCode()));
    }

    /**
     * Checks if a duplicate barcode exists.
     *
     * @param updateProductReq the UpdateProductReq object
     * @param addProductReq    the AddProductReq object
     * @return true if a duplicate barcode exists, false otherwise
     */
    private boolean duplicateBarcodeExists(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getBarcode()) && productRepository.existsByBarcode(updateProductReq.getBarcode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getBarcode()) && productRepository.existsByBarcode(addProductReq.getBarcode()));
    }

    /**
     * Checks if a string is not blank.
     *
     * @param str the string to check
     * @return true if the string is not blank, false otherwise
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Checks if a string is blank.
     *
     * @param str the string to check
     * @return true if the string is blank, false otherwise
     */
    private boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }
}
