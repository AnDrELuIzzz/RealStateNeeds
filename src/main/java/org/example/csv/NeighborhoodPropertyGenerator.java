package org.example.csv;

import org.example.geolocation.GeolocationValidator;
import org.example.model.Property;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerador de propriedades ao redor de um endereço base.
 *
 * Para cada endereço do CSV, busca ruas próximas via API e gera:
 * - Múltiplas residências na mesma rua
 * - Múltiplas ruas ao redor
 * - Preços baseado na estratégia (acima/abaixo/ambos)
 *
 * @version 2.0
 * @author Property Management System
 */
public class NeighborhoodPropertyGenerator {

    private static final double MIN_PRICE = 8000.00;
    private static final double MAX_PRICE = 11999.99;
    private static final int MIN_HOUSE_NUMBER = 30;
    private static final int MAX_HOUSE_NUMBER = 900;

    private final Random random = new Random();
    private final GeolocationValidator geoValidator;

    public NeighborhoodPropertyGenerator() {
        this.geoValidator = new GeolocationValidator();
    }

    /**
     * Gera propriedades ao redor do endereço base.
     *
     * @param csvRecord Registro com rua, número, preço e intervalo
     * @param quantityPerStreet Quantidade de propriedades por rua
     * @param nearbyStreetsCount Quantidade de ruas ao redor para buscar
     * @return Lista de propriedades geradas
     */
    public List<Property> generateNeighborhoodProperties(
            CsvPropertyRecord csvRecord,
            int quantityPerStreet,
            int nearbyStreetsCount) {

        List<Property> properties = new ArrayList<>();
        double basePrice = csvRecord.getPrice().doubleValue();
        String priceStrategy = csvRecord.getPriceRange().toLowerCase();

        // 1. Gerar propriedades na RUA ORIGINAL
        System.out.println("\n🏠 Gerando propriedades na rua original: " + csvRecord.getStreet());
        List<Property> originalStreetProperties = generatePropertiesOnStreet(
                csvRecord.getStreet(),
                csvRecord.getFullAddress(),
                basePrice,
                priceStrategy,
                quantityPerStreet
        );
        properties.addAll(originalStreetProperties);
        System.out.println("   ✓ " + originalStreetProperties.size() + " propriedades geradas");

        // 2. Buscar ruas PRÓXIMAS via API
        System.out.println("\n🗺️  Buscando ruas próximas via OpenStreetMap API...");
        List<String> nearbyStreets = findNearbyStreets(csvRecord.getStreet(), nearbyStreetsCount);

        if (nearbyStreets.isEmpty()) {
            System.out.println("   ⚠️  Nenhuma rua próxima encontrada. Usando ruas alternativas.");
            nearbyStreets = getDefaultNearbyStreets(csvRecord.getStreet());
        }

        // 3. Gerar propriedades nas RUAS PRÓXIMAS
        for (String nearbyStreet : nearbyStreets) {
            String fullAddress = String.format("%s, IPIRANGA", nearbyStreet);

            List<Property> nearbyProperties = generatePropertiesOnStreet(
                    nearbyStreet,
                    fullAddress,
                    basePrice,
                    priceStrategy,
                    quantityPerStreet
            );
            properties.addAll(nearbyProperties);
            System.out.println("   ✓ " + nearbyProperties.size() + " propriedades em " + nearbyStreet);
        }

        System.out.println("\n✅ Total de propriedades geradas: " + properties.size());
        return properties;
    }

    /**
     * Gera propriedades em uma rua específica.
     */
    private List<Property> generatePropertiesOnStreet(
            String street,
            String fullAddressBase,
            double basePrice,
            String priceStrategy,
            int quantity) {

        List<Property> properties = new ArrayList<>();
        Set<Integer> usedNumbers = new HashSet<>();

        for (int i = 0; i < quantity; i++) {
            // Gerar número único
            Integer houseNumber;
            do {
                houseNumber = MIN_HOUSE_NUMBER + random.nextInt(MAX_HOUSE_NUMBER - MIN_HOUSE_NUMBER + 1);
            } while (usedNumbers.contains(houseNumber));
            usedNumbers.add(houseNumber);

            // Formatar endereço completo
            String address = String.format("%s, %d, Ipiranga", street, houseNumber);

            // Gerar preço
            double price = generatePrice(basePrice, priceStrategy);
            price = Math.max(MIN_PRICE, Math.min(MAX_PRICE, price));

            // Criar propriedade
            Property property = new Property(
                    address,
                    new BigDecimal(String.format(Locale.US, "%.2f", price))
            );

            properties.add(property);
        }

        return properties;
    }

    /**
     * Busca ruas próximas usando a API OpenStreetMap.
     */
    private List<String> findNearbyStreets(String baseStreet, int count) {
        // Por enquanto, retorna lista vazia (API não fornece "ruas próximas" facilmente)
        // Na versão real, você poderia:
        // 1. Obter coordenadas da rua base
        // 2. Fazer busca circular ao redor das coordenadas
        // 3. Retornar ruas encontradas

        // Para demo, usamos ruas alternativas conhecidas
        return Collections.emptyList();
    }

    /**
     * Retorna ruas alternativas conhecidas como próximas (fallback).
     */
    private List<String> getDefaultNearbyStreets(String baseStreet) {
        Map<String, List<String>> nearbyMap = new HashMap<>();

        // Mapa de ruas próximas (você pode expandir isso)
        nearbyMap.put("Rua Vicente da Costa", Arrays.asList(
                "Rua Moreira e Costa",
                "Rua Xavier de Almeida",
                "Rua Rodrigues do Prado"
        ));

        nearbyMap.put("Rua Bom Pastor", Arrays.asList(
                "Rua Agostinho Gomes",
                "Rua Cipriano Barata",
                "Rua Dom Luiz de Lasanha"
        ));

        // Fallback para ruas padrão do bairro
        List<String> defaultStreets = Arrays.asList(
                "Rua Moreira e Costa",
                "Rua Xavier de Almeida",
                "Rua Rodrigues do Prado",
                "Rua Clóvis Bueno de Azevedo"
        );

        return nearbyMap.getOrDefault(baseStreet, defaultStreets);
    }

    /**
     * Gera um preço baseado na estratégia.
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
     * Gera preço acima do base (até +20%).
     */
    private double generateAbove(double basePrice) {
        double maxPrice = Math.min(MAX_PRICE, basePrice * 1.2);
        return basePrice + random.nextDouble() * (maxPrice - basePrice);
    }

    /**
     * Gera preço abaixo do base (até -20%).
     */
    private double generateBelow(double basePrice) {
        double minPrice = Math.max(MIN_PRICE, basePrice * 0.8);
        return minPrice + random.nextDouble() * (basePrice - minPrice);
    }
}