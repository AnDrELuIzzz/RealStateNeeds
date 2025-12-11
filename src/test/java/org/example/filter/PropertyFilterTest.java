package org.example.filter;

import org.example.model.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para os Filtros de Propriedades.
 *
 * Valida os filtros simples e compostos.
 *
 * @version 1.0
 * @author Property Management System
 */
@DisplayName("Testes de Filtros - PropertyFilter")
class PropertyFilterTest {

    private List<Property> properties;

    @BeforeEach
    void setUp() {
        properties = new ArrayList<>();
        properties.add(new Property("Rua Vicente da Costa, 100, Ipiranga", new BigDecimal("8500.00")));
        properties.add(new Property("Rua Moreira e Costa, 200, Ipiranga", new BigDecimal("9500.00")));
        properties.add(new Property("Rua Vicente da Costa, 300, Ipiranga", new BigDecimal("10500.00")));
        properties.add(new Property("Rua Bom Pastor, 400, Ipiranga", new BigDecimal("11000.00")));
    }

    @Test
    @DisplayName("Deve filtrar propriedades por faixa de preço")
    void testPriceRangeFilter() {
        PropertyFilter filter = new PriceRangeFilter(9000, 10500);
        List<Property> filtered = filter.apply(properties);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(p ->
                p.getPrice().doubleValue() >= 9000 &&
                        p.getPrice().doubleValue() <= 10500
        ));
    }

    @Test
    @DisplayName("Deve retornar lista vazia se nenhuma propriedade estiver na faixa")
    void testPriceRangeFilterNoResults() {
        PropertyFilter filter = new PriceRangeFilter(7000, 8000);
        List<Property> filtered = filter.apply(properties);

        assertTrue(filtered.isEmpty());
    }

    @Test
    @DisplayName("Deve filtrar propriedades por rua")
    void testStreetFilter() {
        PropertyFilter filter = new StreetFilter("Rua Vicente da Costa");
        List<Property> filtered = filter.apply(properties);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(p ->
                p.getAddress().contains("Rua Vicente da Costa")
        ));
    }

    @Test
    @DisplayName("Deve retornar lista vazia se rua não existir")
    void testStreetFilterNoResults() {
        PropertyFilter filter = new StreetFilter("Rua Inexistente");
        List<Property> filtered = filter.apply(properties);

        assertTrue(filtered.isEmpty());
    }

    @Test
    @DisplayName("Deve combinar filtros com operador AND")
    void testCompositeFilterAnd() {
        PropertyFilter priceFilter = new PriceRangeFilter(8000, 11000);
        PropertyFilter streetFilter = new StreetFilter("Rua Vicente da Costa");
        PropertyFilter composite = new CompositeFilter(
                CompositeFilter.Operator.AND,
                priceFilter,
                streetFilter
        );

        List<Property> filtered = composite.apply(properties);

        assertEquals(2, filtered.size());
        assertTrue(filtered.stream().allMatch(p ->
                p.getAddress().contains("Rua Vicente da Costa") &&
                        p.getPrice().doubleValue() >= 8000 &&
                        p.getPrice().doubleValue() <= 11000
        ));
    }

    @Test
    @DisplayName("Deve combinar filtros com operador OR")
    void testCompositeFilterOr() {
        PropertyFilter filter1 = new StreetFilter("Rua Vicente da Costa");
        PropertyFilter filter2 = new StreetFilter("Rua Bom Pastor");
        PropertyFilter composite = new CompositeFilter(
                CompositeFilter.Operator.OR,
                filter1,
                filter2
        );

        List<Property> filtered = composite.apply(properties);

        assertEquals(3, filtered.size());
    }

    @Test
    @DisplayName("Deve retornar descrição do filtro de preço")
    void testPriceFilterDescription() {
        PropertyFilter filter = new PriceRangeFilter(8000, 9000);
        String description = filter.getDescription();

        assertNotNull(description);
        assertTrue(description.contains("8000"));
        assertTrue(description.contains("9000"));
    }

    @Test
    @DisplayName("Deve retornar descrição do filtro de rua")
    void testStreetFilterDescription() {
        PropertyFilter filter = new StreetFilter("Rua Teste");
        String description = filter.getDescription();

        assertNotNull(description);
        assertTrue(description.contains("Rua Teste"));
    }

    @Test
    @DisplayName("Deve retornar descrição do filtro composto")
    void testCompositeFilterDescription() {
        PropertyFilter filter1 = new PriceRangeFilter(8000, 9000);
        PropertyFilter filter2 = new StreetFilter("Rua Teste");
        PropertyFilter composite = new CompositeFilter(
                CompositeFilter.Operator.AND,
                filter1,
                filter2
        );

        String description = composite.getDescription();

        assertNotNull(description);
        assertTrue(description.contains("AND"));
    }
}
