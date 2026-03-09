package com.upgrade.upgradecoreapi.repository;

import com.upgrade.upgradecoreapi.model.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessEntityRepository extends JpaRepository<BusinessEntity, Long> {
}
