package com.example.investment.dto;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="buying")
public class BuyingDto implements Serializable {
    /** デフォルトシリアルUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private String id;

    /** 取引ID */
    @Column
    private String investId;

    /** ユーザID */
    @Column
    private String userId;

    /** 取引名 */
    @Column
    private String investName;

    /** ユーザ名 */
    @Column
    private String userName;

    /** 個数 */
    @Column
    private int quantity;

    /** 購入価格 */
    @Column(name="buy_price")
    private int buyPrice;

    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDto() {
        this.id = UUID.randomUUID().toString();
    }
}
