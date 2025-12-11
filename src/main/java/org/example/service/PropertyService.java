package org.example.service;

import org.example.filter.PropertyFilter;
import org.example.model.Property;
import org.example.observer.PropertyChangeNotifier;
import org.example.observer.PropertyListener;
import org.example.repository.PropertyRepository;
import org.example.strategy.PropertySortStrategy;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de negócio para gerenciar propriedades.
 *
 * Fornece uma interface de alto nível para operações com propriedades,
 * integrando o padrão Repository, Observer e Strategy.
 *
 * @version 1.0
 * @author Property Management System
 */
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyChangeNotifier notifier = new PropertyChangeNotifier();

    /**
     * Adiciona uma propriedade ao sistema e notifica listeners.
     *
     * @param property A propriedade a adicionar
     */
    public void addProperty(Property property) {
        if (property != null && property.isValidPrice()) {
            repository.add(property);
            notifier.notifyPropertyCreated(property);
        }
    }

    /**
     * Remove uma propriedade do sistema e notifica listeners.
     *
     * @param property A propriedade a remover
     */
    public void removeProperty(Property property) {
        if (property != null && repository.remove(property)) {
            notifier.notifyPropertyRemoved(property);
        }
    }

    /**
     * Retorna todas as propriedades.
     *
     * @return Lista de propriedades
     */
    public List<Property> getAllProperties() {
        return repository.findAll();
    }

    /**
     * Encontra uma propriedade pelo seu ID.
     *
     * @param id O UUID da propriedade
     * @return Optional contendo a propriedade
     */
    public Optional<Property> findPropertyById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Atualiza o preço de uma propriedade e notifica listeners.
     *
     * @param property A propriedade
     * @param newPrice O novo preço
     */
    public void updatePropertyPrice(Property property, BigDecimal newPrice) {
        if (property != null && newPrice != null) {
            double oldPrice = property.getPrice().doubleValue();
            property.setPrice(newPrice);
            property.updateTimestamp();
            repository.update(property);
            notifier.notifyPriceChange(property, oldPrice, newPrice.doubleValue());
        }
    }

    /**
     * Atualiza o preço usando valor double.
     *
     * @param property A propriedade
     * @param newPrice O novo preço como double
     */
    public void updatePropertyPrice(Property property, double newPrice) {
        updatePropertyPrice(property, new BigDecimal(newPrice));
    }

    /**
     * Filtra propriedades usando um filtro.
     *
     * @param filter O filtro a aplicar
     * @return Lista de propriedades filtradas
     */
    public List<Property> filterProperties(PropertyFilter filter) {
        return filter.apply(repository.findAll());
    }

    /**
     * Ordena propriedades usando uma estratégia.
     *
     * @param strategy A estratégia de ordenação
     * @return Lista de propriedades ordenadas
     */
    public List<Property> sortProperties(PropertySortStrategy strategy) {
        return strategy.sort(repository.findAll());
    }

    /**
     * Filtra e então ordena as propriedades.
     *
     * @param filter O filtro a aplicar
     * @param strategy A estratégia de ordenação
     * @return Lista filtrada e ordenada
     */
    public List<Property> filterAndSort(PropertyFilter filter, PropertySortStrategy strategy) {
        List<Property> filtered = filterProperties(filter);
        return strategy.sort(filtered);
    }

    /**
     * Inscreve um observador para notificações.
     *
     * @param listener O observador
     */
    public void subscribe(PropertyListener listener) {
        notifier.subscribe(listener);
    }

    /**
     * Desinscreve um observador.
     *
     * @param listener O observador
     */
    public void unsubscribe(PropertyListener listener) {
        notifier.unsubscribe(listener);
    }

    /**
     * Retorna o número de propriedades.
     *
     * @return Quantidade de propriedades
     */
    public int getPropertyCount() {
        return repository.count();
    }
}
