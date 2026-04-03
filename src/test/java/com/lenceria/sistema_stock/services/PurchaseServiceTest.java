package com.lenceria.sistema_stock.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lenceria.sistema_stock.dtos.PurchaseRequestDTO.ItemCompraDTO;
import com.lenceria.sistema_stock.entities.*;
import com.lenceria.sistema_stock.repositories.PurchaseRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private StockMovementService stockService;

    @InjectMocks
    private PurchaseService purchaseService;

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
    void registrarCompra_MultiplesItems_DebeCalcularTotalCorrecto() {
        // Given
        ItemCompraDTO item1 = new ItemCompraDTO();
        item1.setVariantId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("80.00"));
        
        ItemCompraDTO item2 = new ItemCompraDTO();
        item2.setVariantId(2L);
        item2.setQuantity(3);
        item2.setUnitPrice(new BigDecimal("50.00")); // Usar precio explicito
        
        List<ItemCompraDTO> items = Arrays.asList(item1, item2);
        
        when(variantRepository.findAllById(any())).thenReturn(Arrays.asList(variant1, variant2));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> {
            Purchase p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, items);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("310.00"), result.getTotalAmount()); // 2x80 + 3x50
        assertEquals(MetodoPago.EFECTIVO, result.getPaymentMethod());
        assertEquals(2, result.getDetails().size());
    }

    @Test
    void registrarCompra_CostoUnitario_DebeUsarPrecioProporcionado() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("75.50"));
        
        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.TARJETA, 
            Collections.singletonList(item));

        // Then
        assertEquals(new BigDecimal("151.00"), result.getTotalAmount()); // 2 x 75.50
        PurchaseDetail detail = result.getDetails().get(0);
        assertEquals(new BigDecimal("75.50"), detail.getUnitPrice());
    }

    @Test
    void registrarCompra_VarianteNoExiste_DebeLanzarExcepcion() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(99L);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("50.00"));
        
        when(variantRepository.findAllById(any())).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.registrarCompra(MetodoPago.EFECTIVO, Collections.singletonList(item));
        });
        assertTrue(exception.getMessage().contains("Id no encontrada"));
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void registrarCompra_SinItems_DebeCrearCompraVacia() {
        // Given
        when(variantRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> {
            Purchase p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, Collections.emptyList());

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertTrue(result.getDetails().isEmpty());
    }

    @Test
    void registrarCompra_DebeRegistrarEntradaStock() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("80.00"));
        
        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        purchaseService.registrarCompra(MetodoPago.EFECTIVO, Collections.singletonList(item));

        // Then
        verify(stockService).registrarMovimiento(1L, "ENTRADA", 10, "COMPRA");
    }

    @Test
    void registrarCompra_DebeAsociarDetallesALaCompra() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(5);
        item.setUnitPrice(new BigDecimal("100.00"));
        
        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.TRANSFERENCIA, 
            Collections.singletonList(item));

        // Then
        assertNotNull(result.getDetails());
        assertEquals(1, result.getDetails().size());
        assertEquals(result, result.getDetails().get(0).getPurchase());
    }

    @Test
    void registrarCompra_DebeUsarFechaActual() {
        // Given
        when(variantRepository.findAllById(any())).thenReturn(Collections.emptyList());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, Collections.emptyList());

        // Then
        assertNotNull(result.getDate());
        assertTrue(java.time.Duration.between(result.getDate(), 
            java.time.LocalDateTime.now()).getSeconds() < 1);
    }

    @Test
    void registrarCompra_DebeCalcularSubtotalCorrecto() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal("80.00"));
        
        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.TARJETA, 
            Collections.singletonList(item));

        // Then
        assertEquals(new BigDecimal("240.00"), result.getTotalAmount()); // 3 x 80
    }
}
