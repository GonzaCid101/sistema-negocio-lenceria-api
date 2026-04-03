package com.lenceria.sistema_stock.config;

import com.lenceria.sistema_stock.entities.*;
import com.lenceria.sistema_stock.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Component
public class DataSeeder implements CommandLineRunner {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final SaleRepository saleRepository;
    // Agrega los repositorios de Article, Brand, etc., si quieres generar artículos también

    public DataSeeder(ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository, SaleRepository saleRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.saleRepository = saleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (expenseRepository.count() > 0 || saleRepository.count() > 0) {
            System.out.println("✅ Base de datos poblada. Omitiendo inyección masiva.");
            return;
        }

        System.out.println("🚀 INICIANDO SIMULACIÓN GLOBAL (GASTOS Y VENTAS)...");
        Random random = new Random();

        // 1. INYECTAR 3000 GASTOS
        String[] nombresCategorias = {"Sueldos", "Impuestos", "Alquiler", "Limpieza", "Marketing"};
        List<ExpenseCategory> categorias = new ArrayList<>();
        for (String nombre : nombresCategorias) {
            ExpenseCategory cat = new ExpenseCategory();
            cat.setName(nombre);
            cat.setActive(true);
            categorias.add(expenseCategoryRepository.save(cat));
        }

        List<Expense> listaGastos = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            Expense gasto = new Expense();
            gasto.setCategory(categorias.get(random.nextInt(categorias.size())));
            
            double monto = 1000 + (49000 * random.nextDouble());
            gasto.setAmount(BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP));
            gasto.setDescription("Gasto automático #" + i);
            gasto.setDate(LocalDateTime.now().minusDays(random.nextInt(365)).minusHours(random.nextInt(24)));
            listaGastos.add(gasto);
        }
        expenseRepository.saveAll(listaGastos);

        // 2. INYECTAR 5000 VENTAS (Para probar el Balance)
        List<Sale> listaVentas = new ArrayList<>();
        String[] vendedores = {"FLAVIO", "MARISA"};
        
        // 🔥 Obtenemos automáticamente todos los valores de tu Enum 🔥
        MetodoPago[] metodosPago = MetodoPago.values();

        for (int i = 0; i < 5000; i++) {
            Sale venta = new Sale();
            
            // Elegimos un método de pago aleatorio directamente del Enum
            venta.setPaymentMethod(metodosPago[random.nextInt(metodosPago.length)]);
            venta.setSeller(vendedores[random.nextInt(vendedores.length)]);
            
            double totalVenta = 5000 + (150000 * random.nextDouble());
            venta.setTotalAmount(BigDecimal.valueOf(totalVenta).setScale(2, RoundingMode.HALF_UP));
            venta.setDate(LocalDateTime.now().minusDays(random.nextInt(365)).minusHours(random.nextInt(24)));
            
            listaVentas.add(venta);
        }
        saleRepository.saveAll(listaVentas);

        System.out.println("🏁 SIMULACIÓN COMPLETADA: 3000 Gastos y 5000 Ventas inyectadas.");
    }
}