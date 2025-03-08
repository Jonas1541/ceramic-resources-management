package com.jonasdurau.ceramicmanagement.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.KilnDTO;
import com.jonasdurau.ceramicmanagement.dtos.MonthReportDTO;
import com.jonasdurau.ceramicmanagement.dtos.YearReportDTO;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.repositories.BisqueFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.GlazeFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;

@Service
public class KilnService {
    
    @Autowired
    private KilnRepository kilnRepository;

    @Autowired
    private BisqueFiringRepository bisqueFiringRepository;

    @Autowired
    private GlazeFiringRepository glazeFiringRepository;

    @Transactional(readOnly = true)
    public List<KilnDTO> findAll() {
        List<Kiln> list = kilnRepository.findAll();
        return list.stream().map(this::entityToDTO).toList();
    }

    @Transactional(readOnly = true)
    public KilnDTO findById(Long id) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + id));
        return entityToDTO(entity);
    }

    @Transactional
    public KilnDTO create(KilnDTO dto) {
        Kiln entity = new Kiln();
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        entity = kilnRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public KilnDTO update(Long id, KilnDTO dto) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. id: " + id));
        entity.setName(dto.getName());
        entity.setPower(dto.getPower());
        entity = kilnRepository.save(entity);
        return entityToDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Kiln entity = kilnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forno não encontrado. Id: " + id));
        boolean hasBisqueFiring = bisqueFiringRepository.existsByKilnId(id);
        boolean hasGlazeFiring = glazeFiringRepository.existsByKilnId(id);
        if(hasBisqueFiring || hasGlazeFiring) {
            throw new ResourceDeletionException("O forno não pode ser deletado pois possui queimas associadas.");
        }
        kilnRepository.delete(entity);
    }

    @Transactional(readOnly = true)
    public List<YearReportDTO> yearlyReport(Long kilnId) {
        Kiln kiln = kilnRepository.findById(kilnId)
                .orElseThrow(() -> new ResourceNotFoundException("Kiln not found: " + kilnId));
        ZoneId zone = ZoneId.systemDefault();
        Stream<AbstractMap.SimpleEntry<Instant, BigDecimal>> bisqueStream = kiln.getBisqueFirings().stream()
                .map(bisque -> new AbstractMap.SimpleEntry<>(bisque.getCreatedAt(), bisque.getCostAtTime()));
        Stream<AbstractMap.SimpleEntry<Instant, BigDecimal>> glazeStream = kiln.getGlazeFirings().stream()
                .map(glaze -> new AbstractMap.SimpleEntry<>(glaze.getCreatedAt(), glaze.getCostAtTime()));
        Stream<AbstractMap.SimpleEntry<Instant, BigDecimal>> combinedStream = Stream.concat(bisqueStream, glazeStream);
        Map<Integer, Map<Month, List<AbstractMap.SimpleEntry<Instant, BigDecimal>>>> mapYearMonth = combinedStream
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().atZone(zone).getYear(),
                        Collectors.groupingBy(
                                entry -> entry.getKey().atZone(zone).getMonth())));
        List<YearReportDTO> yearReports = new ArrayList<>();
        for (Map.Entry<Integer, Map<Month, List<AbstractMap.SimpleEntry<Instant, BigDecimal>>>> yearEntry : mapYearMonth
                .entrySet()) {
            int year = yearEntry.getKey();
            Map<Month, List<AbstractMap.SimpleEntry<Instant, BigDecimal>>> monthMap = yearEntry.getValue();
            YearReportDTO yearReport = new YearReportDTO(year);
            double totalIncomingQtyYear = 0.0;
            BigDecimal totalIncomingCostYear = BigDecimal.ZERO;
            for (Month m : Month.values()) {
                List<AbstractMap.SimpleEntry<Instant, BigDecimal>> monthEntries = monthMap.getOrDefault(m,
                        Collections.emptyList());
                double incomingQty = monthEntries.size();
                BigDecimal incomingCost = monthEntries.stream()
                        .map(entry -> entry.getValue() != null ? entry.getValue() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalIncomingQtyYear += incomingQty;
                totalIncomingCostYear = totalIncomingCostYear.add(incomingCost);
                MonthReportDTO monthDto = new MonthReportDTO();
                monthDto.setMonthName(m.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                monthDto.setIncomingQty(incomingQty);
                monthDto.setIncomingCost(incomingCost);
                monthDto.setOutgoingQty(0.0);
                monthDto.setOutgoingProfit(BigDecimal.ZERO);
                yearReport.getMonths().add(monthDto);
            }
            yearReport.setTotalIncomingQty(totalIncomingQtyYear);
            yearReport.setTotalIncomingCost(totalIncomingCostYear);
            yearReport.setTotalOutgoingQty(0.0);
            yearReport.setTotalOutgoingProfit(BigDecimal.ZERO);
            yearReports.add(yearReport);
        }
        yearReports.sort((a, b) -> b.getYear() - a.getYear());
        return yearReports;
    }

    private KilnDTO entityToDTO(Kiln entity) {
        KilnDTO dto = new KilnDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setName(entity.getName());
        dto.setPower(entity.getPower());
        return dto;
    }
}
