package com.reforms.orm;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author evgenie
 */
public class GoodsFilter {

    private Long id;
    private String name;
    private BigDecimal price;
    private Date actTime;

    public Long getId() {
        return id;
    }

    public GoodsFilter setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GoodsFilter setName(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public GoodsFilter setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Date getActTime() {
        return actTime;
    }

    public GoodsFilter setActTime(Date actTime) {
        this.actTime = actTime;
        return this;
    }

}
