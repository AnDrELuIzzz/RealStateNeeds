package org.example;

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
 * Esta classe é o entry point (ponto de entrada) da aplicação.
 * Ela demonstra de forma prática todos os padrões de design implementados:
 * - Observer Pattern (notificações)
 * - Strategy Pattern (ordenação)
 * - Composite Pattern (filtros)
 * - Repository Pattern (acesso a dados)
 * - Service Layer Pattern (lógica de negócio)
 *
 * @version 1.0
 * @author Property Management System
 */
public class PropertyManagementApplication {

    /**
     * Método principal (main).
     *
     * Fluxo de execução:
     * 1. Gera 30 propriedades aleatórias
     * 2. Cria repositório e serviço
     * 3. Inscreve observador para auditoria
     * 4. Executa 9 demonstrações de funcionalidades
     * 5. Exibe resultados no console
     *
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // ========== CABEÇALHO ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("    Sistema de Gestão de Propriedades - Bairro Ipiranga");
        System.out.println("════════════════════════════════════════════════════════════════\n");

        // ========== PASSO 1: Gerar Dados ==========
        System.out.println("📊 Gerando 30 propriedades...\n");
        PropertyGenerator generator = new PropertyGenerator();
        List<Property> generatedProperties = generator.generate(30);

        // ========== PASSO 2: Criar Repositório e Serviço ==========
        PropertyRepository repository = new PropertyRepository(generatedProperties);
        PropertyService service = new PropertyService(repository);

        // ========== PASSO 3: Inscrever Observer ==========
        System.out.println("📌 Inscrevendo observador de auditoria...\n");
        PropertyListener auditListener = new PriceChangeListener();
        service.subscribe(auditListener);

        // ========== DEMONSTRAÇÃO 1: Todas as Propriedades ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("TODAS AS PROPRIEDADES (Total: " + service.getPropertyCount() + ")");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        service.getAllProperties().forEach(System.out::println);

        // ========== DEMONSTRAÇÃO 2: Ordenação por Preço (Crescente) ==========
        System.out.println("\n════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES ORDENADAS POR PREÇO (Crescente)");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> sortedByPrice = service.sortProperties(
                new PriceSortStrategy(SortOrder.ASCENDING)
        );
        sortedByPrice.stream().limit(5).forEach(System.out::println);
        System.out.println("...");
        System.out.println("[Exibindo 5 de " + sortedByPrice.size() + " propriedades]\n");

        // ========== DEMONSTRAÇÃO 3: Ordenação por Endereço (Decrescente) ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES ORDENADAS POR ENDEREÇO (Descendente)");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> sortedByAddress = service.sortProperties(
                new AddressSortStrategy(SortOrder.DESCENDING)
        );
        sortedByAddress.stream().limit(5).forEach(System.out::println);
        System.out.println("...");
        System.out.println("[Exibindo 5 de " + sortedByAddress.size() + " propriedades]\n");

        // ========== DEMONSTRAÇÃO 4: Filtro por Faixa de Preço ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES FILTRADAS - Preço entre R$ 9.000,00 e R$ 9.999,99");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredByPrice = service.filterProperties(
                new PriceRangeFilter(9000, 9999.99)
        );
        filteredByPrice.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredByPrice.size() + "\n");

        // ========== DEMONSTRAÇÃO 5: Filtro por Rua ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("PROPRIEDADES - Rua Vicente da Costa");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredByStreet = service.filterProperties(
                new StreetFilter("Rua Vicente da Costa")
        );
        filteredByStreet.forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredByStreet.size() + "\n");

        // ========== DEMONSTRAÇÃO 6: Filtro Composto (AND) ==========
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

        // ========== DEMONSTRAÇÃO 7: Observer Pattern em Ação ==========
        if (!filteredByPrice.isEmpty()) {
            System.out.println("════════════════════════════════════════════════════════════════");
            System.out.println("DEMONSTRAÇÃO - Observer Pattern (Mudança de Preço)");
            System.out.println("════════════════════════════════════════════════════════════════\n");

            Property propertyToUpdate = filteredByPrice.get(0);
            System.out.println("Atualizando preço de: " + propertyToUpdate.getAddress());
            System.out.println("Preço anterior: R$ " + String.format("%.2f", propertyToUpdate.getPrice().doubleValue()));

            // Isto irá disparar notificação para o listener (PriceChangeListener)
            service.updatePropertyPrice(propertyToUpdate, 10500.00);

            System.out.println("\n");
        }

        // ========== DEMONSTRAÇÃO 8: Filtro Composto (OR) ==========
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

        // ========== DEMONSTRAÇÃO 9: Filtro + Ordenação Combinados ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("FILTRO + ORDENAÇÃO - Faixa 8.500-10.000, Ordenado por Preço");
        System.out.println("════════════════════════════════════════════════════════════════\n");
        List<Property> filteredAndSorted = service.filterAndSort(
                new PriceRangeFilter(8500, 10000),
                new PriceSortStrategy(SortOrder.ASCENDING)
        );
        filteredAndSorted.stream().limit(10).forEach(System.out::println);
        System.out.println("\nTotal encontrado: " + filteredAndSorted.size() + "\n");

        // ========== CONCLUSÃO ==========
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("✅ Demonstração concluída com sucesso!");
        System.out.println("════════════════════════════════════════════════════════════════");
    }

    /**
     * Método auxiliar para executar cenário customizado.
     *
     * Pode ser chamado de testes ou outras classes para demonstrações específicas.
     *
     * @param propertyCount Número de propriedades a gerar
     */
    public static void runCustomScenario(int propertyCount) {
        PropertyGenerator generator = new PropertyGenerator();
        List<Property> properties = generator.generate(propertyCount);

        PropertyRepository repository = new PropertyRepository(properties);
        PropertyService service = new PropertyService(repository);

        service.subscribe(new PriceChangeListener());

        System.out.println("\n📌 Cenário Customizado com " + propertyCount + " propriedades\n");
        System.out.println("Total de propriedades: " + service.getPropertyCount());
    }
}
