package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dto.InvestLogDto;

public interface InvestLogRepository extends JpaRepository<InvestLogDto, String>, JpaSpecificationExecutor<InvestLogDto> {
    /** 全売買情報ログリスト取得 取引IDソート */
    @Query("SELECT X FROM InvestLogDto X ORDER BY X.investId")
    List<InvestLogDto> findOrderByInvestIdList();

    /** 全売買情報ログリスト取得 日時ソート 降順*/
    @Query("SELECT X FROM InvestLogDto X ORDER BY X.date DESC")
    List<InvestLogDto> findOrderByDateDescList();

    /** 全売買情報ログリスト取得 日時ソート*/
    @Query("SELECT X FROM InvestLogDto X ORDER BY X.date")
    List<InvestLogDto> findOrderByDateList();

    /** 全売買情報ログリスト取得 日時ソート*/
    @Query("SELECT X FROM InvestLogDto X WHERE X.investId = ?1 ORDER BY X.date")
    List<InvestLogDto> findByInvestIdOrderByDateList(String investId);
}
