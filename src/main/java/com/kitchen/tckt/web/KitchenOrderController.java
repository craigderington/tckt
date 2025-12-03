package com.kitchen.tckt.web;

import com.kitchen.tckt.model.KitchenOrder;
import com.kitchen.tckt.model.OrderStatus;
import com.kitchen.tckt.repo.KitchenOrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Kitchen Orders", description = "Kitchen order management API for distributed ticket queue system")
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
    @Operation(summary = "List active orders", description = "Retrieve all non-archived kitchen orders sorted by creation date (newest first)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active orders")
    public List<KitchenOrder> list() {
        return repository.findByArchivedFalseOrderByCreatedAtDesc();
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Create new order", description = "Create a new kitchen order with the specified menu item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request - item is required")
    })
    public KitchenOrder create(@RequestBody CreateOrderRequest request) {
        if (request.getItem() == null || request.getItem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is required");
        }
        if (request.getTableNumber() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table number is required");
        }

        KitchenOrder order = new KitchenOrder();
        order.setItem(request.getItem().trim());
        order.setTableNumber(request.getTableNumber());
        order.setStatus(OrderStatus.NEW);

        return repository.save(order);
    }

    @PostMapping("/{id}/claim")
    @Transactional
    @Operation(summary = "Claim an order", description = "Mark an order as IN_PROGRESS and assign it to the current pod/node")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order claimed successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Order already done"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict - order was modified by another pod")
    })
    public KitchenOrder claim(@Parameter(description = "Order ID") @PathVariable Long id) {
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
    @Transactional
    @Operation(summary = "Complete an order", description = "Mark an order as DONE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order completed successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict - order was modified by another pod")
    })
    public KitchenOrder done(@Parameter(description = "Order ID") @PathVariable Long id) {
        KitchenOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        order.setStatus(OrderStatus.DONE);
        if (order.getHandledByPod() == null) {
            order.setHandledByPod(podName);
            order.setHandledByNode(nodeName);
        }
        return repository.save(order);
    }

    @PostMapping("/{id}/archive")
    @Transactional
    @Operation(summary = "Archive an order", description = "Archive a completed order to remove it from active view")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order archived successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Only completed orders can be archived"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking conflict - order was modified by another pod")
    })
    public KitchenOrder archive(@Parameter(description = "Order ID") @PathVariable Long id) {
        KitchenOrder order = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (order.getStatus() != OrderStatus.DONE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only completed orders can be archived");
        }

        order.setArchived(true);
        return repository.save(order);
    }

    @GetMapping("/archived")
    @Operation(summary = "List archived orders", description = "Retrieve archived orders with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved archived orders")
    })
    public Page<KitchenOrder> listArchived(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByArchivedTrueOrderByCreatedAtDesc(pageable);
    }

    @GetMapping("/meta")
    @Operation(summary = "Get pod/node metadata", description = "Returns the current pod and node serving this request")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved metadata")
    public Map<String, String> meta() {
        Map<String, String> map = new HashMap<>();
        map.put("podName", podName);
        map.put("nodeName", nodeName);
        return map;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get statistics", description = "Returns aggregated statistics about orders grouped by status, pod, and node")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public Map<String, Object> stats() {
        List<KitchenOrder> allOrders = repository.findAll();

        Map<String, Long> byPod = new HashMap<>();
        Map<String, Long> byNode = new HashMap<>();
        Map<String, Long> byStatus = new HashMap<>();

        for (KitchenOrder order : allOrders) {
            // Count by status
            String status = order.getStatus().toString();
            byStatus.put(status, byStatus.getOrDefault(status, 0L) + 1);

            // Count by pod (only for claimed/done orders)
            if (order.getHandledByPod() != null) {
                byPod.put(order.getHandledByPod(), byPod.getOrDefault(order.getHandledByPod(), 0L) + 1);
            }

            // Count by node (only for claimed/done orders)
            if (order.getHandledByNode() != null) {
                byNode.put(order.getHandledByNode(), byNode.getOrDefault(order.getHandledByNode(), 0L) + 1);
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", allOrders.size());
        stats.put("byPod", byPod);
        stats.put("byNode", byNode);
        stats.put("byStatus", byStatus);

        return stats;
    }

    public static class CreateOrderRequest {
        private String item;
        private Integer tableNumber;

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Integer getTableNumber() {
            return tableNumber;
        }

        public void setTableNumber(Integer tableNumber) {
            this.tableNumber = tableNumber;
        }
    }
}

