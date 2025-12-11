package org.example.strategy;

import org.example.model.Property;
import java.util.List;

/**
 * Interface para o padrão Strategy.
 *
 * Define o contrato para diferentes estratégias de ordenação de propriedades.
 * Permite trocar dinamicamente o algoritmo de ordenação em tempo de execução.
 *
 * @version 1.0
 * @author Property Management System
 */
public interface PropertySortStrategy {

    /**
     * Ordena uma lista de propriedades de acordo com a estratégia implementada.
     *
     * @param properties Lista de propriedades a serem ordenadas
     * @return Lista ordenada de propriedades
     */
    List<Property> sort(List<Property> properties);

    /**
     * Retorna uma descrição legível da estratégia de ordenação.
     *
     * @return String descrevendo a estratégia
     */
    String getDescription();
}
