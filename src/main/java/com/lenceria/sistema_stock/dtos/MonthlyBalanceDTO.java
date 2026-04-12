package com.lenceria.sistema_stock.dtos;

import java.math.BigDecimal;

public class MonthlyBalanceDTO {

    private Long totalVentasRealizadas;
    private BigDecimal ingresosVentas;
    private int prendasIngresadas;
    private int prendasVendidas;
    private BigDecimal egresosCompras;
    private Long totalComprasRealizadas;
    private BigDecimal gastosAdministrativos;
    private int cantGastosAdmin;
    private BigDecimal ingresosTotales;
    private BigDecimal egresosTotales;
    private BigDecimal utilidadNeta;
    private BigDecimal deudaPendienteProveedores;

    //Constructor default
    public MonthlyBalanceDTO(){
        this.totalVentasRealizadas = 0L;
        this.totalComprasRealizadas = 0L;
        this.ingresosVentas = BigDecimal.ZERO;
        this.egresosCompras = BigDecimal.ZERO;
        this.prendasIngresadas = 0;
        this.prendasVendidas = 0;
        this.gastosAdministrativos = BigDecimal.ZERO;
        this.cantGastosAdmin = 0;
        this.ingresosTotales = BigDecimal.ZERO;
        this.egresosTotales = BigDecimal.ZERO;
        this.utilidadNeta = BigDecimal.ZERO;
        this.deudaPendienteProveedores = BigDecimal.ZERO;

    }

    // Getters y Setters
    public Long getTotalVentasRealizadas() { return totalVentasRealizadas; }
    public void setTotalVentasRealizadas(Long totalVentasRealizadas) { this.totalVentasRealizadas = totalVentasRealizadas; }

    public Long getTotalComprasRealizadas() { return totalComprasRealizadas; }
    public void setTotalComprasRealizadas(Long totalComprasRealizadas) { this.totalComprasRealizadas = totalComprasRealizadas; }

    public BigDecimal getIngresosVentas() { return ingresosVentas; }
    public void setIngresosVentas(BigDecimal ingresosVentas) { this.ingresosVentas = ingresosVentas; }

    public BigDecimal getEgresosCompras() { return egresosCompras; }
    public void setEgresosCompras(BigDecimal egresosCompras) { this.egresosCompras = egresosCompras; }

    public int getPrendasIngresadas() { return prendasIngresadas; }
    public void setPrendasIngresadas(int prendasIngresadas) { this.prendasIngresadas = prendasIngresadas; }

    public int getPrendasVendidas() { return prendasVendidas; }
    public void setPrendasVendidas(int prendasVendidas) { this.prendasVendidas = prendasVendidas; }

    public BigDecimal getGastosAdministrativos() { return gastosAdministrativos; }
    public void setGastosAdministrativos(BigDecimal gastosAdministrativos) { this.gastosAdministrativos = gastosAdministrativos; }

    public int getCantGastosAdmin() { return cantGastosAdmin; }
    public void setCantGastosAdmin(int cantGastosAdmin) { this.cantGastosAdmin = cantGastosAdmin; }

    public BigDecimal getIngresosTotales() { return ingresosTotales; }
    public void setIngresosTotales(BigDecimal ingresosTotales) { this.ingresosTotales = ingresosTotales; }

    public BigDecimal getEgresosTotales() { return egresosTotales; }
    public void setEgresosTotales(BigDecimal egresosTotales) { this.egresosTotales = egresosTotales; }


    public BigDecimal getUtilidadNeta() { return utilidadNeta; }
    public void setUtilidadNeta(BigDecimal utilidadNeta) { this.utilidadNeta = utilidadNeta; }

    public BigDecimal getDeudaPendienteProveedores() { return deudaPendienteProveedores; }
    public void setDeudaPendienteProveedores(BigDecimal deudaPendienteProveedores) { this.deudaPendienteProveedores = deudaPendienteProveedores; }


}
