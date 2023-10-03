package com.example.investment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.investment.dto.UserDto;

public interface UserRepository extends JpaRepository<UserDto, String>, JpaSpecificationExecutor<UserDto> {
    /** ユーザ名でユーザ情報取得 */
    @Query("SELECT X FROM UserDto X WHERE X.userName = ?1")
    UserDto findByUserNameDto(String userName);

    /** メールアドレスでユーザ情報取得 */
    @Query("SELECT X FROM UserDto X WHERE X.mailaddress = ?1")
    UserDto findByMailaddressDto(String mailaddress);

    /** メールアドレスでユーザID取得 */
    @Query("SELECT X.userId FROM UserDto X WHERE X.mailaddress = ?1")
    String findByMailaddressInt(String mailaddress);
}
