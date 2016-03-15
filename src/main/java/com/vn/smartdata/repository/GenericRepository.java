package com.vn.smartdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


/**
 * The Interface GenericRepository.
 *
 * @param <T> the generic type
 */
@NoRepositoryBean
public interface GenericRepository<T> extends JpaRepository<T, Long> {
    
    /** The Constant DEFAULT_PAGE. */
    public static final int DEFAULT_PAGE = 0;

    /** The Constant DEFAULT_PAGE_SIZE. */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
}