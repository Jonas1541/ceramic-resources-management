package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.jonasdurau.ceramicmanagement.entities.enums.ProductState;
import com.jonasdurau.ceramicmanagement.entities.enums.ResourceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jonasdurau.ceramicmanagement.controllers.exceptions.BusinessException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceDeletionException;
import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.BisqueFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.FiringMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.BisqueFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.BisqueFiring;
import com.jonasdurau.ceramicmanagement.entities.FiringMachineUsage;
import com.jonasdurau.ceramicmanagement.entities.Kiln;
import com.jonasdurau.ceramicmanagement.entities.Machine;
import com.jonasdurau.ceramicmanagement.entities.Product;
import com.jonasdurau.ceramicmanagement.entities.ProductLine;
import com.jonasdurau.ceramicmanagement.entities.ProductTransaction;
import com.jonasdurau.ceramicmanagement.entities.ProductType;
import com.jonasdurau.ceramicmanagement.entities.Resource;
import com.jonasdurau.ceramicmanagement.repositories.BisqueFiringRepository;
import com.jonasdurau.ceramicmanagement.repositories.FiringMachineUsageRepository;
import com.jonasdurau.ceramicmanagement.repositories.KilnRepository;
import com.jonasdurau.ceramicmanagement.repositories.MachineRepository;
import com.jonasdurau.ceramicmanagement.repositories.ProductTransactionRepository;
import com.jonasdurau.ceramicmanagement.repositories.ResourceRepository;

@ExtendWith(MockitoExtension.class)
public class BisqueFiringServiceTest {

    @Mock
    private BisqueFiringRepository firingRepository;

    @Mock
    private KilnRepository kilnRepository;

    @Mock
    private ProductTransactionRepository productTransactionRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private FiringMachineUsageRepository machineUsageRepository;

    @InjectMocks
    private BisqueFiringService bisqueFiringService;

    private Kiln kiln;
    private BisqueFiring firing;
    private ProductTransaction biscuit;
    private Machine machine;
    private Resource electricity;
    private Resource gas;
    private Long kilnId = 1L;
    private Long firingId = 1L;
    private Long biscuitId = 1L;
    private Long machineId = 1L;

    @BeforeEach
    void setUp() {
        kiln = new Kiln();
        kiln.setId(kilnId);
        kiln.setName("Forno Principal");

        Machine machine1 = new Machine();
        machine1.setId(2L);
        machine1.setCreatedAt(Instant.now());
        machine1.setUpdatedAt(null);
        machine1.setName("Máquina");
        machine1.setPower(10);
        
        kiln.getMachines().add(machine1);

        firing = new BisqueFiring();
        firing.setId(firingId);
        firing.setTemperature(1000.0);
        firing.setBurnTime(8.0);
        firing.setCoolingTime(4.0);
        firing.setGasConsumption(10.0);
        firing.setKiln(kiln);

        ProductType type = new ProductType();
        type.setId(1L);
        type.setName("Vaso");
        
        ProductLine line = new ProductLine();
        line.setId(1L);
        line.setName("Coleção Verão");

        Product product = new Product();
        product.setId(1L);
        product.setName("Vaso Decorativo");
        product.setPrice(new BigDecimal("150.00"));
        product.setType(type);
        product.setLine(line);

        biscuit = new ProductTransaction();
        biscuit.setId(biscuitId);
        biscuit.setState(ProductState.GREENWARE);
        biscuit.setProduct(product);
        
        product.getTransactions().add(biscuit); 

        machine = new Machine();
        machine.setId(machineId);
        machine.setName("Máquina de Modelagem");
        machine.setPower(2.5);

        electricity = new Resource();
        electricity.setCategory(ResourceCategory.ELECTRICITY);
        electricity.setUnitValue(new BigDecimal("0.50"));

        gas = new Resource();
        gas.setCategory(ResourceCategory.GAS);
        gas.setUnitValue(new BigDecimal("3.00"));
    }

    @Test
    void findAllByParentId_WhenKilnExists_ShouldReturnList() {
        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByKilnId(kilnId)).thenReturn(List.of(firing));

        List<FiringListDTO> result = bisqueFiringService.findAllByParentId(kilnId);

        assertEquals(1, result.size());
        assertEquals(firingId, result.getFirst().id());
        verify(firingRepository).findByKilnId(kilnId);
    }

    @Test
    void findAllByParentId_WhenKilnNotExists_ShouldThrowException() {
        when(kilnRepository.existsById(kilnId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.findAllByParentId(kilnId));
    }

    @Test
    void findById_WhenExists_ShouldReturnFiring() {
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
    
        BisqueFiringResponseDTO result = bisqueFiringService.findById(kilnId, firingId);
    
        assertEquals(firingId, result.id());
        verify(firingRepository).findByIdAndKilnId(firingId, kilnId);

        verify(resourceRepository).findByCategory(ResourceCategory.ELECTRICITY);
        verify(resourceRepository).findByCategory(ResourceCategory.GAS);
    }

    @Test
    void findById_WhenFiringNotExists_ShouldThrowException() {
        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.findById(kilnId, firingId));
    }

    @Test
    void create_WithValidData_ShouldCreateFiring() {
        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(
            1000.0, 8.0, 4.0, 10.0, 
            List.of(biscuitId), 
            List.of(new FiringMachineUsageRequestDTO(2.0, machineId))
        );

        when(machineUsageRepository.save(any(FiringMachineUsage.class))).thenAnswer(invocation -> {
            FiringMachineUsage usage = invocation.getArgument(0);
            usage.setId(1L);
            usage.setMachine(machine);
            return usage;
        });

        when(kilnRepository.findById(kilnId)).thenReturn(Optional.of(kiln));
        when(productTransactionRepository.findById(biscuitId)).thenReturn(Optional.of(biscuit));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        when(firingRepository.save(any())).thenReturn(firing);

        BisqueFiringResponseDTO result = bisqueFiringService.create(kilnId, dto);

        assertNotNull(result);
        assertEquals(ProductState.BISCUIT, biscuit.getState());
        
        verify(machineUsageRepository).save(argThat(usage -> 
            usage.getMachine().equals(machine) &&
            usage.getUsageTime() == 2.0
        ));
        
        verify(firingRepository, times(2)).save(any());
    }

    @Test
    void create_WhenKilnNotFound_ShouldThrowException() {
        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(1000.0, 8.0, 4.0, 10.0, List.of(biscuitId), Collections.emptyList());

        when(kilnRepository.findById(kilnId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.create(kilnId, dto));
        verify(firingRepository, never()).save(any());
    }

    @Test
    void create_WithInvalidMachine_ShouldThrowException() {
        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(
            1000.0, 8.0, 4.0, 10.0, 
            List.of(biscuitId), 
            List.of(new FiringMachineUsageRequestDTO(2.0, 999L))
        );

        when(kilnRepository.findById(kilnId)).thenReturn(Optional.of(kiln));

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.create(kilnId, dto));
    }

    @Test
    void create_WithInvalidBiscuit_ShouldThrowException() {
        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(1000.0, 8.0, 4.0, 10.0, List.of(biscuitId), Collections.emptyList());

        when(kilnRepository.findById(kilnId)).thenReturn(Optional.of(kiln));
        when(productTransactionRepository.findById(biscuitId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.create(kilnId, dto));
    }

    @Test
    void update_WhenValid_ShouldUpdateFiringAndBiscuits() {
        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(1100.0, 9.0, 5.0, 12.0, List.of(biscuitId), List.of(new FiringMachineUsageRequestDTO(3.0, machineId)));

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
        when(productTransactionRepository.findById(biscuitId)).thenReturn(Optional.of(biscuit));
        when(machineRepository.findById(machineId)).thenReturn(Optional.of(machine));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        when(firingRepository.save(any())).thenReturn(firing);

        BisqueFiringResponseDTO result = bisqueFiringService.update(kilnId, firingId, dto);

        assertEquals(1100.0, result.temperature());
        verify(productTransactionRepository, times(1)).save(biscuit);
    }

    @Test
    void update_WhenBiscuitInOtherFiring_ShouldThrowException() {
        BisqueFiring otherFiring = new BisqueFiring();
        otherFiring.setId(2L);
        
        ProductTransaction otherBiscuit = new ProductTransaction();
        otherBiscuit.setId(2L);
        otherBiscuit.setBisqueFiring(otherFiring);

        BisqueFiringRequestDTO dto = new BisqueFiringRequestDTO(1100.0, 9.0, 5.0, 12.0, List.of(2L), Collections.emptyList());

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
        when(productTransactionRepository.findById(2L)).thenReturn(Optional.of(otherBiscuit));

        assertThrows(BusinessException.class, () -> bisqueFiringService.update(kilnId, firingId, dto));
    }

    @Test
    void delete_WhenValid_ShouldDeleteFiring() {
        biscuit.setState(ProductState.BISCUIT);
        firing.getBiscuits().add(biscuit);

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));

        bisqueFiringService.delete(kilnId, firingId);

        assertEquals(ProductState.GREENWARE, biscuit.getState());
        verify(firingRepository).delete(firing);
    }

    @Test
    void delete_WhenBiscuitIsGlazed_ShouldThrowException() {
        biscuit.setState(ProductState.GLAZED);
        firing.getBiscuits().add(biscuit);

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));

        assertThrows(ResourceDeletionException.class, () -> bisqueFiringService.delete(kilnId, firingId));
    }

    @Test
    void delete_WhenFiringNotFound_ShouldThrowException() {
        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bisqueFiringService.delete(kilnId, firingId));
    }
}