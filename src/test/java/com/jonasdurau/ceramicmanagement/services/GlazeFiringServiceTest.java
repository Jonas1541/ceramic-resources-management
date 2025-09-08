package com.jonasdurau.ceramicmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.jonasdurau.ceramicmanagement.controllers.exceptions.ResourceNotFoundException;
import com.jonasdurau.ceramicmanagement.dtos.list.FiringListDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.FiringMachineUsageRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlazeFiringRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.request.GlostRequestDTO;
import com.jonasdurau.ceramicmanagement.dtos.response.GlazeFiringResponseDTO;
import com.jonasdurau.ceramicmanagement.entities.*;
import com.jonasdurau.ceramicmanagement.repositories.*;

@ExtendWith(MockitoExtension.class)
public class GlazeFiringServiceTest {

    @Mock
    private GlazeFiringRepository firingRepository;

    @Mock
    private KilnRepository kilnRepository;

    @Mock
    private ProductTransactionRepository productTransactionRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private GlazeTransactionService glazeTransactionService;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private FiringMachineUsageRepository machineUsageRepository;

    @InjectMocks
    private GlazeFiringService glazeFiringService;

    private Kiln kiln;
    private GlazeFiring firing;
    private ProductTransaction glost;
    private Machine machine;
    private Resource electricity;
    private Resource gas;
    private Glaze glaze;
    private Long kilnId = 1L;
    private Long firingId = 1L;
    private Long glostId = 1L;
    private Long machineId = 1L;
    private Long glazeId = 1L;

    @BeforeEach
    void setUp() {

        kiln = new Kiln();
        kiln.setId(kilnId);
        kiln.setName("Forno de Esmalte");

        Machine machine1 = new Machine();
        machine1.setId(2L);
        machine1.setCreatedAt(Instant.now());
        machine1.setUpdatedAt(null);
        machine1.setName("Máquina");
        machine1.setPower(10);
        
        kiln.getMachines().add(machine1);

        firing = new GlazeFiring();
        firing.setId(firingId);
        firing.setTemperature(800.0);
        firing.setBurnTime(6.0);
        firing.setCoolingTime(3.0);
        firing.setGasConsumption(8.0);
        firing.setKiln(kiln);

        Product product = new Product();
        product.setId(1L);
        product.setName("Vaso Esmaltado");

        glost = new ProductTransaction();
        glost.setId(glostId);
        glost.setState(ProductState.BISCUIT);
        glost.setProduct(product);

        machine = new Machine();
        machine.setId(machineId);
        machine.setName("Máquina de Esmalte");
        machine.setPower(3.0);

        electricity = new Resource();
        electricity.setCategory(ResourceCategory.ELECTRICITY);
        electricity.setUnitValue(new BigDecimal("0.60"));

        gas = new Resource();
        gas.setCategory(ResourceCategory.GAS);
        gas.setUnitValue(new BigDecimal("3.50"));

        glaze = new Glaze();
        glaze.setId(glazeId);
        glaze.setColor("Azul Cobalto");
    }

    @Test
    void findAllByParentId_WhenKilnExists_ShouldReturnList() {
        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByKilnId(kilnId)).thenReturn(List.of(firing));

        List<FiringListDTO> result = glazeFiringService.findAllByParentId(kilnId);

        assertEquals(1, result.size());
        assertEquals(firingId, result.getFirst().id());
    }

    @Test
    void findById_WhenExists_ShouldReturnFiring() {
        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));

        GlazeFiringResponseDTO result = glazeFiringService.findById(kilnId, firingId);

        assertEquals(firingId, result.id());
        verify(firingRepository).findByIdAndKilnId(firingId, kilnId);
    }

    @Test
    void create_WithValidDataAndGlaze_ShouldCreateFiring() {

        GlazeTransaction glazeTx = new GlazeTransaction();
        glazeTx.setGlaze(glaze);
        glazeTx.setQuantity(2.5);

        when(glazeTransactionService.createEntity(eq(glazeId), eq(2.5), any(ProductTransaction.class))).thenReturn(glazeTx);

        when(kilnRepository.findById(kilnId)).thenReturn(Optional.of(kiln));
        when(productTransactionRepository.findById(glostId)).thenReturn(Optional.of(glost));
        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        when(firingRepository.save(any())).thenReturn(firing);

        GlazeFiringRequestDTO dto = new GlazeFiringRequestDTO(
            800.0, 6.0, 3.0, 8.0,
            List.of(new GlostRequestDTO(glostId, glazeId, 2.5)),
            Collections.emptyList()
        );
        GlazeFiringResponseDTO result = glazeFiringService.create(kilnId, dto);

        assertEquals("Azul Cobalto", result.glosts().getFirst().glazeColor());
        assertEquals(2.5, result.glosts().getFirst().quantity());
    }

    @Test
    void update_WhenAddingGlostFromOtherFiring_ShouldThrowException() {
        GlazeFiring otherFiring = new GlazeFiring();
        otherFiring.setId(2L);
        ProductTransaction otherGlost = new ProductTransaction();
        otherGlost.setId(2L);
        otherGlost.setGlazeFiring(otherFiring);

        GlostRequestDTO glostDTO = new GlostRequestDTO(2L, null, null);
        GlazeFiringRequestDTO dto = new GlazeFiringRequestDTO(
            850.0, 7.0, 4.0, 9.0,
            List.of(glostDTO),
            Collections.emptyList()
        );

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
        when(productTransactionRepository.findById(2L)).thenReturn(Optional.of(otherGlost));

        assertThrows(ResourceNotFoundException.class, () -> glazeFiringService.update(kilnId, firingId, dto));
    }

    @Test
    void update_WhenRemovingGlosts_ShouldResetState() {

        when(resourceRepository.findByCategory(ResourceCategory.ELECTRICITY)).thenReturn(Optional.of(electricity));
        when(resourceRepository.findByCategory(ResourceCategory.GAS)).thenReturn(Optional.of(gas));
        
        when(firingRepository.save(any(GlazeFiring.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductTransaction existingGlost = new ProductTransaction();
        existingGlost.setId(2L);
        existingGlost.setGlazeFiring(firing);
        existingGlost.setState(ProductState.GLAZED);
        firing.getGlosts().add(existingGlost);

        GlazeFiringRequestDTO dto = new GlazeFiringRequestDTO(
            850.0, 7.0, 4.0, 9.0,
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));

        GlazeFiringResponseDTO result = glazeFiringService.update(kilnId, firingId, dto);

        assertNotNull(result);
        assertEquals(ProductState.BISCUIT, existingGlost.getState());
        assertNull(existingGlost.getGlazeFiring());
    }

    @Test
    void update_WithInvalidMachine_ShouldThrowException() {
        GlazeFiringRequestDTO dto = new GlazeFiringRequestDTO(
            850.0, 7.0, 4.0, 9.0,
            Collections.emptyList(),
            List.of(new FiringMachineUsageRequestDTO(2.0, 999L))
        );

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));
        when(machineRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> glazeFiringService.update(kilnId, firingId, dto));
    }

    @Test
    void delete_ShouldResetGlostsAndTransactions() {
        GlazeTransaction glazeTx = new GlazeTransaction();
        glost.setGlazeTransaction(glazeTx);
        glost.setGlazeFiring(firing);
        firing.getGlosts().add(glost);

        when(kilnRepository.existsById(kilnId)).thenReturn(true);
        when(firingRepository.findByIdAndKilnId(firingId, kilnId)).thenReturn(Optional.of(firing));

        glazeFiringService.delete(kilnId, firingId);

        assertEquals(ProductState.BISCUIT, glost.getState());
        assertNull(glost.getGlazeTransaction());
        verify(firingRepository).delete(firing);
    }
}