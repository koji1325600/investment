package com.example.investment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dao.BuyingDao;

public interface BuyingRepository extends JpaRepository<BuyingDao, String>, JpaSpecificationExecutor<BuyingDao> {
    /** 取引IDとユーザIDで売買情報取得 */
    @Query("SELECT X FROM BuyingDao X WHERE X.investId = ?1 AND X.userId = ?2")
    BuyingDao findByInvestIdAndUserIdDao(String investId, String userId);

    /** ユーザIDで売買情報リスト取得 */
    @Query("SELECT X FROM BuyingDao X WHERE X.userId = ?1")
    List<BuyingDao> findByUserIdList(String userId);

    /** 取引IDで売買情報リスト取得 */
    @Query("SELECT X FROM BuyingDao X WHERE X.investId = ?1")
    List<BuyingDao> findByInvestIdList(String investId);
}
