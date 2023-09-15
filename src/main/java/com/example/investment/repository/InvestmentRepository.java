package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dao.InvestmentDao;

public interface InvestmentRepository extends JpaRepository<InvestmentDao, String>, JpaSpecificationExecutor<InvestmentDao> {
    /** 全取引リスト取得*/
    @Query("SELECT X FROM InvestmentDao X")
    List<InvestmentDao> findByList();
}
