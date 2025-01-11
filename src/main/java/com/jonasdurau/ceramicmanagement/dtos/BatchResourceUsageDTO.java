package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

public class BatchResourceUsageDTO {
    
    @Min(value = 1, message = "O ID do recurso deve ser positivo.")
    private Long resourceId;

    @Min(value = 0, message = "A quantidade inicial não pode ser negativa.")
    private double initialQuantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "A umidade não pode ser negativa.")
    @DecimalMax(value = "1.0", inclusive = true, message = "A umidade não pode exceder 1.0.")
    private double umidity;

    @Min(value = 0, message = "A quantidade adicionada não pode ser negativa.")
    private double addedQuantity;

    private double totalQuantity;
    private double totalWater;
    private BigDecimal totalCost;

    public BatchResourceUsageDTO() {
    }

    public BatchResourceUsageDTO(Long resourceId, double initialQuantity, double umidity, double addedQuantity, double totalQuantity, double totalWater, BigDecimal totalCost) {
        this.resourceId = resourceId;
        this.initialQuantity = initialQuantity;
        this.umidity = umidity;
        this.addedQuantity = addedQuantity;
        this.totalQuantity = totalQuantity;
        this.totalWater = totalWater;
        this.totalCost = totalCost;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public double getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public double getUmidity() {
        return umidity;
    }

    public void setUmidity(double umidity) {
        this.umidity = umidity;
    }

    public double getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(double addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalWater() {
        return totalWater;
    }

    public void setTotalWater(double totalWater) {
        this.totalWater = totalWater;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
