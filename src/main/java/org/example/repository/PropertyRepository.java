package org.example.repository;

import org.example.model.Property;
import java.util.*;

/**
 * Camada de repositório para acesso aos dados de propriedades.
 *
 * Fornece operações CRUD (Create, Read, Update, Delete) para propriedades.
 * Atua como um intermediário entre a camada de serviço e os dados.
 *
 * @version 1.0
 * @author Property Management System
 */
public class PropertyRepository {

    private final List<Property> properties;

    /**
     * Construtor com lista inicial de propriedades.
     *
     * @param properties Lista de propriedades para inicializar o repositório
     */
    public PropertyRepository(List<Property> properties) {
        this.properties = new ArrayList<>(properties);
    }

    /**
     * Construtor sem argumentos cria repositório vazio.
     */
    public PropertyRepository() {
        this.properties = new ArrayList<>();
    }

    /**
     * Adiciona uma propriedade ao repositório.
     *
     * @param property A propriedade a adicionar
     */
    public void add(Property property) {
        if (property != null) {
            properties.add(property);
        }
    }

    /**
     * Remove uma propriedade do repositório.
     *
     * @param property A propriedade a remover
     * @return true se foi removida, false caso não estivesse no repositório
     */
    public boolean remove(Property property) {
        return properties.remove(property);
    }

    /**
     * Encontra uma propriedade pelo seu ID.
     *
     * @param id O UUID da propriedade
     * @return Optional contendo a propriedade ou vazio
     */
    public Optional<Property> findById(UUID id) {
        return properties.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Retorna todas as propriedades.
     *
     * @return Lista de propriedades (cópia defensiva)
     */
    public List<Property> findAll() {
        return new ArrayList<>(properties);
    }

    /**
     * Retorna a quantidade de propriedades.
     *
     * @return Número de propriedades
     */
    public int count() {
        return properties.size();
    }

    /**
     * Verifica se existe uma propriedade com o dado ID.
     *
     * @param id O UUID da propriedade
     * @return true se existe, false caso contrário
     */
    public boolean existsById(UUID id) {
        return properties.stream().anyMatch(p -> p.getId().equals(id));
    }

    /**
     * Limpa todas as propriedades do repositório.
     */
    public void clear() {
        properties.clear();
    }

    /**
     * Atualiza uma propriedade existente.
     *
     * @param property A propriedade com dados atualizados
     * @return true se foi atualizada, false caso não estivesse no repositório
     */
    public boolean update(Property property) {
        if (property == null) return false;

        Optional<Property> existing = findById(property.getId());
        if (existing.isPresent()) {
            int index = properties.indexOf(existing.get());
            properties.set(index, property);
            return true;
        }
        return false;
    }
}