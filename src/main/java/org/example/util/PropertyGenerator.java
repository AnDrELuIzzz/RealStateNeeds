package org.example.util;

import org.example.model.Property;

import java.math.BigDecimal;
import java.util.*;

/**
 * Utilitário para gerar dados de teste de propriedades.
 *
 * Gera propriedades fictícias para o bairro da Ipiranga com preços
 * variados dentro da faixa especificada.
 *
 * @version 1.0
 * @author Property Management System
 */
public class PropertyGenerator {

    /**
     * Ruas do bairro Ipiranga para geração de endereços.
     */
    private static final String[] STREETS = {
            "Rua Vicente da Costa",
            "Rua Moreira e Costa",
            "Rua Xavier de Almeida",
            "Rua Rodrigues do Prado",
            "Rua Clóvis Bueno de Azevedo",
            "Rua Dom Luiz de Lasanha",
            "Rua Bom Pastor",
            "Rua Agostinho Gomes",
            "Rua Cipriano Barata",
            "Rua Tabor",
            "Rua Lucas Obes"
    };

    private static final String NEIGHBORHOOD = "Ipiranga";
    private static final double MIN_PRICE = 8000.00;
    private static final double MAX_PRICE = 11999.99;
    private static final int MIN_NUMBER = 30;
    private static final int MAX_NUMBER = 900;

    private final Random random = new Random();

    /**
     * Gera uma lista de propriedades fictícias.
     *
     * @param count Quantidade de propriedades a gerar
     * @return Lista de propriedades geradas
     */
    public List<Property> generate(int count) {
        Set<String> generatedAddresses = new HashSet<>();
        List<Property> properties = new ArrayList<>();

        while (properties.size() < count) {
            String street = STREETS[random.nextInt(STREETS.length)];
            int number = MIN_NUMBER + random.nextInt(MAX_NUMBER - MIN_NUMBER + 1);
            String address = String.format("%s, %d, %s", street, number, NEIGHBORHOOD);

            if (!generatedAddresses.contains(address)) {
                generatedAddresses.add(address);

                double price = MIN_PRICE + random.nextDouble() * (MAX_PRICE - MIN_PRICE);
                BigDecimal bdPrice = new BigDecimal(String.format(Locale.US, "%.2f", price));

                Property property = new Property(address, bdPrice);
                properties.add(property);
            }
        }

        return properties;
    }

    /**
     * Gera um endereço aleatório.
     *
     * @return String contendo um endereço aleatório
     */
    public String generateAddress() {
        String street = STREETS[random.nextInt(STREETS.length)];
        int number = MIN_NUMBER + random.nextInt(MAX_NUMBER - MIN_NUMBER + 1);
        return String.format("%s, %d, %s", street, number, NEIGHBORHOOD);
    }

    /**
     * Gera um preço aleatório dentro da faixa válida.
     *
     * @return BigDecimal contendo um preço aleatório
     */
    public BigDecimal generatePrice() {
        double price = MIN_PRICE + random.nextDouble() * (MAX_PRICE - MIN_PRICE);
        return new BigDecimal(String.format(Locale.US, "%.2f", price));
    }

    /**
     * Gera uma propriedade aleatória.
     *
     * @return Property com dados aleatórios
     */
    public Property generateProperty() {
        return new Property(generateAddress(), generatePrice());
    }
}
