package com.dantegarden.updatehosts.dao;

import com.dantegarden.updatehosts.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-07 22:00
 */
@Repository
public interface DomainRepository extends JpaRepository<Domain, Integer> {
}
