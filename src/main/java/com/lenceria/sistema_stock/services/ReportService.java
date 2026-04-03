package com.lenceria.sistema_stock.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.dtos.MonthlyBalanceDTO;
import com.lenceria.sistema_stock.repositories.ExpenseRepository;
import com.lenceria.sistema_stock.repositories.PurchaseRepository;
import com.lenceria.sistema_stock.repositories.SaleRepository;
import com.lenceria.sistema_stock.repositories.StockMovementRepository;

@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private final StockMovementRepository movementRepository;
    private final PurchaseRepository purchaseRepository;
    private final ExpenseRepository expenseRepository;

    public ReportService(SaleRepository saleRepository, StockMovementRepository movementRepository, PurchaseRepository purchaseRepository, ExpenseRepository expenseRepository){
        this.saleRepository = saleRepository;
        this.movementRepository = movementRepository;
        this.purchaseRepository = purchaseRepository;
        this.expenseRepository = expenseRepository;
    }

    public MonthlyBalanceDTO generarBalanceMensual(int anio, int mes){
        MonthlyBalanceDTO balance = new MonthlyBalanceDTO();

        //Rango de fechas
        YearMonth anioMes = YearMonth.of(anio, mes);
        LocalDateTime inicio = anioMes.atDay(1).atStartOfDay();
        LocalDateTime fin = anioMes.atEndOfMonth().atTime(23,59,59);

        //Calculo de ingresos y egresos
        BigDecimal ingresosVentas = saleRepository.sumarIngresosPorFecha(inicio, fin);
        balance.setIngresosVentas(ingresosVentas);

        BigDecimal egresosCompras = purchaseRepository.sumarEgresosPorFecha(inicio, fin);
        balance.setEgresosCompras(egresosCompras);


        //Calculo de movimientos de mercaderia
        Integer prendasVendidas = movementRepository.calcularStock(inicio, fin, "SALIDA", "VENTA");
        balance.setPrendasVendidas(prendasVendidas);

        Integer prendasCompradas = movementRepository.calcularStock(inicio, fin, "ENTRADA", "COMPRA");
        balance.setPrendasIngresadas(prendasCompradas);
        
        //Ventas y Compras realizadas
        Long ventasRealizadas = saleRepository.ventasRealizadas(inicio, fin);
        balance.setTotalVentasRealizadas(ventasRealizadas);

        Long comprasRealizadas = purchaseRepository.comprasRealizadas(inicio, fin);
        balance.setTotalComprasRealizadas(comprasRealizadas);

        //Gastos varios

        BigDecimal gastosAdministrativos = expenseRepository.sumarExpensasPorFecha(inicio, fin);
        balance.setGastosAdministrativos(gastosAdministrativos);

        Integer cantGastosAdmin = expenseRepository.contarExpensasRealizadas(inicio, fin);
        balance.setCantGastosAdmin(cantGastosAdmin);

        //Utilidad
        BigDecimal ingresosTotales = ingresosVentas;
        balance.setIngresosTotales(ingresosTotales);
        BigDecimal egresosTotales = egresosCompras.add(gastosAdministrativos);
        balance.setEgresosTotales(egresosTotales);

        BigDecimal utilidadNeta = ingresosVentas.subtract(egresosTotales);
        balance.setUtilidadNeta(utilidadNeta);

        return balance;
    }
}
