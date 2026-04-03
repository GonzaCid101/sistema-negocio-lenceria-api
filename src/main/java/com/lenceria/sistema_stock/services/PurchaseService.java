package com.lenceria.sistema_stock.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lenceria.sistema_stock.dtos.PurchaseRequestDTO.ItemCompraDTO;
import com.lenceria.sistema_stock.entities.MetodoPago;
import com.lenceria.sistema_stock.entities.Purchase;
import com.lenceria.sistema_stock.entities.PurchaseDetail;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.repositories.PurchaseRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;


@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final VariantRepository variantRepository;
    private final StockMovementService stockService;

    public PurchaseService(PurchaseRepository purchaseRepository, VariantRepository variantRepository, StockMovementService stockService){
        this.purchaseRepository = purchaseRepository;
        this.variantRepository = variantRepository;
        this.stockService = stockService;
    }

    @Transactional //Control de errores. Evita que el sistema falle en mitad de una venta.
    public Purchase registrarCompra(MetodoPago metodoPago, List<ItemCompraDTO> itemsAComprar){
        Purchase nuevaCompra = new Purchase(metodoPago);

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
        for(ItemCompraDTO item : itemsAComprar){  //itemsAVender: Clave = ID, Valor = Cantidad
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

        //Guarda la compra
        return purchaseRepository.save(nuevaCompra);
    }

    public List<Purchase> listaCompras(Integer anio, Integer mes){
        if (anio != null && mes != null) {
            return purchaseRepository.buscarPorAnioYMes(anio, mes);
        }
        return purchaseRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
}
