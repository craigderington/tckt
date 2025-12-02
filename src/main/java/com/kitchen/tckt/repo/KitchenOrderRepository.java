package com.kitchen.tckt.repo;

import com.kitchen.tckt.model.KitchenOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {

    List<KitchenOrder> findAllByOrderByCreatedAtDesc();
}

