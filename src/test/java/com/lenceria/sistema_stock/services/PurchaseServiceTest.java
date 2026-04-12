package com.lenceria.sistema_stock.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.lenceria.sistema_stock.dtos.PurchasePaymentDTO;
import com.lenceria.sistema_stock.repositories.PurchasePaymentRepository;
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

    @Mock
    private PurchasePaymentRepository purchasePaymentRepository;

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
        item2.setUnitPrice(new BigDecimal("50.00"));

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
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, "Proveedor Test", "FAC-001", items);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("310.00"), result.getTotalAmount());
        assertEquals(MetodoPago.EFECTIVO, result.getPaymentMethod());
        assertEquals("Proveedor Test", result.getSupplier());
        assertEquals("FAC-001", result.getInvoiceNumber());
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
        Purchase result = purchaseService.registrarCompra(MetodoPago.TARJETA, "Mayorista", "001-0001",
            Collections.singletonList(item));

        // Then
        assertEquals(new BigDecimal("151.00"), result.getTotalAmount());
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
            purchaseService.registrarCompra(MetodoPago.EFECTIVO, "Proveedor", "FAC-001", Collections.singletonList(item));
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
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, "General", "S/N", Collections.emptyList());

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
        purchaseService.registrarCompra(MetodoPago.EFECTIVO, "Stock Test", "STK-001", Collections.singletonList(item));

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
        Purchase result = purchaseService.registrarCompra(MetodoPago.TRANSFERENCIA, "Detalle Test", "DET-001",
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
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, "Fecha Test", "FCH-001", Collections.emptyList());

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
        Purchase result = purchaseService.registrarCompra(MetodoPago.TARJETA, "Subtotal Test", "SUB-001",
            Collections.singletonList(item));

        // Then
        assertEquals(new BigDecimal("240.00"), result.getTotalAmount());
    }

    // ========== TESTS NUEVOS - SISTEMA DE CUENTAS POR PAGAR ==========

    @Test
    void registrarCompra_CuentaCorriente_DebeQuedarPendiente() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("100.00"));

        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.CUENTA_CORRIENTE, "Proveedor CC", "CC-001",
            Collections.singletonList(item));

        // Then
        assertEquals(PurchaseStatus.PENDIENTE, result.getStatus());
        assertTrue(result.getPayments().isEmpty());
        assertEquals(new BigDecimal("200.00"), result.getPendingAmount());
    }

    @Test
    void registrarCompra_Efectivo_DebeQuedarPagada() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("100.00"));

        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchasePaymentRepository.save(any(PurchasePayment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.EFECTIVO, "Proveedor Efectivo", "EF-001",
            Collections.singletonList(item));

        // Then
        assertEquals(PurchaseStatus.PAGADA, result.getStatus());
        assertEquals(1, result.getPayments().size());
        assertEquals(new BigDecimal("200.00"), result.getPayments().get(0).getAmount());
        assertEquals(BigDecimal.ZERO, result.getPendingAmount());
    }

    @Test
    void registrarCompra_Transferencia_DebeQuedarPagada() {
        // Given
        ItemCompraDTO item = new ItemCompraDTO();
        item.setVariantId(1L);
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("150.00"));

        when(variantRepository.findAllById(any())).thenReturn(Collections.singletonList(variant1));
        when(stockService.registrarMovimiento(anyLong(), anyString(), anyInt(), anyString()))
            .thenReturn(new StockMovement());
        when(purchasePaymentRepository.save(any(PurchasePayment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Purchase result = purchaseService.registrarCompra(MetodoPago.TRANSFERENCIA, "Proveedor Transf", "TR-001",
            Collections.singletonList(item));

        // Then
        assertEquals(PurchaseStatus.PAGADA, result.getStatus());
        assertEquals(MetodoPago.TRANSFERENCIA, result.getPayments().get(0).getPaymentMethod());
    }

    @Test
    void registrarPago_Parcial_DebeActualizarEstado() {
        // Given
        Purchase compraPendiente = new Purchase();
        compraPendiente.setId(1L);
        compraPendiente.setTotalAmount(new BigDecimal("500.00"));
        compraPendiente.setStatus(PurchaseStatus.PENDIENTE);
        compraPendiente.setSupplier("Proveedor Test");

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPendiente));
        when(purchasePaymentRepository.save(any(PurchasePayment.class))).thenAnswer(inv -> {
            PurchasePayment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(new BigDecimal("200.00"));
        pago.setPaymentMethod(MetodoPago.EFECTIVO);

        // When
        Purchase result = purchaseService.registrarPayment(1L, pago);

        // Then
        assertEquals(PurchaseStatus.PENDIENTE, result.getStatus());
        assertEquals(new BigDecimal("300.00"), result.getPendingAmount());
        assertEquals(1, result.getPayments().size());
    }

    @Test
    void registrarPago_Completo_DebeMarcarComoPagada() {
        // Given
        Purchase compraPendiente = new Purchase();
        compraPendiente.setId(1L);
        compraPendiente.setTotalAmount(new BigDecimal("500.00"));
        compraPendiente.setStatus(PurchaseStatus.PENDIENTE);
        compraPendiente.setSupplier("Proveedor Test");

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPendiente));
        when(purchasePaymentRepository.save(any(PurchasePayment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(new BigDecimal("500.00"));
        pago.setPaymentMethod(MetodoPago.TRANSFERENCIA);

        // When
        Purchase result = purchaseService.registrarPayment(1L, pago);

        // Then
        assertEquals(PurchaseStatus.PAGADA, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getPendingAmount());
    }

    @Test
    void registrarPago_MontoCero_DebeLanzarExcepcion() {
        // Given
        Purchase compraPendiente = new Purchase();
        compraPendiente.setId(1L);
        compraPendiente.setTotalAmount(new BigDecimal("500.00"));
        compraPendiente.setStatus(PurchaseStatus.PENDIENTE);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPendiente));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(BigDecimal.ZERO);
        pago.setPaymentMethod(MetodoPago.EFECTIVO);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.registrarPayment(1L, pago);
        });
        assertTrue(exception.getMessage().contains("mayor a cero"));
    }

    @Test
    void registrarPago_MontoMayorQueDeuda_DebeLanzarExcepcion() {
        // Given
        Purchase compraPendiente = new Purchase();
        compraPendiente.setId(1L);
        compraPendiente.setTotalAmount(new BigDecimal("500.00"));
        compraPendiente.setStatus(PurchaseStatus.PENDIENTE);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPendiente));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(new BigDecimal("600.00"));
        pago.setPaymentMethod(MetodoPago.EFECTIVO);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.registrarPayment(1L, pago);
        });
        assertTrue(exception.getMessage().contains("supera la deuda"));
    }

    @Test
    void registrarPago_CompraPagada_DebeLanzarExcepcion() {
        // Given
        Purchase compraPagada = new Purchase();
        compraPagada.setId(1L);
        compraPagada.setTotalAmount(new BigDecimal("500.00"));
        compraPagada.setStatus(PurchaseStatus.PAGADA);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPagada));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(new BigDecimal("100.00"));
        pago.setPaymentMethod(MetodoPago.EFECTIVO);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            purchaseService.registrarPayment(1L, pago);
        });
        assertTrue(exception.getMessage().contains("completamente pagada"));
    }

    @Test
    void registrarPago_CompraAnulada_DebeLanzarExcepcion() {
        // Given
        Purchase compraAnulada = new Purchase();
        compraAnulada.setId(1L);
        compraAnulada.setTotalAmount(new BigDecimal("500.00"));
        compraAnulada.setStatus(PurchaseStatus.ANULADA);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraAnulada));

        PurchasePaymentDTO pago = new PurchasePaymentDTO();
        pago.setAmount(new BigDecimal("100.00"));
        pago.setPaymentMethod(MetodoPago.EFECTIVO);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            purchaseService.registrarPayment(1L, pago);
        });
        assertTrue(exception.getMessage().contains("anulada"));
    }

    @Test
    void registrarPago_MultiplesPagos_DebeAcumular() {
        // Given
        Purchase compraPendiente = new Purchase();
        compraPendiente.setId(1L);
        compraPendiente.setTotalAmount(new BigDecimal("1000.00"));
        compraPendiente.setStatus(PurchaseStatus.PENDIENTE);
        compraPendiente.setSupplier("Proveedor Test");

        // Primer pago
        PurchasePayment primerPago = new PurchasePayment();
        primerPago.setAmount(new BigDecimal("300.00"));
        primerPago.setPaymentMethod(MetodoPago.EFECTIVO);
        compraPendiente.addPayment(primerPago);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(compraPendiente));
        when(purchasePaymentRepository.save(any(PurchasePayment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchasePaymentDTO segundoPago = new PurchasePaymentDTO();
        segundoPago.setAmount(new BigDecimal("400.00"));
        segundoPago.setPaymentMethod(MetodoPago.TRANSFERENCIA);

        // When
        Purchase result = purchaseService.registrarPayment(1L, segundoPago);

        // Then
        assertEquals(PurchaseStatus.PENDIENTE, result.getStatus());
        assertEquals(new BigDecimal("300.00"), result.getPendingAmount());
        assertEquals(2, result.getPayments().size());
    }

    @Test
    void getPendingAmount_DebeCalcularCorrectamente() {
        // Given
        Purchase compra = new Purchase();
        compra.setTotalAmount(new BigDecimal("1000.00"));

        PurchasePayment pago1 = new PurchasePayment();
        pago1.setAmount(new BigDecimal("400.00"));
        compra.addPayment(pago1);

        PurchasePayment pago2 = new PurchasePayment();
        pago2.setAmount(new BigDecimal("300.00"));
        compra.addPayment(pago2);

        // When & Then
        assertEquals(new BigDecimal("300.00"), compra.getPendingAmount());
    }
}
