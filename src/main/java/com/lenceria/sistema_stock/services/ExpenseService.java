package com.lenceria.sistema_stock.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lenceria.sistema_stock.dtos.ExpenseCategoryDTO;
import com.lenceria.sistema_stock.entities.Expense;
import com.lenceria.sistema_stock.entities.ExpenseCategory;
import com.lenceria.sistema_stock.repositories.ExpenseCategoryRepository;
import com.lenceria.sistema_stock.repositories.ExpenseRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseCategoryRepository expenseCategoryRepository){
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
    }

    public List<ExpenseCategory> obtenerTodos(){
        return expenseCategoryRepository.findByActive(true);
    }

    public List<Expense> obtenerExpensas(Integer anio, Integer mes){
        if (anio != null && mes != null) {
            return expenseRepository.buscarPorAnioYMes(anio, mes);
        }
        return expenseRepository.findAll();
    }

    //Categorias
    public ExpenseCategory crearCategoria(ExpenseCategoryDTO expenseCategoryDTO){
        ExpenseCategory nuevaCategoria = new ExpenseCategory();
        nuevaCategoria.setName(expenseCategoryDTO.getName());
        nuevaCategoria.setActive(true);

        return expenseCategoryRepository.save(nuevaCategoria);
    }

    @Transactional
    public ExpenseCategory actualizarCategoria(Long id, ExpenseCategoryDTO updatedCategory){
        ExpenseCategory originalCategory = expenseCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada."));
        
        originalCategory.setName(updatedCategory.getName());

        return originalCategory;
    }
    
    @Transactional
    public void eliminarCategoria(Long id) {
        ExpenseCategory categoria = expenseCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada."));

        categoria.setActive(false);
    }

    //Expensas
    @Transactional
    public Expense crearExpensa(Expense newExpense){
        ExpenseCategory categoriaEncontrada = expenseCategoryRepository.findById(newExpense.getCategory().getId()).orElseThrow(() -> new RuntimeException("¡Error! La categoria ID " + newExpense.getCategory().getId() + " no existe."));

        Expense nuevaExpensa = new Expense();
        nuevaExpensa.setCategory(categoriaEncontrada);
        nuevaExpensa.setAmount(newExpense.getAmount());
        nuevaExpensa.setDescription(newExpense.getDescription());
        nuevaExpensa.setDate(newExpense.getDate());
        
        return expenseRepository.save(newExpense);
    }

    @Transactional
    public Expense actualizarExpensa(Long id, Expense updatedExpense){
        Expense originalExpense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expensa no encontrada."));
        ExpenseCategory foundCategory = expenseCategoryRepository.findById(updatedExpense.getCategory().getId()).orElseThrow(() -> new RuntimeException("¡Error! La categoria ID " + updatedExpense.getCategory().getId() + " no existe."));

        originalExpense.setCategory(foundCategory);
        originalExpense.setAmount(updatedExpense.getAmount());
        originalExpense.setDescription(updatedExpense.getDescription());
        originalExpense.setDate(updatedExpense.getDate());

        return originalExpense;
    }
    
    public void eliminarExpensa(Long id) {
        Expense originalExpense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expensa no encontrada."));

        expenseRepository.deleteById(originalExpense.getId());
    }
}
