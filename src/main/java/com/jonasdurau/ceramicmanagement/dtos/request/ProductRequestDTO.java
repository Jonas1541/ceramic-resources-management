package com.jonasdurau.ceramicmanagement.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser positivo")
    private BigDecimal price;

    @Positive(message = "A altura deve ser positiva")
    private double height;

    @Positive(message = "O comprimento deve ser positivo")
    private double length;

    @Positive(message = "A largura deve ser positiva")
    private double width;

    @Positive(message = "O tipo é obrigatório")
    private long typeId;

    @Positive(message = "A linha é obrigatória")
    private long lineId;

    public ProductRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public double getHeight() {
        return height;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public long getTypeId() {
        return typeId;
    }

    public long getLineId() {
        return lineId;
    }
}
