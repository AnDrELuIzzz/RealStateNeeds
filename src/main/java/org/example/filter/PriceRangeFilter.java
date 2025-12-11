package org.example.filter;

import org.example.model.Property;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro para propriedades por faixa de preço.
 *
 * Filtra propriedades que possuem preço dentro de um intervalo especificado.
 *
 * @version 1.0
 * @author Property Management System
 */
@RequiredArgsConstructor
public class PriceRangeFilter implements PropertyFilter {

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    /**
     * Construtor alternativo que aceita valores double.
     *
     * @param minPrice Preço mínimo
     * @param maxPrice Preço máximo
     */
    public PriceRangeFilter(double minPrice, double maxPrice) {
        this(new BigDecimal(minPrice), new BigDecimal(maxPrice));
    }

    /**
     * Filtra propriedades por faixa de preço.
     *
     * @param properties Lista de propriedades
     * @return Propriedades com preço entre minPrice e maxPrice
     */
    @Override
    public List<Property> apply(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0 &&
                        p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    /**
     * Retorna descrição do filtro.
     *
     * @return String com intervalo de preço
     */
    @Override
    public String getDescription() {
        return String.format("Preço entre R$ %.2f e R$ %.2f",
                minPrice.doubleValue(),
                maxPrice.doubleValue());
    }
}
