package com.lenceria.sistema_stock.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lenceria.sistema_stock.dtos.PurchasePaymentDTO;
import com.lenceria.sistema_stock.dtos.PurchaseResponseDTO;
import com.lenceria.sistema_stock.entities.*;
import com.lenceria.sistema_stock.repositories.PurchasePaymentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lenceria.sistema_stock.dtos.PurchaseRequestDTO.ItemCompraDTO;
import com.lenceria.sistema_stock.repositories.PurchaseRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;


@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final VariantRepository variantRepository;
    private final StockMovementService stockService;
    private final PurchasePaymentRepository purchasePaymentRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, VariantRepository variantRepository, 
                          StockMovementService stockService, PurchasePaymentRepository purchasePaymentRepository){
        this.purchaseRepository = purchaseRepository;
        this.variantRepository = variantRepository;
        this.stockService = stockService;
        this.purchasePaymentRepository = purchasePaymentRepository;
    }

    @Transactional //Control de errores. Evita que el sistema falle en mitad de una venta.
    public Purchase registrarCompra(MetodoPago metodoPago, String supplier, String invoiceNumber, 
                                     List<ItemCompraDTO> itemsAComprar){
        Purchase nuevaCompra = new Purchase(metodoPago);
        nuevaCompra.setSupplier(supplier);
        nuevaCompra.setInvoiceNumber(invoiceNumber);

        //Obtiene las variantes de la lista de compra
        List<Long> variantIds = itemsAComprar.stream()
                .map(ItemCompraDTO::getVariantId)
                .toList();

        List<Variant> variantesEnBD = variantRepository.findAllById(variantIds);
        Map<Long,Variant> variantesConId = new HashMap<>();

        for (Variant variant : variantesEnBD) {
            variantesConId.put(variant.getId(), variant);
        }

        //Recorre la lista de compra
        for(ItemCompraDTO item : itemsAComprar){ //itemsAVender: Clave = ID, Valor = Cantidad
            Long variantId = item.getVariantId();
            Integer cantidadComprada = item.getQuantity();
            BigDecimal costoUnitario = item.getUnitPrice();

            Variant variante = variantesConId.get(variantId);

            if (variante == null){
                throw new IllegalArgumentException("Id no encontrada." + variantId);
            }

            //Suma de stock
            stockService.registrarMovimiento(variantId, "ENTRADA", cantidadComprada, "COMPRA");

            PurchaseDetail renglon = new PurchaseDetail(nuevaCompra, variante, cantidadComprada, costoUnitario);

            //Agrega el renglon
            nuevaCompra.getDetails().add(renglon);

            //precio * cantidad
            BigDecimal subtotal = costoUnitario.multiply(new BigDecimal(cantidadComprada));
            nuevaCompra.addAmount(subtotal);


        }

        // Si el método de pago no es CUENTA_CORRIENTE, se paga completo inmediatamente
        if (metodoPago != MetodoPago.CUENTA_CORRIENTE) {
            // Crear un pago automático por el monto total
            PurchasePayment pagoInicial = new PurchasePayment(nuevaCompra, nuevaCompra.getTotalAmount(), metodoPago);
            nuevaCompra.addPayment(pagoInicial);
            nuevaCompra.setStatus(PurchaseStatus.PAGADA);
        }

        //Guarda la compra
        return purchaseRepository.save(nuevaCompra);
    }

    @Transactional
    public Purchase registrarPayment(Long purchaseId, PurchasePaymentDTO paymentDTO) {
        // Validar monto > 0
        if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }

        // Buscar la compra
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada con id: " + purchaseId));

        // Verificar que no esté anulada
        if (purchase.getStatus() == PurchaseStatus.ANULADA) {
            throw new IllegalStateException("No se pueden registrar pagos en una compra anulada");
        }

        // Verificar que no esté ya pagada completamente
        if (purchase.getStatus() == PurchaseStatus.PAGADA) {
            throw new IllegalStateException("La compra ya está completamente pagada");
        }

        // Calcular deuda actual
        BigDecimal pendingAmount = purchase.getPendingAmount();

        // Validar que el monto no supere la deuda
        if (paymentDTO.getAmount().compareTo(pendingAmount) > 0) {
            throw new IllegalArgumentException(
                String.format("El monto del pago ($%.2f) supera la deuda actual ($%.2f)", 
                    paymentDTO.getAmount(), pendingAmount));
        }

        // Crear y guardar el pago
        PurchasePayment payment = new PurchasePayment();
        payment.setPurchase(purchase);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());

        purchase.addPayment(payment);
        purchasePaymentRepository.save(payment);

        // Si después del pago no queda deuda, actualizar estado a PAGADA
        if (purchase.getPendingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            purchase.setStatus(PurchaseStatus.PAGADA);
        }

        return purchaseRepository.save(purchase);
    }

    public List<PurchaseResponseDTO> listaCompras(Integer anio, Integer mes){
        List<Purchase> compras;
        if (anio != null && mes != null) {
            compras = purchaseRepository.buscarPorAnioYMes(anio, mes);
        } else {
            compras = purchaseRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        }
        
        return compras.stream()
                .map(PurchaseResponseDTO::new)
                .collect(Collectors.toList());
    }

    public PurchaseResponseDTO obtenerCompraPorId(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada con id: " + id));
        return new PurchaseResponseDTO(purchase);
    }
}
