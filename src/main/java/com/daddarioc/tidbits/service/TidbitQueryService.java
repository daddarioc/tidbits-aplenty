package com.daddarioc.tidbits.service;

import com.daddarioc.tidbits.domain.Category_;
import com.daddarioc.tidbits.domain.Tidbit;
import com.daddarioc.tidbits.domain.Tidbit_;
import com.daddarioc.tidbits.repository.TidbitRepository;
import com.daddarioc.tidbits.service.dto.TidbitCriteria;
import com.daddarioc.tidbits.service.dto.TidbitDTO;
import com.daddarioc.tidbits.service.mapper.TidbitMapper;
import io.github.jhipster.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for Tidbit entities in the database.
 * The main input is a {@link TidbitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TidbitDTO} or a {@link Page} of {@link TidbitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TidbitQueryService extends QueryService<Tidbit> {

    private final Logger log = LoggerFactory.getLogger(TidbitQueryService.class);

    private final TidbitRepository tidbitRepository;

    private final TidbitMapper tidbitMapper;

    public TidbitQueryService(TidbitRepository tidbitRepository, TidbitMapper tidbitMapper) {
        this.tidbitRepository = tidbitRepository;
        this.tidbitMapper = tidbitMapper;
    }

    /**
     * Return a {@link List} of {@link TidbitDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TidbitDTO> findByCriteria(TidbitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Tidbit> specification = createSpecification(criteria);
        return tidbitMapper.toDto(tidbitRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TidbitDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TidbitDTO> findByCriteria(TidbitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tidbit> specification = createSpecification(criteria);
        return tidbitRepository.findAll(specification, page)
            .map(tidbitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TidbitCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Tidbit> specification = createSpecification(criteria);
        return tidbitRepository.count(specification);
    }

    /**
     * Function to convert TidbitCriteria to a {@link Specification}
     */
    private Specification<Tidbit> createSpecification(TidbitCriteria criteria) {
        Specification<Tidbit> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Tidbit_.id));
            }
            if (criteria.getContent() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContent(), Tidbit_.content));
            }
            if (criteria.getAuthor() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAuthor(), Tidbit_.author));
            }
            if (criteria.getSource() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSource(), Tidbit_.source));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Tidbit_.url));
            }
            if (criteria.getCategoryId() != null) {
                specification = specification.and(buildSpecification(criteria.getCategoryId(),
                    root -> root.join(Tidbit_.category, JoinType.LEFT).get(Category_.id)));
            }
        }
        return specification;
    }
}
