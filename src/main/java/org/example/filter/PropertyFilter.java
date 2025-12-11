package org.example.filter;

import org.example.model.Property;
import java.util.List;

/**
 * Interface para o padrão Strategy aplicado a filtros.
 *
 * Define o contrato para diferentes estratégias de filtragem de propriedades.
 * Permite criar filtros complexos combinando múltiplos filtros.
 *
 * @version 1.0
 * @author Property Management System
 */
public interface PropertyFilter {

    /**
     * Aplica o filtro à lista de propriedades.
     *
     * @param properties Lista de propriedades a serem filtradas
     * @return Lista filtrada contendo apenas as propriedades que atendem aos critérios
     */
    List<Property> apply(List<Property> properties);

    /**
     * Retorna uma descrição do filtro.
     *
     * @return String descrevendo o critério de filtro
     */
    String getDescription();
}
