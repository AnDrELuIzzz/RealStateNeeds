package org.example.service;

import org.example.filter.PriceRangeFilter;
import org.example.filter.StreetFilter;
import org.example.model.Property;
import org.example.observer.PropertyListener;
import org.example.repository.PropertyRepository;
import org.example.strategy.PriceSortStrategy;
import org.example.strategy.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PropertyService (Serviço de Negócio).
 *
 * Valida operações CRUD, filtros, ordenação e notificações.
 *
 * @version 1.0
 * @author Property Management System
 */
@DisplayName("Testes da Camada de Serviço - PropertyService")
class PropertyServiceTest {

    private PropertyService service;
    private PropertyRepository repository;

    @Mock
    private PropertyListener mockListener;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new PropertyRepository();
        service = new PropertyService(repository);
        testProperty = new Property("Rua Teste, 100, Ipiranga", new BigDecimal("9999.99"));
    }

    @Test
    @DisplayName("Deve adicionar propriedade válida")
    void testAddValidProperty() {
        service.addProperty(testProperty);

        assertEquals(1, service.getPropertyCount());
        assertTrue(service.getAllProperties().contains(testProperty));
    }

    @Test
    @DisplayName("Não deve adicionar propriedade com preço inválido")
    void testAddInvalidPropertyPrice() {
        Property invalidProperty = new Property("Rua Teste, 100, Ipiranga", new BigDecimal("12000.00"));

        service.addProperty(invalidProperty);

        assertEquals(0, service.getPropertyCount());
    }

    @Test
    @DisplayName("Não deve adicionar propriedade null")
    void testAddNullProperty() {
        service.addProperty(null);

        assertEquals(0, service.getPropertyCount());
    }

    @Test
    @DisplayName("Deve remover propriedade")
    void testRemoveProperty() {
        service.addProperty(testProperty);
        service.removeProperty(testProperty);

        assertEquals(0, service.getPropertyCount());
    }

    @Test
    @DisplayName("Deve encontrar propriedade pelo ID")
    void testFindPropertyById() {
        service.addProperty(testProperty);

        Optional<Property> found = service.findPropertyById(testProperty.getId());

        assertTrue(found.isPresent());
        assertEquals(testProperty.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Deve atualizar preço de propriedade")
    void testUpdatePropertyPrice() {
        service.addProperty(testProperty);
        BigDecimal newPrice = new BigDecimal("10500.00");

        service.updatePropertyPrice(testProperty, newPrice);

        assertEquals(newPrice, testProperty.getPrice());
    }

    @Test
    @DisplayName("Deve filtrar propriedades por preço")
    void testFilterByPrice() {
        service.addProperty(new Property("Rua A, 100, Ipiranga", new BigDecimal("8500.00")));
        service.addProperty(new Property("Rua B, 200, Ipiranga", new BigDecimal("10000.00")));
        service.addProperty(new Property("Rua C, 300, Ipiranga", new BigDecimal("11000.00")));

        List<Property> filtered = service.filterProperties(
                new PriceRangeFilter(9000, 11000)
        );

        assertEquals(2, filtered.size());
    }

    @Test
    @DisplayName("Deve filtrar propriedades por rua")
    void testFilterByStreet() {
        service.addProperty(new Property("Rua Vicente da Costa, 100, Ipiranga", new BigDecimal("9000.00")));
        service.addProperty(new Property("Rua Moreira e Costa, 200, Ipiranga", new BigDecimal("9500.00")));
        service.addProperty(new Property("Rua Vicente da Costa, 300, Ipiranga", new BigDecimal("10000.00")));

        List<Property> filtered = service.filterProperties(
                new StreetFilter("Rua Vicente da Costa")
        );

        assertEquals(2, filtered.size());
    }

    @Test
    @DisplayName("Deve ordenar propriedades por preço")
    void testSortByPrice() {
        service.addProperty(new Property("Rua A, 100, Ipiranga", new BigDecimal("10000.00")));
        service.addProperty(new Property("Rua B, 200, Ipiranga", new BigDecimal("8500.00")));
        service.addProperty(new Property("Rua C, 300, Ipiranga", new BigDecimal("11000.00")));

        List<Property> sorted = service.sortProperties(
                new PriceSortStrategy(SortOrder.ASCENDING)
        );

        assertEquals(8500.00, sorted.get(0).getPrice().doubleValue());
        assertEquals(10000.00, sorted.get(1).getPrice().doubleValue());
        assertEquals(11000.00, sorted.get(2).getPrice().doubleValue());
    }

    @Test
    @DisplayName("Deve filtrar e ordenar propriedades")
    void testFilterAndSort() {
        service.addProperty(new Property("Rua A, 100, Ipiranga", new BigDecimal("10000.00")));
        service.addProperty(new Property("Rua B, 200, Ipiranga", new BigDecimal("8500.00")));
        service.addProperty(new Property("Rua A, 300, Ipiranga", new BigDecimal("11000.00")));

        List<Property> result = service.filterAndSort(
                new StreetFilter("Rua A"),
                new PriceSortStrategy(SortOrder.ASCENDING)
        );

        assertEquals(2, result.size());
        assertEquals(10000.00, result.get(0).getPrice().doubleValue());
        assertEquals(11000.00, result.get(1).getPrice().doubleValue());
    }

    @Test
    @DisplayName("Deve inscrever listener")
    void testSubscribeListener() {
        service.subscribe(mockListener);

        // Verificar que listener está registrado
        service.addProperty(testProperty);
        verify(mockListener, times(1)).onPropertyCreated(testProperty);
    }

    @Test
    @DisplayName("Deve desinscrever listener")
    void testUnsubscribeListener() {
        service.subscribe(mockListener);
        service.unsubscribe(mockListener);

        service.addProperty(testProperty);
        verify(mockListener, never()).onPropertyCreated(testProperty);
    }

    @Test
    @DisplayName("Deve notificar listener ao atualizar preço")
    void testListenerNotifiedOnPriceUpdate() {
        service.subscribe(mockListener);
        service.addProperty(testProperty);

        service.updatePropertyPrice(testProperty, 10500.00);

        verify(mockListener, times(1)).onPriceChanged(
                eq(testProperty),
                eq(9999.99),
                eq(10500.00)
        );
    }

    @Test
    @DisplayName("Deve retornar contagem correta de propriedades")
    void testGetPropertyCount() {
        assertEquals(0, service.getPropertyCount());

        service.addProperty(testProperty);
        assertEquals(1, service.getPropertyCount());

        service.removeProperty(testProperty);
        assertEquals(0, service.getPropertyCount());
    }
}
