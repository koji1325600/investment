package com.example.investment.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="selling")
public class SellingDto implements Serializable {
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

    /** 売却価格 */
    @Column(name="sell_price")
    private int sellPrice;

    /** 日時 */
    @Column
    private LocalDateTime date;
    
    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDto() {
        this.id = UUID.randomUUID().toString();
    }
}
