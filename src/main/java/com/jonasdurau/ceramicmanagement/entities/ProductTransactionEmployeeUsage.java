package com.jonasdurau.ceramicmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_product_transaction_employee_usage")
public class ProductTransactionEmployeeUsage extends BaseEmployeeUsage {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_transaction_id")
    private ProductTransaction productTransaction;

    public ProductTransactionEmployeeUsage() {
    }

    public ProductTransactionEmployeeUsage(Long id, double usageTime, Employee employee, ProductTransaction productTransaction) {
        super(id, usageTime, employee);
        this.productTransaction = productTransaction;
    }

    public ProductTransaction getProductTransaction() {
        return productTransaction;
    }

    public void setProductTransaction(ProductTransaction productTransaction) {
        this.productTransaction = productTransaction;
    }
}
