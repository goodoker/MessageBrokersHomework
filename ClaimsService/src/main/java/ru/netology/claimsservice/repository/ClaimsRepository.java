package ru.netology.claimsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.claimsservice.entity.ClaimEntity;

@Repository
public interface ClaimsRepository extends JpaRepository<ClaimEntity, Long> {
}
