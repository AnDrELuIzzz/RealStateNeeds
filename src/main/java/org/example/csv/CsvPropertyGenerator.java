package org.example.csv;

import org.example.model.Property;
import java.math.BigDecimal;
import java.util.*;

/**
 * Gerador de propriedades a partir de registros CSV.
 *
 * Cria múltiplas propriedades para cada linha do CSV, gerando preços
 * baseado no intervalo especificado (acima, abaixo ou ambos).
 *
 * @version 1.0
 * @author Property Management System
 */
public class CsvPropertyGenerator {

    private static final double MIN_PRICE = 8000.00;
    private static final double MAX_PRICE = 11999.99;
    private static final int PROPERTIES_PER_RECORD = 3; // Gerar 3 propriedades por linha do CSV

    private final Random random = new Random();

    /**
     * Gera propriedades a partir de registros CSV.
     *
     * Para cada registro, cria múltiplas propriedades com preços variados
     * conforme o intervalo especificado.
     *
     * @param records Lista de CsvPropertyRecord importados
     * @return Lista de Property geradas
     */
    public List<Property> generate(List<CsvPropertyRecord> records) {
        List<Property> properties = new ArrayList<>();

        for (CsvPropertyRecord record : records) {
            if (!record.isValid()) {
                System.err.println("Registro inválido: " + record.getErrorMessage());
                continue;
            }

            // Gerar múltiplas propriedades para cada rua
            List<Property> generated = generateFromRecord(record);
            properties.addAll(generated);

            System.out.println("✓ Geradas " + generated.size() + " propriedades para: " + record.getFullAddress());
        }

        return properties;
    }

    /**
     * Gera propriedades a partir de um único registro CSV.
     *
     * @param record Registro do CSV
     * @return Lista com propriedades geradas
     */
    private List<Property> generateFromRecord(CsvPropertyRecord record) {
        List<Property> properties = new ArrayList<>();
        double basePrice = record.getPrice().doubleValue();
        String priceRange = record.getPriceRange().toLowerCase();

        for (int i = 0; i < PROPERTIES_PER_RECORD; i++) {
            double price = generatePrice(basePrice, priceRange);

            // Garantir que está dentro da faixa global
            price = Math.max(MIN_PRICE, Math.min(MAX_PRICE, price));

            Property property = new Property(
                    record.getFullAddress(),
                    new BigDecimal(String.format(Locale.US, "%.2f", price))
            );

            properties.add(property);
        }

        return properties;
    }

    /**
     * Gera um preço baseado na estratégia especificada.
     *
     * @param basePrice Preço base do CSV
     * @param strategy "acima", "abaixo" ou "ambos"
     * @return Preço gerado
     */
    private double generatePrice(double basePrice, String strategy) {
        return switch (strategy) {
            case "acima" -> generateAbove(basePrice);
            case "abaixo" -> generateBelow(basePrice);
            case "ambos" -> random.nextBoolean() ? generateAbove(basePrice) : generateBelow(basePrice);
            default -> basePrice;
        };
    }

    /**
     * Gera preço acima do valor base.
     *
     * Varia entre basePrice e MAX_PRICE (ou basePrice + 20%)
     */
    private double generateAbove(double basePrice) {
        double maxPrice = Math.min(MAX_PRICE, basePrice * 1.2); // Máximo 20% acima
        return basePrice + random.nextDouble() * (maxPrice - basePrice);
    }

    /**
     * Gera preço abaixo do valor base.
     *
     * Varia entre MIN_PRICE (ou basePrice - 20%) e basePrice
     */
    private double generateBelow(double basePrice) {
        double minPrice = Math.max(MIN_PRICE, basePrice * 0.8); // Mínimo 20% abaixo
        return minPrice + random.nextDouble() * (basePrice - minPrice);
    }

    /**
     * Retorna a quantidade padrão de propriedades geradas por registro.
     *
     * @return Quantidade de propriedades
     */
    public int getPropertiesPerRecord() {
        return PROPERTIES_PER_RECORD;
    }
}
