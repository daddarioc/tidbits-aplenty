package com.daddarioc.tidbits.service;

import com.daddarioc.tidbits.service.dto.TidbitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Tidbit.
 */
public interface TidbitService {

    /**
     * Save a tidbit.
     *
     * @param tidbitDTO the entity to save
     * @return the persisted entity
     */
    TidbitDTO save(TidbitDTO tidbitDTO);

    /**
     * Get all the tidbits.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TidbitDTO> findAll(Pageable pageable);


    /**
     * Get the "id" tidbit.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TidbitDTO> findOne(Long id);

    /**
     * Delete the "id" tidbit.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
