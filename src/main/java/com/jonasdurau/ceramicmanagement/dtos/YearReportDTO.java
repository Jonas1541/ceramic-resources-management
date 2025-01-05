package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class YearReportDTO {

    private int year;
    private List<MonthReportDTO> months = new ArrayList<>();

    // Totais anuais
    private double totalIncomingQty;
    private BigDecimal totalIncomingCost;
    private double totalOutgoingQty;

    public YearReportDTO() {
    }

    public YearReportDTO(int year) {
        this.year = year;
    }

    public YearReportDTO(int year, double totalIncomingQty, BigDecimal totalIncomingCost, double totalOutgoingQty) {
        this.year = year;
        this.totalIncomingQty = totalIncomingQty;
        this.totalIncomingCost = totalIncomingCost;
        this.totalOutgoingQty = totalOutgoingQty;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<MonthReportDTO> getMonths() {
        return months;
    }

    public double getTotalIncomingQty() {
        return totalIncomingQty;
    }

    public void setTotalIncomingQty(double totalIncomingQty) {
        this.totalIncomingQty = totalIncomingQty;
    }

    public BigDecimal getTotalIncomingCost() {
        return totalIncomingCost;
    }

    public void setTotalIncomingCost(BigDecimal totalIncomingCost) {
        this.totalIncomingCost = totalIncomingCost;
    }

    public double getTotalOutgoingQty() {
        return totalOutgoingQty;
    }

    public void setTotalOutgoingQty(double totalOutgoingQty) {
        this.totalOutgoingQty = totalOutgoingQty;
    }
}
