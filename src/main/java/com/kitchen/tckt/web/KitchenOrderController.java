package com.kitchen.tckt.web;

import com.kitchen.tckt.model.KitchenOrder;
import com.kitchen.tckt.model.OrderStatus;
import com.kitchen.tckt.repo.KitchenOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class KitchenOrderController {

    private final KitchenOrderRepository repository;
    private final String podName;
    private final String nodeName;

    public KitchenOrderController(
            KitchenOrderRepository repository,
            @Value("${POD_NAME:unknown-pod}") String podName,
            @Value("${NODE_NAME:unknown-node}") String nodeName) {
        this.repository = repository;
        this.podName = podName;
        this.nodeName = nodeName;
    }

    @GetMapping
    public List<KitchenOrder> list() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping
    public KitchenOrder create(@RequestBody CreateOrderRequest request) {
        if (request.getItem() == null || request.getItem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is required");
        }

        KitchenOrder order = new KitchenOrder();
        order.setItem(request.getItem().trim());
        order.setStatus(OrderStatus.NEW);

        return repository.save(order);
    }

    @PostMapping("/{id}/claim")
    public KitchenOrder claim(@PathVariable Long id) {
        KitchenOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (order.getStatus() == OrderStatus.DONE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already done");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setHandledByPod(podName);
        order.setHandledByNode(nodeName);
        return repository.save(order);
    }

    @PostMapping("/{id}/done")
    public KitchenOrder done(@PathVariable Long id) {
        KitchenOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        order.setStatus(OrderStatus.DONE);
        if (order.getHandledByPod() == null) {
            order.setHandledByPod(podName);
            order.setHandledByNode(nodeName);
        }
        return repository.save(order);
    }

    @GetMapping("/meta")
    public Map<String, String> meta() {
        Map<String, String> map = new HashMap<>();
        map.put("podName", podName);
        map.put("nodeName", nodeName);
        return map;
    }

    public static class CreateOrderRequest {
        private String item;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }
    }
}

