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
@Table(name="investment")
public class InvestmentDao implements Serializable {
    /** デフォルトシリアルUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @Column
    private String id;

    /** 名前 */
    @Column
    private String name;

    /** 最大価格 */
    @Column(name="max_price")
    private int maxPrice;

    /** 最小価格 */
    @Column(name="min_price")
    private int minPrice;

    /** 価格 */
    @Column
    private int price;

    /** 暴落設定 */
    @Column
    private int cresh;

    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDao() {
        this.id = UUID.randomUUID().toString();
    }
}
