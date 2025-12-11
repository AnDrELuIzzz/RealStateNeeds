package org.example.strategy;

import org.example.model.Property;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Estratégia de ordenação de propriedades por preço.
 *
 * Implementa o padrão Strategy para ordenar propriedades em ordem
 * crescente ou decrescente de preço.
 *
 * @version 1.0
 * @author Property Management System
 */
@RequiredArgsConstructor
public class PriceSortStrategy implements PropertySortStrategy {

    private final SortOrder order;

    /**
     * Ordena as propriedades por preço.
     *
     * @param properties Lista de propriedades a serem ordenadas
     * @return Lista ordenada por preço
     */
    @Override
    public List<Property> sort(List<Property> properties) {
        List<Property> sorted = new ArrayList<>(properties);

        if (order == SortOrder.ASCENDING) {
            sorted.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
        } else {
            sorted.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
        }

        return sorted;
    }

    /**
     * Retorna descrição da estratégia.
     *
     * @return String com descrição
     */
    @Override
    public String getDescription() {
        return String.format("Ordenação por Preço (%s)", order);
    }
}
