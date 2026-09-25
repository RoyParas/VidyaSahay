package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
	List<Category> findAllById(UUID categoryIds);
}
