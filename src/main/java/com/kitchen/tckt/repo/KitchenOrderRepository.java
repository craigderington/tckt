package com.kitchen.tckt.repo;

import com.kitchen.tckt.model.KitchenOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {

    List<KitchenOrder> findAllByOrderByCreatedAtDesc();

    List<KitchenOrder> findByArchivedFalseOrderByCreatedAtDesc();

    Page<KitchenOrder> findByArchivedTrueOrderByCreatedAtDesc(Pageable pageable);
}

