package ru.netology.claimsservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.claimsservice.dto.CreditClaim;
import ru.netology.claimsservice.model.ClaimStatus;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@Table(name = "credit_claims")
public class ClaimEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private Integer term;
    @Column(nullable = false)
    private BigDecimal income;
    @Column(name = "current_credit_load", nullable = false)
    private BigDecimal currentCreditLoad;
    @Column(name = "current_credit_rating", nullable = false)
    private Integer currentCreditRating;
    @Column(nullable = false)
    public ClaimStatus status;

    public ClaimEntity(CreditClaim claim){
        this.amount = claim.getAmount();
        this.term = claim.getTerm();
        this.income = claim.getIncome()  ;
        this.currentCreditLoad = claim.getCurrentCreditLoad();
        this.currentCreditRating = claim.getCurrentCreditRating();
        this.status = ClaimStatus.PROCESSED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClaimEntity that = (ClaimEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
