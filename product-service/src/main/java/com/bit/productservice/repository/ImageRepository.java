package com.bit.productservice.repository;

import com.bit.productservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 Repository interface for managing images.
 Extends JpaRepository for CRUD operations on Image entities.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image,String> {
}
