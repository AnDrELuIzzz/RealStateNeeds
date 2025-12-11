package org.example.strategy;

import org.example.model.Property;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Estratégia de ordenação de propriedades por endereço (rua).
 *
 * Implementa o padrão Strategy para ordenar propriedades alfabeticamente
 * por endereço em ordem crescente ou decrescente.
 *
 * @version 1.0
 * @author Property Management System
 */
@RequiredArgsConstructor
public class AddressSortStrategy implements PropertySortStrategy {

    private final SortOrder order;

    /**
     * Ordena as propriedades por endereço.
     *
     * @param properties Lista de propriedades a serem ordenadas
     * @return Lista ordenada por endereço
     */
    @Override
    public List<Property> sort(List<Property> properties) {
        List<Property> sorted = new ArrayList<>(properties);

        if (order == SortOrder.ASCENDING) {
            sorted.sort((p1, p2) -> p1.getAddress().compareTo(p2.getAddress()));
        } else {
            sorted.sort((p1, p2) -> p2.getAddress().compareTo(p1.getAddress()));
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
        return String.format("Ordenação por Endereço (%s)", order);
    }
}
