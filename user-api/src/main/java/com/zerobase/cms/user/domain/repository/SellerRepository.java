package com.zerobase.cms.user.domain.repository;

import com.zerobase.cms.user.domain.model.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByEmail(String email);
}
