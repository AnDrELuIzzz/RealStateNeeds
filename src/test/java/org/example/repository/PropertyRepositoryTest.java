package org.example.repository;

import org.example.filter.CompositeFilter;
import org.example.filter.PriceRangeFilter;
import org.example.filter.StreetFilter;
import org.example.model.Property;
import org.example.observer.PriceChangeListener;
import org.example.observer.PropertyListener;
import org.example.repository.PropertyRepository;
import org.example.service.PropertyService;
import org.example.strategy.AddressSortStrategy;
import org.example.strategy.PriceSortStrategy;
import org.example.strategy.SortOrder;
import org.example.util.PropertyGenerator;

import java.util.List;

/**
 * Aplicação principal do Sistema de Gestão de Propriedades Ipiranga.
 *
 * Demonstra o uso prático de:
 * - Observer Pattern (PriceChangeListener)
 * - Strategy Pattern (PriceSortStrategy, AddressSortStrategy)
 * - Filtros compostos (CompositeFilter)
 * - Repositório e Serviço
 *
 * @version 1.0
 * @author Property Management System
 */
class PropertyManagementApplication {

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("    Sistema de Gestão de Propriedades - Bairro Ipiranga");
        System.out.println("════════════════════════════════════════════════════════════════\n");

        // 1. Gerar dados de teste
        System.out.println("📊 Gerando 30 propriedades...\n");
        PropertyGenerator generator = new PropertyGenerator();
        List<Property> generatedProperties = generator.generate(30);

        // 2. Criar repositório e serviço
        PropertyRepository repository = new PropertyRepository(generatedProperties);
        PropertyService service = new PropertyService(repository);

        // 3. Inscrever um observador (Observer Pattern)
        System.out.println("📌 Inscrevendo observador de auditoria...\n");
        PropertyListener auditListener = new PriceChangeListener();
        service.subscribe(auditListener);

        // 4. Exibir todas as propriedades
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("TODAS AS PROPRIEDADES (Total: " + service.getPropertyCount() + ")");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        service.getAllProperties().forEach(System.out::println);

        // 5. Ordenar por preço ascendente
        System.out.println("\n════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES ORDENADAS POR PREÇO (Crescente)");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> sortedByPrice = service.sortProperties(
                new PriceSortStrategy(SortOrder.ASCENDING)
        );
        sortedByPrice.stream().limit(5).forEach(System.out::println);
        System.out.println("...");
        System.out.println("[Exibindo 5 de " + sortedByPrice.size() + " propriedades]\n");

        // 6. Ordenar por endereço descendente
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES ORDENADAS POR ENDEREÇO (Descendente)");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> sortedByAddress = service.sortProperties(
                new AddressSortStrategy(SortOrder.DESCENDING)
        );
        sortedByAddress.stream().limit(5).forEach(System.out::println);
        System.out.println("...");
        System.out.println("[Exibindo 5 de " + sortedByAddress.size() + " propriedades]\n");

        // 7. Filtrar por faixa de preço
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES FILTRADAS - Preço entre R$ 9.000,00 e R$ 9.999,99");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredByPrice = service.filterProperties(
                new PriceRangeFilter(9000, 9999.99)
        );
        filteredByPrice.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredByPrice.size() + "\n");

        // 8. Filtrar por rua específica
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES - Rua Vicente da Costa");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredByStreet = service.filterProperties(
                new StreetFilter("Rua Vicente da Costa")
        );
        filteredByStreet.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredByStreet.size() + "\n");

        // 9. Filtro composto (AND): Rua Vicente da Costa AND Preço entre 8.500 e 10.000
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("FILTRO COMPOSTO (AND) - Rua Vicente da Costa E Preço 8.500-10.000");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> compositeFilterAnd = service.filterProperties(
                new CompositeFilter(
                        CompositeFilter.Operator.AND,
                        new StreetFilter("Rua Vicente da Costa"),
                        new PriceRangeFilter(8500, 10000)
                )
        );
        compositeFilterAnd.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + compositeFilterAnd.size() + "\n");

        // 10. Demonstração do Observer Pattern: atualizar preço
        if (!filteredByPrice.isEmpty()) {
            System.out.println("════════════════════════════════════════════════════════════════");
            System.out.println("DEMONSTRAÇÃO - Observer Pattern (Mudança de Preço)");
            System.out.println("════════════════════════════════════════════════════════════════\n");

            Property propertyToUpdate = filteredByPrice.get(0);
            System.out.println("Atualizando preço de: " + propertyToUpdate.getAddress());
            System.out.println("Preço anterior: R$ " + String.format("%.2f", propertyToUpdate.getPrice().doubleValue()));

            // Isto irá disparar notificação para o listener
            service.updatePropertyPrice(propertyToUpdate, 10500.00);

            System.out.println("\n");
        }

        // 11. Filtro composto (OR): Rua Vicente da Costa OR Rua Bom Pastor
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("FILTRO COMPOSTO (OR) - Rua Vicente da Costa OU Rua Bom Pastor");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> compositeFilterOr = service.filterProperties(
                new CompositeFilter(
                        CompositeFilter.Operator.OR,
                        new StreetFilter("Rua Vicente da Costa"),
                        new StreetFilter("Rua Bom Pastor")
                )
        );
        compositeFilterOr.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + compositeFilterOr.size() + "\n");

        // 12. Filtrar e ordenar combinado
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("FILTRO + ORDENAÇÃO - Faixa 8.500-10.000, Ordenado por Preço");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredAndSorted = service.filterAndSort(
                new PriceRangeFilter(8500, 10000),
                new PriceSortStrategy(SortOrder.ASCENDING)
        );
        filteredAndSorted.stream().limit(10).forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredAndSorted.size() + "\n");

        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("✅ Demonstração concluída com sucesso!");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
}
