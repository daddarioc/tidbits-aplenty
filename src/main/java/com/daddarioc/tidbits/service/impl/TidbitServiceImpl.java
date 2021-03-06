package com.daddarioc.tidbits.service.impl;

import com.daddarioc.tidbits.domain.Tidbit;
import com.daddarioc.tidbits.repository.TidbitRepository;
import com.daddarioc.tidbits.service.TidbitQueryService;
import com.daddarioc.tidbits.service.TidbitService;
import com.daddarioc.tidbits.service.dto.TidbitCriteria;
import com.daddarioc.tidbits.service.dto.TidbitDTO;
import com.daddarioc.tidbits.service.mapper.TidbitMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service Implementation for managing Tidbit.
 */
@Service
@Transactional
public class TidbitServiceImpl implements TidbitService {

    private final Logger log = LoggerFactory.getLogger(TidbitServiceImpl.class);

    private final TidbitRepository tidbitRepository;

    private final TidbitMapper tidbitMapper;

    private final TidbitQueryService tidbitQueryService;

    public TidbitServiceImpl(TidbitRepository tidbitRepository, TidbitMapper tidbitMapper, TidbitQueryService tidbitQueryService) {
        this.tidbitRepository = tidbitRepository;
        this.tidbitMapper = tidbitMapper;
        this.tidbitQueryService = tidbitQueryService;
    }

    /**
     * Retrieve a single random tidbit
     * @return the entity
     */
    @Override
    public Optional<TidbitDTO> getRandom() {
        long tidbitCount = tidbitQueryService.countByCriteria(new TidbitCriteria());
        Optional<Tidbit> tidbit;

        do {
            long tidbitIdToRetrieve = ThreadLocalRandom.current().nextLong(1, tidbitCount + 1);
            tidbit = tidbitRepository.findById(tidbitIdToRetrieve);
        } while (!tidbit.isPresent());

        Optional<TidbitDTO> randomTidbit = Optional.of(tidbitMapper.toDto(tidbit.get()));


        return randomTidbit;
    }

    /**
     * Save a tidbit.
     *
     * @param tidbitDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TidbitDTO save(TidbitDTO tidbitDTO) {
        log.debug("Request to save Tidbit : {}", tidbitDTO);
        Tidbit tidbit = tidbitMapper.toEntity(tidbitDTO);
        tidbit = tidbitRepository.save(tidbit);
        return tidbitMapper.toDto(tidbit);
    }

    /**
     * Get all the tidbits.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TidbitDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tidbits");
        return tidbitRepository.findAll(pageable)
            .map(tidbitMapper::toDto);
    }


    /**
     * Get one tidbit by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TidbitDTO> findOne(Long id) {
        log.debug("Request to get Tidbit : {}", id);
        return tidbitRepository.findById(id)
            .map(tidbitMapper::toDto);
    }

    /**
     * Delete the tidbit by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tidbit : {}", id);
        tidbitRepository.deleteById(id);
    }
}
