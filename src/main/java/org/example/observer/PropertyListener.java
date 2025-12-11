package org.example.observer;

import org.example.model.Property;

/**
 * Interface para o padrão Observer.
 *
 * Define o contrato que todos os observadores devem implementar
 * para receber notificações sobre mudanças em uma propriedade.
 *
 * @version 1.0
 * @author Property Management System
 */
public interface PropertyListener {

    /**
     * Chamado quando o preço de uma propriedade é alterado.
     *
     * @param property A propriedade cujo preço foi alterado
     * @param oldPrice O preço anterior
     * @param newPrice O novo preço
     */
    void onPriceChanged(Property property, double oldPrice, double newPrice);

    /**
     * Chamado quando uma propriedade é criada.
     *
     * @param property A propriedade recém-criada
     */
    void onPropertyCreated(Property property);

    /**
     * Chamado quando uma propriedade é removida.
     *
     * @param property A propriedade removida
     */
    void onPropertyRemoved(Property property);
}
