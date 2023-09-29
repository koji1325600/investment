package com.example.investment.dao;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="user")
public class UserDao implements Serializable {
    /** デフォルトシリアルUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @Column(name="user_id")
    private String userId;

    /** 名前 */
    @Column(name="user_name")
    private String userName;

    /** パスワード */
    @Column(nullable = false)
    private String password;

    /** メールアドレス */
    @Column(nullable = false)
    private String mailaddress;

    /** 所持金 */
    @Column
    private int money;

    /** 自動取引設定 */
    @Column
    private Boolean auto;

    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDao() {
        this.userId = UUID.randomUUID().toString();
    }
}
