package com.lenceria.sistema_stock.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lenceria.sistema_stock.entities.StockMovement;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.repositories.StockMovementRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository movementRepository;

    @Mock
    private VariantRepository variantRepository;

    @InjectMocks
    private StockMovementService stockMovementService;

    private Variant variant;

    @BeforeEach
    void setUp() {
        variant = new Variant();
        variant.setId(1L);
        variant.setStock(10);
    }

    @Test
    void registrarMovimiento_Entrada_DebeIncrementarStock() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StockMovement result = stockMovementService.registrarMovimiento(1L, "ENTRADA", 5, "Compra inicial");

        // Then
        assertNotNull(result);
        assertEquals(15, variant.getStock()); // 10 + 5
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void registrarMovimiento_Salida_DebeDecrementarStock() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StockMovement result = stockMovementService.registrarMovimiento(1L, "SALIDA", 3, "Venta");

        // Then
        assertNotNull(result);
        assertEquals(7, variant.getStock()); // 10 - 3
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void registrarMovimiento_SalidaSinStock_DebeLanzarExcepcion() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockMovementService.registrarMovimiento(1L, "SALIDA", 15, "Venta");
        });
        assertEquals("No hay suficiente stock para sacar esa cantidad.", exception.getMessage());
        assertEquals(10, variant.getStock()); // Stock no cambió
        verify(movementRepository, never()).save(any());
    }

    @Test
    void registrarMovimiento_CantidadCero_DebeLanzarExcepcion() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockMovementService.registrarMovimiento(1L, "ENTRADA", 0, "Test");
        });
        assertEquals("La cantidad del movimiento debe ser mayor a cero.", exception.getMessage());
        verify(movementRepository, never()).save(any());
    }

    @Test
    void registrarMovimiento_CantidadNegativa_DebeLanzarExcepcion() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockMovementService.registrarMovimiento(1L, "ENTRADA", -5, "Test");
        });
        assertEquals("La cantidad del movimiento debe ser mayor a cero.", exception.getMessage());
    }

    @Test
    void registrarMovimiento_TipoInvalido_DebeLanzarExcepcion() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockMovementService.registrarMovimiento(1L, "INVALIDO", 5, "Test");
        });
        assertEquals("El tipo de movimiento solo puede ser ENTRADA o SALIDA.", exception.getMessage());
    }

    @Test
    void registrarMovimiento_VarianteNoExiste_DebeLanzarExcepcion() {
        // Given
        when(variantRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stockMovementService.registrarMovimiento(99L, "ENTRADA", 5, "Test");
        });
        assertEquals("Variante no encontrada.", exception.getMessage());
    }

    @Test
    void registrarMovimiento_StockInicialNull_DebeTratarComoCero() {
        // Given
        variant.setStock(null);
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StockMovement result = stockMovementService.registrarMovimiento(1L, "ENTRADA", 5, "Test");

        // Then
        assertNotNull(result);
        assertEquals(5, variant.getStock()); // null tratado como 0 + 5
    }

    @Test
    void registrarMovimiento_DebeGuardarMovimientoConDatosCorrectos() {
        // Given
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        stockMovementService.registrarMovimiento(1L, "SALIDA", 3, "Venta test");

        // Then
        ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
        verify(movementRepository).save(captor.capture());
        StockMovement saved = captor.getValue();
        
        assertEquals(variant, saved.getVariant());
        assertEquals("SALIDA", saved.getMovementType());
        assertEquals(3, saved.getQuantity());
        assertEquals("Venta test", saved.getReason());
    }
}
