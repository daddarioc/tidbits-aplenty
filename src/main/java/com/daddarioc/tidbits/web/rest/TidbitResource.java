package com.daddarioc.tidbits.web.rest;

import com.daddarioc.tidbits.service.TidbitQueryService;
import com.daddarioc.tidbits.service.TidbitService;
import com.daddarioc.tidbits.service.dto.TidbitCriteria;
import com.daddarioc.tidbits.service.dto.TidbitDTO;
import com.daddarioc.tidbits.web.rest.errors.BadRequestAlertException;
import com.daddarioc.tidbits.web.rest.util.HeaderUtil;
import com.daddarioc.tidbits.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Tidbit.
 */
@RestController
@RequestMapping("/api")
public class TidbitResource {

    private final Logger log = LoggerFactory.getLogger(TidbitResource.class);

    private static final String ENTITY_NAME = "tidbit";

    private final TidbitService tidbitService;

    private final TidbitQueryService tidbitQueryService;

    public TidbitResource(TidbitService tidbitService, TidbitQueryService tidbitQueryService) {
        this.tidbitService = tidbitService;
        this.tidbitQueryService = tidbitQueryService;
    }

    /**
     * POST  /tidbits : Create a new tidbit.
     *
     * @param tidbitDTO the tidbitDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tidbitDTO, or with status 400 (Bad Request) if the tidbit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tidbits")
    public ResponseEntity<TidbitDTO> createTidbit(@Valid @RequestBody TidbitDTO tidbitDTO) throws URISyntaxException {
        log.debug("REST request to save Tidbit : {}", tidbitDTO);
        if (tidbitDTO.getId() != null) {
            throw new BadRequestAlertException("A new tidbit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TidbitDTO result = tidbitService.save(tidbitDTO);
        return ResponseEntity.created(new URI("/api/tidbits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tidbits : Updates an existing tidbit.
     *
     * @param tidbitDTO the tidbitDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tidbitDTO,
     * or with status 400 (Bad Request) if the tidbitDTO is not valid,
     * or with status 500 (Internal Server Error) if the tidbitDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tidbits")
    public ResponseEntity<TidbitDTO> updateTidbit(@Valid @RequestBody TidbitDTO tidbitDTO) throws URISyntaxException {
        log.debug("REST request to update Tidbit : {}", tidbitDTO);
        if (tidbitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TidbitDTO result = tidbitService.save(tidbitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tidbitDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tidbits : get all the tidbits.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tidbits in body
     */
    @GetMapping("/tidbits")
    public ResponseEntity<List<TidbitDTO>> getAllTidbits(TidbitCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tidbits by criteria: {}", criteria);
        Page<TidbitDTO> page = tidbitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tidbits");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /tidbits/count : count all the tidbits.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/tidbits/count")
    public ResponseEntity<Long> countTidbits(TidbitCriteria criteria) {
        log.debug("REST request to count Tidbits by criteria: {}", criteria);
        return ResponseEntity.ok().body(tidbitQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /tidbits/:id : get the "id" tidbit.
     *
     * @param id the id of the tidbitDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tidbitDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tidbits/{id}")
    public ResponseEntity<TidbitDTO> getTidbit(@PathVariable Long id) {
        log.debug("REST request to get Tidbit : {}", id);
        Optional<TidbitDTO> tidbitDTO = tidbitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tidbitDTO);
    }

    /**
     * DELETE  /tidbits/:id : delete the "id" tidbit.
     *
     * @param id the id of the tidbitDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tidbits/{id}")
    public ResponseEntity<Void> deleteTidbit(@PathVariable Long id) {
        log.debug("REST request to delete Tidbit : {}", id);
        tidbitService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
