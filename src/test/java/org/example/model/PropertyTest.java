package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe Property.
 *
 * Valida a criação, validação e funcionalidades do modelo de dados.
 *
 * @version 1.0
 * @author Property Management System
 */
@DisplayName("Testes da Classe Property")
class PropertyTest {

    private Property property;
    private final String TEST_ADDRESS = "Rua Vicente da Costa, 150, Ipiranga";
    private final BigDecimal VALID_PRICE = new BigDecimal("9999.99");

    @BeforeEach
    void setUp() {
        property = new Property(TEST_ADDRESS, VALID_PRICE);
    }

    @Test
    @DisplayName("Deve criar propriedade com construtor padrão")
    void testCreatePropertyWithDefaultConstructor() {
        String address = "Rua Teste, 100, Ipiranga";
        BigDecimal price = new BigDecimal("8500.00");

        Property prop = new Property(address, price);

        assertNotNull(prop);
        assertEquals(address, prop.getAddress());
        assertEquals(price, prop.getPrice());
        assertNotNull(prop.getId());
        assertNotNull(prop.getCreatedAt());
        assertNotNull(prop.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve validar preço mínimo")
    void testValidatePriceMinimum() {
        Property prop = new Property(TEST_ADDRESS, new BigDecimal("8000.00"));
        assertTrue(prop.isValidPrice());
    }

    @Test
    @DisplayName("Deve validar preço máximo")
    void testValidatePriceMaximum() {
        Property prop = new Property(TEST_ADDRESS, new BigDecimal("11999.99"));
        assertTrue(prop.isValidPrice());
    }

    @Test
    @DisplayName("Deve rejeitar preço abaixo do mínimo")
    void testValidatePriceBelowMinimum() {
        Property prop = new Property(TEST_ADDRESS, new BigDecimal("7999.99"));
        assertFalse(prop.isValidPrice());
    }

    @Test
    @DisplayName("Deve rejeitar preço acima do máximo")
    void testValidatePriceAboveMaximum() {
        Property prop = new Property(TEST_ADDRESS, new BigDecimal("12000.00"));
        assertFalse(prop.isValidPrice());
    }

    @Test
    @DisplayName("Deve atualizar o timestamp ao chamar updateTimestamp()")
    void testUpdateTimestamp() {
        LocalDateTime originalTime = property.getUpdatedAt();

        // Aguarda um milissegundo para garantir diferença de tempo
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        property.updateTimestamp();
        LocalDateTime newTime = property.getUpdatedAt();

        assertTrue(newTime.isAfter(originalTime));
    }

    @Test
    @DisplayName("Deve gerar UUID único para cada propriedade")
    void testUniqueIdGeneration() {
        Property prop1 = new Property(TEST_ADDRESS, VALID_PRICE);
        Property prop2 = new Property(TEST_ADDRESS, VALID_PRICE);

        assertNotEquals(prop1.getId(), prop2.getId());
    }

    @Test
    @DisplayName("Deve formatar toString corretamente com ponto decimal")
    void testToStringFormat() {
        // O toString deve usar ponto (.) como separador decimal, não vírgula
        String expected = TEST_ADDRESS + " | R$ 9999.99";
        assertEquals(expected, property.toString());
    }

    @Test
    @DisplayName("Deve usar Builder para criar propriedade")
    void testBuilderConstruction() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Property prop = Property.builder()
                .id(id)
                .address(TEST_ADDRESS)
                .price(VALID_PRICE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(id, prop.getId());
        assertEquals(TEST_ADDRESS, prop.getAddress());
        assertEquals(VALID_PRICE, prop.getPrice());
        assertEquals(now, prop.getCreatedAt());
        assertEquals(now, prop.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando address é null")
    void testNullAddressThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new Property(null, VALID_PRICE);
        });
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando price é null")
    void testNullPriceThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new Property(TEST_ADDRESS, null);
        });
    }
}
