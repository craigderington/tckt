package com.kitchen.tckt.model;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
public class KitchenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    private String item;
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String handledByPod;
    private String handledByNode;

    private ZonedDateTime createdAt;
    private boolean archived = false;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.NEW;
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getItem() {
        return item;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getHandledByPod() {
        return handledByPod;
    }

    public String getHandledByNode() {
        return handledByNode;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setHandledByPod(String handledByPod) {
        this.handledByPod = handledByPod;
    }

    public void setHandledByNode(String handledByNode) {
        this.handledByNode = handledByNode;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}

