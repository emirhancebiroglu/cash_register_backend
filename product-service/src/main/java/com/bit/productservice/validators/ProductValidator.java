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

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;

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

        return errors;
    }

    public void validateProduct(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        validateProductName(addProductReq, updateProductReq);
        validateProductCodeAndBarcode(addProductReq, updateProductReq);
    }

    private void validateProductName(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        if ((updateProductReq != null && productRepository.existsByName(updateProductReq.getName())) ||
                (addProductReq != null && productRepository.existsByName(addProductReq.getName()))) {
            throw new ProductWithSameNameException("A product with the same name already exists");
        }
    }

    private void validateProductCodeAndBarcode(AddProductReq addProductReq, UpdateProductReq updateProductReq) {
        if (bothCodeAndBarcodeProvided(updateProductReq, addProductReq)) {
            throw new BothCodeTypeProvidedException("Either barcode or product code should be provided, not both");
        }

        if (neitherCodeNorBarcodeProvided(updateProductReq, addProductReq)) {
            throw new NoCodeProvidedException("Either barcode or product code should be provided");
        }

        if (duplicateProductCodeExists(updateProductReq, addProductReq)) {
            throw new ProductWithSameProductCodeException("A product with the same product code already exists");
        }

        if (duplicateBarcodeExists(updateProductReq, addProductReq)) {
            throw new ProductWithSameBarcodeException("A product with the same barcode already exists");
        }
    }

    private boolean bothCodeAndBarcodeProvided(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getProductCode()) && isNotBlank(updateProductReq.getBarcode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getProductCode()) && isNotBlank(addProductReq.getBarcode()));
    }

    private boolean neitherCodeNorBarcodeProvided(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq == null || (isBlank(updateProductReq.getProductCode()) && isBlank(updateProductReq.getBarcode()))) &&
                (addProductReq == null || (isBlank(addProductReq.getProductCode()) && isBlank(addProductReq.getBarcode())));
    }

    private boolean duplicateProductCodeExists(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getProductCode()) && productRepository.existsByProductCode(updateProductReq.getProductCode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getProductCode()) && productRepository.existsByProductCode(addProductReq.getProductCode()));
    }

    private boolean duplicateBarcodeExists(UpdateProductReq updateProductReq, AddProductReq addProductReq) {
        return (updateProductReq != null && isNotBlank(updateProductReq.getBarcode()) && productRepository.existsByBarcode(updateProductReq.getBarcode())) ||
                (addProductReq != null && isNotBlank(addProductReq.getBarcode()) && productRepository.existsByBarcode(addProductReq.getBarcode()));
    }

    private boolean isNotBlank(String str) {
        return str != null && !str.isEmpty();
    }

    private boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }
}
