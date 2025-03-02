package com.jonasdurau.ceramicmanagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jonasdurau.ceramicmanagement.dtos.ProductTransactionDTO;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductOutgoingReason;
import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.services.ProductTransactionService;

@RestController
@RequestMapping("/products/{productId}/transactions")
public class ProductTransactionController {

    @Autowired
    private ProductTransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<ProductTransactionDTO>> findAllByProduct(@PathVariable Long productId) {
        List<ProductTransactionDTO> list = transactionService.findAllByProduct(productId);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "state")
    public ResponseEntity<List<ProductTransactionDTO>> findAllByState(@RequestParam ProductState state) {
        List<ProductTransactionDTO> list = transactionService.findAllByState(state);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ProductTransactionDTO> findById(@PathVariable Long productId, @PathVariable Long transactionId) {
        ProductTransactionDTO dto = transactionService.findById(productId, transactionId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<List<ProductTransactionDTO>> create(@PathVariable Long productId, @RequestParam int quantity) {
        List<ProductTransactionDTO> created = transactionService.create(productId, quantity);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long transactionId) {
        transactionService.delete(productId, transactionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{transactionId}", params = "outgoingReason")
    public ResponseEntity<ProductTransactionDTO> outgoing(@PathVariable Long productId, @PathVariable Long transactionId, @RequestParam ProductOutgoingReason outgoingReason) {
        ProductTransactionDTO dto = transactionService.outgoing(productId, transactionId, outgoingReason);
        return ResponseEntity.ok(dto);
    }
}
