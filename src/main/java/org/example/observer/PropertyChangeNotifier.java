package org.example.observer;

import org.example.model.Property;
import java.util.*;

/**
 * Gerenciador do padrão Observer para notificações de propriedades.
 *
 * Mantém uma lista de observadores (listeners) e notifica todos
 * quando eventos ocorrem com as propriedades.
 *
 * Implementa o padrão Composite com uma lista de listeners.
 *
 * @version 1.0
 * @author Property Management System
 */
public class PropertyChangeNotifier {

    private final List<PropertyListener> listeners = new ArrayList<>();

    /**
     * Inscreve um novo observador na lista de notificações.
     *
     * @param listener O observador a ser adicionado
     */
    public void subscribe(PropertyListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Desinscreve um observador da lista de notificações.
     *
     * @param listener O observador a ser removido
     */
    public void unsubscribe(PropertyListener listener) {
        listeners.remove(listener);
    }

    /**
     * Remove todos os observadores.
     */
    public void unsubscribeAll() {
        listeners.clear();
    }

    /**
     * Notifica todos os observadores sobre mudança de preço.
     *
     * @param property A propriedade cujo preço mudou
     * @param oldPrice O preço anterior
     * @param newPrice O novo preço
     */
    public void notifyPriceChange(Property property, double oldPrice, double newPrice) {
        listeners.forEach(listener -> listener.onPriceChanged(property, oldPrice, newPrice));
    }

    /**
     * Notifica todos os observadores sobre criação de propriedade.
     *
     * @param property A propriedade criada
     */
    public void notifyPropertyCreated(Property property) {
        listeners.forEach(listener -> listener.onPropertyCreated(property));
    }

    /**
     * Notifica todos os observadores sobre remoção de propriedade.
     *
     * @param property A propriedade removida
     */
    public void notifyPropertyRemoved(Property property) {
        listeners.forEach(listener -> listener.onPropertyRemoved(property));
    }

    /**
     * Retorna a quantidade de observadores registrados.
     *
     * @return Número de listeners
     */
    public int getListenerCount() {
        return listeners.size();
    }

    /**
     * Retorna uma cópia da lista de listeners.
     *
     * @return Lista de PropertyListener
     */
    public List<PropertyListener> getListeners() {
        return new ArrayList<>(listeners);
    }
}
