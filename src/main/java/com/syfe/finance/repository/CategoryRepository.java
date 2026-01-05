package com.syfe.finance.repository;

import com.syfe.finance.entity.Category;
import com.syfe.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user = :user")
    List<Category> findAllAvailableForUser(@Param("user") User user);
    
    @Query("SELECT c FROM Category c WHERE c.name = :name AND (c.user IS NULL OR c.user = :user)")
    Optional<Category> findByNameAndUser(@Param("name") String name, @Param("user") User user);
    
    List<Category> findByUserAndIsCustomTrue(User user);
    
    boolean existsByNameAndUser(String name, User user);
    
    boolean existsByNameAndUserIsNull(String name);
    
    List<Category> findByUserIsNull();
    
    List<Category> findByUser(User user);
}