package com.example.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dao.UserDao;

public interface UserRepository extends JpaRepository<UserDao, String>, JpaSpecificationExecutor<UserDao> {
    /** ユーザ名でユーザ情報取得 */
    @Query("SELECT X FROM UserDao X WHERE X.userName = ?1")
    UserDao findByUserNameDao(String userName);

    /** メールアドレスでユーザ情報取得 */
    @Query("SELECT X FROM UserDao X WHERE X.mailaddress = ?1")
    UserDao findByMailaddressDao(String mailaddress);

    /** メールアドレスでユーザID取得 */
    @Query("SELECT X.userId FROM UserDao X WHERE X.mailaddress = ?1")
    Integer findByMailaddressInt(String mailaddress);
}
