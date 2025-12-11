package org.example.filter;

import org.example.model.Property;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro composto que combina múltiplos filtros com operadores lógicos.
 *
 * Implementa o padrão Composite para permitir combinações de filtros
 * usando operadores AND (todos devem passar) ou OR (pelo menos um passa).
 *
 * @version 1.0
 * @author Property Management System
 */
public class CompositeFilter implements PropertyFilter {

    /**
     * Enum para operadores lógicos.
     */
    public enum Operator {
        AND, OR
    }

    private final List<PropertyFilter> filters;
    @Getter
    private final Operator operator;

    /**
     * Construtor variádico para múltiplos filtros com operador.
     *
     * @param operator Operador lógico (AND ou OR)
     * @param filters Filtros a combinar
     */
    public CompositeFilter(Operator operator, PropertyFilter... filters) {
        this.operator = operator;
        this.filters = Arrays.asList(filters);
    }

    /**
     * Construtor alternativo com lista de filtros.
     *
     * @param filters Lista de filtros
     * @param operator Operador lógico
     */
    public CompositeFilter(List<PropertyFilter> filters, Operator operator) {
        this.filters = filters;
        this.operator = operator;
    }

    /**
     * Aplica todos os filtros combinados.
     *
     * @param properties Lista de propriedades
     * @return Propriedades que passam pelo filtro composto
     */
    @Override
    public List<Property> apply(List<Property> properties) {
        if (filters.isEmpty()) {
            return properties;
        }

        if (operator == Operator.AND) {
            return applyAnd(properties);
        } else {
            return applyOr(properties);
        }
    }

    /**
     * Aplica todos os filtros com operador AND.
     * Uma propriedade é incluída apenas se passar em TODOS os filtros.
     *
     * @param properties Lista de propriedades
     * @return Propriedades que passam em todos os filtros
     */
    private List<Property> applyAnd(List<Property> properties) {
        List<Property> result = properties;
        for (PropertyFilter filter : filters) {
            result = filter.apply(result);
        }
        return result;
    }

    /**
     * Aplica todos os filtros com operador OR.
     * Uma propriedade é incluída se passar em PELO MENOS UM filtro.
     *
     * @param properties Lista de propriedades
     * @return Propriedades que passam em pelo menos um filtro
     */
    private List<Property> applyOr(List<Property> properties) {
        return properties.stream()
                .filter(property -> filters.stream()
                        .anyMatch(filter -> filter.apply(List.of(property)).contains(property)))
                .collect(Collectors.toList());
    }

    /**
     * Retorna descrição do filtro composto.
     *
     * @return String com descrição dos filtros combinados
     */
    @Override
    public String getDescription() {
        String filterDescriptions = filters.stream()
                .map(PropertyFilter::getDescription)
                .collect(Collectors.joining(" " + operator + " "));
        return String.format("(%s)", filterDescriptions);
    }
}
