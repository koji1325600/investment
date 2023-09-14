package com.example.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.investment.dao.InvestmentDao;


public interface InvestmentRepository extends JpaRepository<InvestmentDao, String>, JpaSpecificationExecutor<InvestmentDao> {
    
}
