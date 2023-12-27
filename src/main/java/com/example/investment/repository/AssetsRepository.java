package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dto.AssetsDto;

public interface AssetsRepository extends JpaRepository<AssetsDto, String>, JpaSpecificationExecutor<AssetsDto> {
    /** 全資産ログリスト取得 日時ソート 降順*/
    @Query("SELECT X FROM AssetsDto X ORDER BY X.date DESC")
    List<AssetsDto> findOrderByDateDescList();

    /** ユーザID資産ログリスト取得 日時ソート*/
    @Query("SELECT X FROM AssetsDto X WHERE X.userId = ?1 ORDER BY X.date")
    List<AssetsDto> findByUserIdOrderByDateList(String userId);
}
