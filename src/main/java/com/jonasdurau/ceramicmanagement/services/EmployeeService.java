package com.jonasdurau.ceramicmanagement.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.request.EmployeeRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.EmployeeResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.Employee;
import com.jonasdurau.ceramicmanagement.entities.EmployeeCategory;
import com.jonasdurau.ceramicmanagement.repositories.BatchEmployeeUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.EmployeeCategoryRepository;
import com.jonasdurau.ceramicmanagement.repositories.EmployeeRepository;

@Service
public class EmployeeService implements IndependentCrudService<EmployeeResponseDTO, EmployeeRequestDTO, EmployeeResponseDTO, Long>{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeCategoryRepository employeeCategoryRepository;

    @Autowired
    private BatchEmployeeUsageRepository batchEmployeeUsageRepository;

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public List<EmployeeResponseDTO> findAll() {
        List<Employee> list = employeeRepository.findAll();
        return list.stream().map(this::entityToResponseDTO).toList();
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager", readOnly = true)
    public EmployeeResponseDTO findById(Long id) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. Id: " + id));
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {
        EmployeeCategory category = employeeCategoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de funcionário não encontrada. Id: " + dto.categoryId()));
        Employee entity = new Employee();
        entity.setName(dto.name());
        entity.setCategory(category);
        entity.setCostPerHour(dto.costPerHour());
        entity = employeeRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO dto) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. Id:" + id));
        EmployeeCategory category = employeeCategoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de funcionário não encontrada. Id: " + dto.categoryId()));
        entity.setName(dto.name());
        entity.setCategory(category);
        entity.setCostPerHour(dto.costPerHour());
        entity = employeeRepository.save(entity);
        return entityToResponseDTO(entity);
    }

    @Override
    @Transactional(transactionManager = "tenantTransactionManager")
    public void delete(Long id) {
        Employee entity = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado. Id: " + id));
        boolean hasBatches = batchEmployeeUsageRepository.existsByEmployeeId(id);
        if(hasBatches) {
            throw new ResourceDeletionException("Não é possível deletar o funcionário de id " + id + " pois ele possui bateladas associadas.");
        }
        employeeRepository.delete(entity);
    }
    
    private EmployeeResponseDTO entityToResponseDTO(Employee entity) {
        return new EmployeeResponseDTO(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getName(),
            entity.getCategory().getId(),
            entity.getCategory().getName(),
            entity.getCostPerHour()
        );
    }
}
