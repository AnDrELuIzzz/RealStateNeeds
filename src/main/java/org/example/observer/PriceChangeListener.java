package org.example.observer;

import org.example.model.Property;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação concreta de PropertyListener para auditar mudanças de preço.
 *
 * Esta classe registra (log) todas as alterações de preço, criação e
 * remoção de propriedades para fins de auditoria.
 *
 * @version 1.0
 * @author Property Management System
 */

public class PriceChangeListener implements PropertyListener {

    /**
     * Registra quando o preço de uma propriedade é alterado.
     *
     * @param property A propriedade cujo preço foi alterado
     * @param oldPrice O preço anterior
     * @param newPrice O novo preço
     */
    @Override
    public void onPriceChanged(Property property, double oldPrice, double newPrice) {
        double difference = newPrice - oldPrice;
        String operation = difference > 0 ? "AUMENTO" : "REDUÇÃO";
        String message = String.format(
                "[AUDITORIA] %s - Endereço: %s | Preço anterior: R$ %.2f | Novo preço: R$ %.2f | %s: R$ %.2f",
                operation,
                property.getAddress(),
                oldPrice,
                newPrice,
                operation,
                Math.abs(difference)
        );
        System.out.println(message);
    }

    /**
     * Registra quando uma propriedade é criada.
     *
     * @param property A propriedade recém-criada
     */
    @Override
    public void onPropertyCreated(Property property) {
        String message = String.format(
                "[CRIAÇÃO] Nova propriedade cadastrada - Endereço: %s | Preço: R$ %.2f | ID: %s",
                property.getAddress(),
                property.getPrice().doubleValue(),
                property.getId()
        );
        System.out.println(message);
    }

    /**
     * Registra quando uma propriedade é removida.
     *
     * @param property A propriedade removida
     */
    @Override
    public void onPropertyRemoved(Property property) {
        String message = String.format(
                "[REMOÇÃO] Propriedade removida - Endereço: %s | Preço: R$ %.2f | ID: %s",
                property.getAddress(),
                property.getPrice().doubleValue(),
                property.getId()
        );
        System.out.println(message);
    }
}