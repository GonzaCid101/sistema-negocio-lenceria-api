package com.lenceria.sistema_stock.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lenceria.sistema_stock.entities.MetodoPago;
import com.lenceria.sistema_stock.entities.Sale;
import com.lenceria.sistema_stock.entities.SaleDetail;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.repositories.SaleRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;


@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final VariantRepository variantRepository;
    private final StockMovementService stockService;

    public SaleService(SaleRepository saleRepository, VariantRepository variantRepository, StockMovementService stockService){
        this.saleRepository = saleRepository;
        this.variantRepository = variantRepository;
        this.stockService = stockService;
    }

    @Transactional //Control de errores. Evita que el sistema falle en mitad de una venta.
    public Sale registrarVenta(MetodoPago metodoPago, Map<Long, Integer> itemsAVender, String seller){
        Sale nuevaVenta = new Sale(metodoPago, seller);

        List<Variant> variantesEnBD = variantRepository.findAllById(itemsAVender.keySet());
        Map<Long,Variant> variantesConId = new HashMap<>();

        for (Variant variant : variantesEnBD) {
            variantesConId.put(variant.getId(), variant);
        }

        //Recorre el "carrito"
        for(Map.Entry<Long, Integer> item : itemsAVender.entrySet()){  //itemsAVender: Clave = ID, Valor = Cantidad
            Long variantId = item.getKey();
            Integer cantidadVendida = item.getValue();
            
            Variant variante = variantesConId.get(variantId);
            if (variante == null){
                throw new IllegalArgumentException("Id no encontrada." + variantId);
            }
            
            //Descuento de stock
            stockService.registrarMovimiento(variantId, "SALIDA", cantidadVendida, "VENTA");

            //Obtiene precio para guardar venta
            BigDecimal precioCongelado = variante.getPrice();
            SaleDetail renglon = new SaleDetail(nuevaVenta, variante, cantidadVendida, precioCongelado);

            //Agrega el renglon
            nuevaVenta.getDetails().add(renglon);

            //precio * cantidad
            BigDecimal subtotal = precioCongelado.multiply(new BigDecimal(cantidadVendida));
            nuevaVenta.addAmount(subtotal);


        }

        //Guarda la venta
        return saleRepository.save(nuevaVenta);
    }

    public List<Sale> listaVentas(Integer anio, Integer mes){
        if (anio != null && mes != null) {
            return saleRepository.buscarPorAnioYMes(anio, mes);
        }
        return saleRepository.findTop500ByOrderByDateDesc();
    }
}
