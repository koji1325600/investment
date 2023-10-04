package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dto.InvestmentDto;

public interface InvestmentRepository extends JpaRepository<InvestmentDto, String>, JpaSpecificationExecutor<InvestmentDto> {
    /** 全取引リスト取得*/
    @Query("SELECT X FROM InvestmentDto X")
    List<InvestmentDto> findByList();
}
