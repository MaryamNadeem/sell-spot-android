package com.sellspot.app.Models;

import java.util.List;

public class Order {
    private String placedBy;
    private String placedFrom;
    private List<String> productId;
    private String time;
    private String status;
    private String orderId;

   public Order() {}

    public Order(String placedBy, String placedFrom, List<String> productId, String time, String status, String orderId) {
        this.placedBy = placedBy;
        this.placedFrom = placedFrom;
        this.productId = productId;
        this.time = time;
        this.status = status;
        this.orderId = orderId;
    }

    public String getPlacedBy() {
        return placedBy;
    }

    public void setPlacedBy(String placedBy) {
        this.placedBy = placedBy;
    }

    public String getPlacedFrom() {
        return placedFrom;
    }

    public void setPlacedFrom(String placedFrom) {
        this.placedFrom = placedFrom;
    }

    public List<String> getProductId() {
        return productId;
    }

    public void setProductId(List<String> productId) {
        this.productId = productId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
