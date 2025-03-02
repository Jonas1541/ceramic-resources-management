package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.ProductDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductLine;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.repositories.ProductLineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTypeRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository typeRepository;

    @Autowired
    private ProductLineRepository lineRepository;

    @Autowired
    private ProductTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> list = productRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public ProductDTO create(ProductDTO dto) {
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        ProductType type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo não encontrado. Id: " + dto.getTypeId()));
        ProductLine line = lineRepository.findById(dto.getLineId())
                .orElseThrow(() -> new ResourceNotFoundException("Linha não encontrada. Id: " + dto.getLineId()));
        entity.setType(type);
        entity.setLine(line);
        entity = productRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setHeight(dto.getHeight());
        entity.setLength(dto.getLength());
        entity.setWidth(dto.getWidth());
        ProductType type = typeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo não encontrado. Id: " + dto.getTypeId()));
        ProductLine line = lineRepository.findById(dto.getLineId())
                .orElseThrow(() -> new ResourceNotFoundException("Linha não encontrada. Id: " + dto.getLineId()));
        entity.setType(type);
        entity.setLine(line);
        entity = productRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado. Id: " + id));
        boolean hasTransactions = transactionRepository.existsByProductId(id);
        if(hasTransactions) {
            throw new ResourceDeletionException("Não é possível deletar o produto com id " + id + " pois ele possui transações associadas.");
        }
        productRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<YearReportDTO> yearlyReport(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        List<ProductTransaction> txs = product.getTransactions();
        ZoneId zone = ZoneId.systemDefault();
        Map<Integer, Map<Month, List<ProductTransaction>>> mapYearMonth = txs.stream()
                .map(t -> new AbstractMap.SimpleEntry<>(t, t.getCreatedAt().atZone(zone)))
                .collect(Collectors.groupingBy(
                        entry -> entry.getValue().getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getValue().getMonth(),
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<ProductTransaction>>> yearEntry : mapYearMonth.entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<ProductTransaction>> mapMonth = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            double totalOutgoingQtyYear = 0.0;
            BigDecimal totalProfitYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<ProductTransaction> monthTx = mapMonth.getOrDefault(m, Collections.emptyList());
                double incomingQty = 0.0;
                double outgoingQty = 0.0;
                BigDecimal profit = BigDecimal.ZERO;
                for (ProductTransaction t : monthTx) {
                    if (t.getOutgoingReason() == null) {
                        incomingQty += 1;
                    } else {
                        outgoingQty += 1;
                        profit = profit.add(t.getProfit());
                    }
                }
                totalIncomingQtyYear += incomingQty;
                totalOutgoingQtyYear += outgoingQty;
                totalProfitYear = totalProfitYear.add(profit);
                MonthReportDTO monthDto = new MonthReportDTO();
                monthDto.setMonthName(m.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                monthDto.setIncomingQty(incomingQty);
                monthDto.setIncomingCost(BigDecimal.ZERO);
                monthDto.setOutgoingQty(outgoingQty);
                monthDto.setOutgoingProfit(profit);
                yearReport.getMonths().add(monthDto);
            }
            yearReport.setTotalIncomingQty(totalIncomingQtyYear);
            yearReport.setTotalIncomingCost(BigDecimal.ZERO);
            yearReport.setTotalOutgoingQty(totalOutgoingQtyYear);
            yearReport.setTotalOutgoingProfit(totalProfitYear);
            yearReports.add(yearReport);
        }
        yearReports.sort((a, b) -> b.getYear() - a.getYear());
        return yearReports;
    }

    private ProductDTO entityToDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setHeight(entity.getHeight());
        dto.setLength(entity.getLength());
        dto.setWidth(entity.getWidth());
        dto.setTypeId(entity.getType().getId());
        dto.setLineId(entity.getLine().getId());
        dto.setProductStock(entity.getProductStock());
        return dto;
    }
}
