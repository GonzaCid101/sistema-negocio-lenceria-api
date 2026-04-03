package com.lenceria.sistema_stock.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lenceria.sistema_stock.dtos.MonthlyBalanceDTO;
import com.lenceria.sistema_stock.services.ReportService;

@CrossOrigin(origins = "*")
@RestController //Toma el pedido del FrontEnd, lo lleva a los servicios y devuelve el resultado.
@RequestMapping("/api/reportes") //URL base para comunicarse
public class ReportController {
    private final ReportService reportService;

    //Inyecta el servicio en el controlador
    public ReportController(ReportService reportService){
        this.reportService = reportService;
    }
    @GetMapping("/balance") //Reacciona cuando alguien entre a la URL
    public MonthlyBalanceDTO obtenerBalance(@RequestParam int anio, @RequestParam int mes) { //Toma los datos enviados por la URL
        return reportService.generarBalanceMensual(anio, mes); //Devuelve el reporte
    }
    
}
