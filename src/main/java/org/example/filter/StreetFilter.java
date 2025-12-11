package org.example.filter;

import org.example.model.Property;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro para propriedades por rua/endereço.
 *
 * Filtra propriedades que estão localizadas em uma rua específica.
 * A busca é case-sensitive e requer correspondência exata com a rua.
 *
 * @version 1.0
 * @author Property Management System
 */
@RequiredArgsConstructor
public class StreetFilter implements PropertyFilter {

    private final String street;

    /**
     * Filtra propriedades pela rua.
     *
     * @param properties Lista de propriedades
     * @return Propriedades localizadas na rua especificada
     */
    @Override
    public List<Property> apply(List<Property> properties) {
        return properties.stream()
                .filter(p -> p.getAddress().contains(street))
                .collect(Collectors.toList());
    }

    /**
     * Retorna descrição do filtro.
     *
     * @return String com nome da rua
     */
    @Override
    public String getDescription() {
        return String.format("Propriedades na rua: %s", street);
    }
}
