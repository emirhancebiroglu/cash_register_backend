package com.bit.productservice.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortApplierTest {
    private SortApplier sortApplier;

    @BeforeEach
    void setUp() {
        sortApplier = new SortApplier();
    }

    @Test
    void applySortForProducts_ASC() {
        Integer pageNo = 0;
        Integer pageSize = 10;
        String sortBy = "name";
        String sortOrder = "ASC";

        Pageable pageable = sortApplier.applySortForProducts(pageNo, pageSize, sortBy, sortOrder);

        assertEquals(pageNo, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
        assertEquals(Sort.by(sortBy).ascending(), pageable.getSort());
    }

    @Test
    void applySortForProducts_DESC() {
        Integer pageNo = 1;
        Integer pageSize = 20;
        String sortBy = "price";
        String sortOrder = "DESC";

        Pageable pageable = sortApplier.applySortForProducts(pageNo, pageSize, sortBy, sortOrder);

        assertEquals(pageNo, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
        assertEquals(Sort.by(sortBy).descending(), pageable.getSort());
    }

    @Test
    void applySortForFavoriteProducts_ASC() {
        Integer pageNo = 0;
        Integer pageSize = 10;
        String sortBy = "name";
        String sortOrder = "ASC";

        Pageable pageable = sortApplier.applySortForFavoriteProducts(pageNo, pageSize, sortBy, sortOrder);

        assertEquals(pageNo, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
        assertEquals(Sort.by("product." + sortBy).ascending(), pageable.getSort());
    }

    @Test
    void applySortForFavoriteProducts_DESC() {
        Integer pageNo = 1;
        Integer pageSize = 20;
        String sortBy = "price";
        String sortOrder = "DESC";

        Pageable pageable = sortApplier.applySortForFavoriteProducts(pageNo, pageSize, sortBy, sortOrder);

        assertEquals(pageNo, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
        assertEquals(Sort.by("product." + sortBy).descending(), pageable.getSort());
    }
}
