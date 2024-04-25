package com.bit.productservice.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * A utility class for applying sorting to product and favorite product pages.
 */
@Component
public class SortApplier {

    /**
     * Apply sorting for products.
     *
     * @param pageNo the page number to retrieve
     * @param pageSize the number of products per page
     * @param sortBy the field to sort by
     * @param sortOrder the sorting order (ASC or DESC)
     * @return a {@link Pageable} object representing the sorting and pagination parameters
     */
    public Pageable applySortForProducts(Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }

    /**
     * Apply sorting for favorite products.
     *
     * @param pageNo the page number to retrieve
     * @param pageSize the number of favorite products per page
     * @param sortBy the field to sort by
     * @param sortOrder the sorting order (ASC or DESC)
     * @return a {@link Pageable} object representing the sorting and pagination parameters
     */
    public Pageable applySortForFavoriteProducts(Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {
        String productSortByPath = "product." + sortBy;
        Sort sort = sortOrder.equalsIgnoreCase("ASC") ? Sort.by(productSortByPath).ascending() : Sort.by(productSortByPath).descending();
        return PageRequest.of(pageNo, pageSize, sort);
    }
}
