package com.kitchen.tckt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class KitchenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String item;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String handledByPod;
    private String handledByNode;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.NEW;
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItem(String item) {
        this.item = item;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

