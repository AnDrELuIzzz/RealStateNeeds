package org.example.strategy;

import org.example.model.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Strategy Pattern de Ordenação.
 *
 * Valida as estratégias de ordenação por preço e endereço.
 *
 * @version 1.0
 * @author Property Management System
 */
@DisplayName("Testes do Strategy Pattern - PropertySortStrategy")
class PropertySortStrategyTest {

    private List<Property> properties;

    @BeforeEach
    void setUp() {
        properties = new ArrayList<>();
        properties.add(new Property("Rua A, 100, Ipiranga", new BigDecimal("10000.00")));
        properties.add(new Property("Rua B, 200, Ipiranga", new BigDecimal("8500.00")));
        properties.add(new Property("Rua C, 300, Ipiranga", new BigDecimal("11000.00")));
        properties.add(new Property("Rua D, 400, Ipiranga", new BigDecimal("9000.00")));
    }

    @Test
    @DisplayName("Deve ordenar propriedades por preço ascendente")
    void testSortByPriceAscending() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.ASCENDING);
        List<Property> sorted = strategy.sort(properties);

        assertEquals(8500.00, sorted.get(0).getPrice().doubleValue());
        assertEquals(9000.00, sorted.get(1).getPrice().doubleValue());
        assertEquals(10000.00, sorted.get(2).getPrice().doubleValue());
        assertEquals(11000.00, sorted.get(3).getPrice().doubleValue());
    }

    @Test
    @DisplayName("Deve ordenar propriedades por preço descendente")
    void testSortByPriceDescending() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.DESCENDING);
        List<Property> sorted = strategy.sort(properties);

        assertEquals(11000.00, sorted.get(0).getPrice().doubleValue());
        assertEquals(10000.00, sorted.get(1).getPrice().doubleValue());
        assertEquals(9000.00, sorted.get(2).getPrice().doubleValue());
        assertEquals(8500.00, sorted.get(3).getPrice().doubleValue());
    }

    @Test
    @DisplayName("Deve ordenar propriedades por endereço ascendente")
    void testSortByAddressAscending() {
        PropertySortStrategy strategy = new AddressSortStrategy(SortOrder.ASCENDING);
        List<Property> sorted = strategy.sort(properties);

        assertTrue(sorted.get(0).getAddress().contains("Rua A"));
        assertTrue(sorted.get(1).getAddress().contains("Rua B"));
        assertTrue(sorted.get(2).getAddress().contains("Rua C"));
        assertTrue(sorted.get(3).getAddress().contains("Rua D"));
    }

    @Test
    @DisplayName("Deve ordenar propriedades por endereço descendente")
    void testSortByAddressDescending() {
        PropertySortStrategy strategy = new AddressSortStrategy(SortOrder.DESCENDING);
        List<Property> sorted = strategy.sort(properties);

        assertTrue(sorted.get(0).getAddress().contains("Rua D"));
        assertTrue(sorted.get(1).getAddress().contains("Rua C"));
        assertTrue(sorted.get(2).getAddress().contains("Rua B"));
        assertTrue(sorted.get(3).getAddress().contains("Rua A"));
    }

    @Test
    @DisplayName("Não deve alterar lista original ao ordenar")
    void testSortDoesNotModifyOriginal() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.ASCENDING);

        BigDecimal firstPrice = properties.get(0).getPrice();
        strategy.sort(properties);

        assertEquals(firstPrice, properties.get(0).getPrice());
    }

    @Test
    @DisplayName("Deve retornar descrição da estratégia")
    void testGetDescription() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.ASCENDING);
        String description = strategy.getDescription();

        assertNotNull(description);
        assertTrue(description.contains("Preço"));
        assertTrue(description.contains("ASCENDING"));
    }

    @Test
    @DisplayName("Deve funcionar com lista vazia")
    void testSortEmptyList() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.ASCENDING);
        List<Property> emptyList = new ArrayList<>();

        List<Property> sorted = strategy.sort(emptyList);

        assertTrue(sorted.isEmpty());
    }

    @Test
    @DisplayName("Deve funcionar com lista de um elemento")
    void testSortSingleElement() {
        PropertySortStrategy strategy = new PriceSortStrategy(SortOrder.ASCENDING);
        List<Property> singleList = new ArrayList<>();
        singleList.add(properties.get(0));

        List<Property> sorted = strategy.sort(singleList);

        assertEquals(1, sorted.size());
        assertEquals(properties.get(0).getPrice(), sorted.get(0).getPrice());
    }
}
