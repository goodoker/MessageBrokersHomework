package ru.netology.claimsservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.netology.claimsservice.dto.ClaimIdWrapper;
import ru.netology.claimsservice.dto.CreditClaim;
import ru.netology.claimsservice.entity.ClaimEntity;
import ru.netology.claimsservice.service.ClaimsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/credit-claims")
public class ClaimsController {
    private final ClaimsService claimsService;

    @Autowired
    public ClaimsController(ClaimsService claimsService) {
        this.claimsService = claimsService;
    }

    @PostMapping
    public ClaimIdWrapper saveClaim(@RequestBody CreditClaim claim){
        Long id = claimsService.processClaim(claim);
        ClaimIdWrapper claimId = new ClaimIdWrapper(id);
        return claimId;
    }

    @GetMapping
    public List<ClaimEntity> getAllClaims(){
        return claimsService.getAllClaims();
    }

    @GetMapping("/{id}")
    public Optional<ClaimEntity> getClaimById(@PathVariable Long id){
        return claimsService.getClaimById(id);
    }

    @GetMapping("/{id}/status")
    public String getClaimStatusById(@PathVariable Long id){
        Optional<ClaimEntity> otpClaimEntity = claimsService.getClaimById(id);
        if (otpClaimEntity.isPresent()){
            return otpClaimEntity.get().getStatus().toString();
        }
        return "CreditClaim with id = " + id + " was not found";
    }
}
