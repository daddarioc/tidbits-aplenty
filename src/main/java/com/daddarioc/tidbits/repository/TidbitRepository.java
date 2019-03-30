package com.daddarioc.tidbits.repository;

import com.daddarioc.tidbits.domain.Tidbit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Tidbit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TidbitRepository extends JpaRepository<Tidbit, Long>, JpaSpecificationExecutor<Tidbit> {

}
