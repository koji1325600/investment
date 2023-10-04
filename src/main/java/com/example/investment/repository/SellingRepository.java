package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dto.SellingDto;

public interface SellingRepository extends JpaRepository<SellingDto, String>, JpaSpecificationExecutor<SellingDto> {
    /** 全売却リスト取得 日時ソート 降順*/
    @Query("SELECT X FROM SellingDto X ORDER BY X.date DESC")
    List<SellingDto> findOrderByDateDescList();
}
