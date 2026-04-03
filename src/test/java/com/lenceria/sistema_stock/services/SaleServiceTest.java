package com.lenceria.sistema_stock.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lenceria.sistema_stock.entities.*;
import com.lenceria.sistema_stock.repositories.SaleRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private StockMovementService stockService;

    @InjectMocks
    private SaleService saleService;

    private Variant variant1;
    private Variant variant2;

    @BeforeEach
    void setUp() {
        variant1 = new Variant();
        variant1.setId(1L);
        variant1.setPrice(new BigDecimal("100.00"));
        
        variant2 = new Variant();
        variant2.setId(2L);
        variant2.setPrice(new BigDecimal("50.00"));
    }

    @Test
    void registrarVenta_MultiplesItems_DebeCalcularTotalCorrecto() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 2); // 2 x $100 = $200
        items.put(2L, 3); // 3 x $50 = $150
        
        when(variantRepository.findAllById(anySet())).thenReturn(Arrays.asList(variant1, variant2));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        // When
        Sale result = saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Juan");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("350.00"), result.getTotalAmount());
        assertEquals(MetodoPago.EFECTIVO, result.getPaymentMethod());
        assertEquals("Juan", result.getSeller());
        assertEquals(2, result.getDetails().size());
    }

    @Test
    void registrarVenta_VarianteNoExiste_DebeLanzarExcepcion() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(99L, 1);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Juan");
        });
        assertTrue(exception.getMessage().contains("Id no encontrada"));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void registrarVenta_SinItems_DebeCrearVentaVacia() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.emptyList());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        // When
        Sale result = saleService.registrarVenta(MetodoPago.TARJETA, items, "Maria");

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertTrue(result.getDetails().isEmpty());
    }

    @Test
    void registrarVenta_DebeCongelarPrecioEnDetalle() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 1);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Sale result = saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Test");

        // Then
        SaleDetail detail = result.getDetails().get(0);
        assertEquals(new BigDecimal("100.00"), detail.getUnitPrice());
    }

    @Test
    void registrarVenta_DebeRegistrarMovimientoStock() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 5);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Test");

        // Then
        verify(stockService).registrarMovimiento(1L, "SALIDA", 5, "VENTA");
    }

    @Test
    void registrarVenta_DebeAsociarDetallesALaVenta() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 2);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Sale result = saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Test");

        // Then
        assertNotNull(result.getDetails());
        assertEquals(1, result.getDetails().size());
        assertEquals(result, result.getDetails().get(0).getSale());
    }

    @Test
    void registrarVenta_DebeUsarFechaActual() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 1);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Sale result = saleService.registrarVenta(MetodoPago.EFECTIVO, items, "Test");

        // Then
        assertNotNull(result.getDate());
        // Verificar que la fecha es reciente (menos de 1 segundo)
        assertTrue(java.time.Duration.between(result.getDate(), 
            java.time.LocalDateTime.now()).getSeconds() < 1);
    }

    @Test
    void registrarVenta_VerificarDetalleContieneVariante() {
        // Given
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 3);
        
        when(variantRepository.findAllById(anySet())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), eq("SALIDA"), anyInt(), eq("VENTA")))
            .thenReturn(new StockMovement());
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Sale result = saleService.registrarVenta(MetodoPago.TRANSFERENCIA, items, "Test");

        // Then
        SaleDetail detail = result.getDetails().get(0);
        assertEquals(variant1, detail.getVariant());
        assertEquals(3, detail.getQuantity());
    }
}
