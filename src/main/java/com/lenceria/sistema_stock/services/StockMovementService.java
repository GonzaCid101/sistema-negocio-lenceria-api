package com.lenceria.sistema_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.entities.StockMovement;
import com.lenceria.sistema_stock.entities.Variant;
import com.lenceria.sistema_stock.repositories.StockMovementRepository;
import com.lenceria.sistema_stock.repositories.VariantRepository;

import jakarta.transaction.Transactional;

@Service //Logica de negocio
public class StockMovementService {
    private final StockMovementRepository movementRepository;
    private final VariantRepository variantRepository;

    //Inyeccion de dependencias
    public StockMovementService(StockMovementRepository movementRepository, VariantRepository variantRepository){
        this.movementRepository = movementRepository; //Inyeccion de repositorio listo para usar
        this.variantRepository = variantRepository;
    }
    
    // Metodo para registrar un movimiento
    @Transactional
    public StockMovement registrarMovimiento(Long variantId, String tipoMovimiento, int cantidad, String razon){
        //Validaciones
        Variant variante = variantRepository.findById(variantId).orElseThrow(()-> new RuntimeException("Variante no encontrada."));
        if (cantidad <= 0){
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }
        if(!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA")){
            throw new IllegalArgumentException("El tipo de movimiento solo puede ser ENTRADA o SALIDA.");
        }

        //Creacion del objeto
        StockMovement nuevoMovimiento = new StockMovement(variante, tipoMovimiento, cantidad, razon);

        //Actualiza el stock de la variante

        if (variante.getStock() == null) {
            variante.setStock(0);
        }
        
        if ("ENTRADA".equalsIgnoreCase(tipoMovimiento)) {
            variante.setStock(variante.getStock() + cantidad);
        } else if ("SALIDA".equalsIgnoreCase(tipoMovimiento)) {
            // Un mini escudo de seguridad para no tener stock negativo
            if (variante.getStock() < cantidad) {
                throw new RuntimeException("No hay suficiente stock para sacar esa cantidad.");
            }
            variante.setStock(variante.getStock() - cantidad);
        }
        
        //Guarda en BD
        return movementRepository.save(nuevoMovimiento);
    }


    public List<StockMovement> listaMovimientos(Integer anio, Integer mes){
        if (anio != null && mes != null) {
            return movementRepository.buscarPorAnioYMes(anio, mes);
        }
        // Usar findTop200 para mejor rendimiento - no cargar toda la BD
        return movementRepository.findTop200ByOrderByCreatedAtDesc();
    }
}

