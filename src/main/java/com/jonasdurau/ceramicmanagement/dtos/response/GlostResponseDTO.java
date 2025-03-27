package com.jonasdurau.ceramicmanagement.dtos.response;

public class GlostResponseDTO {
    
    private String productName;

    private String glazeColor;

    private Double quantity;

    public GlostResponseDTO() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGlazeColor() {
        return glazeColor;
    }

    public void setGlazeColor(String glazeColor) {
        this.glazeColor = glazeColor;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
