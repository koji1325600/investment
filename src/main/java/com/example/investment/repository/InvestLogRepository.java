package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dao.InvestLogDao;

public interface InvestLogRepository extends JpaRepository<InvestLogDao, String>, JpaSpecificationExecutor<InvestLogDao> {
    /** 全売買情報ログリスト取得 取引IDソート */
    @Query("SELECT X FROM InvestLogDao X ORDER BY X.investId")
    List<InvestLogDao> findByInvestDaoOrderByInvestIdList();

    /** 全売買情報ログリスト取得 日時ソート 降順*/
    @Query("SELECT X FROM InvestLogDao X ORDER BY X.date DESC")
    List<InvestLogDao> findByInvestDaoOrderByDateList();
}
