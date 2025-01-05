package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;

public class MonthReportDTO {
    
    private String monthName;
    private double incomingQty;
    private BigDecimal incomingCost;
    private double outgoingQty;

    public MonthReportDTO() {
    }

    public MonthReportDTO(String monthName, double incomingQty, BigDecimal incomingCost, double outgoingQty) {
        this.monthName = monthName;
        this.incomingQty = incomingQty;
        this.incomingCost = incomingCost;
        this.outgoingQty = outgoingQty;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public double getIncomingQty() {
        return incomingQty;
    }

    public void setIncomingQty(double incomingQty) {
        this.incomingQty = incomingQty;
    }

    public BigDecimal getIncomingCost() {
        return incomingCost;
    }

    public void setIncomingCost(BigDecimal incomingCost) {
        this.incomingCost = incomingCost;
    }

    public double getOutgoingQty() {
        return outgoingQty;
    }

    public void setOutgoingQty(double outgoingQty) {
        this.outgoingQty = outgoingQty;
    }
}
