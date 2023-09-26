package com.example.investment.dao;

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
@Table(name="investlog")
public class InvestLogDao implements Serializable {
    /** デフォルトシリアルUID */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private String id;

    /** 取引ID */
    @Column
    private String investId;

    /** 取引名 */
    @Column
    private String investName;

    /** 価格 */
    @Column
    private int price;

    /** 日時 */
    @Column
    private LocalDateTime date;

    /**
     * デフォルトコンストラクタ。
     */
    public void addTodoDao() {
        this.id = UUID.randomUUID().toString();
    }
}
