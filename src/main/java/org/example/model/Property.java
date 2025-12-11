package org.example.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Modelo de dados para uma propriedade imobiliária.
 *
 * Esta classe representa uma propriedade no bairro da Ipiranga com suas
 * características principais: endereço, preço e histórico de modificações.
 *
 * @version 1.0
 * @author Property Management System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    /**
     * Identificador único da propriedade.
     */
    private UUID id;

    /**
     * Endereço completo da propriedade.
     * Formato: "Rua Nome, Número, Bairro"
     * Exemplo: "Rua Vicente da Costa, 150, Ipiranga"
     */
    private String address;

    /**
     * Preço da propriedade em reais brasileiros.
     * Intervalo válido: R$ 8.000,00 a R$ 11.999,99
     */
    private BigDecimal price;

    /**
     * Data e hora de criação do registro.
     */
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação.
     */
    private LocalDateTime updatedAt;

    /**
     * Construtor com dados mínimos (address e price).
     * Gera automaticamente id e timestamps.
     * Valida que address e price não são nulos.
     *
     * @param address Endereço da propriedade (não pode ser null)
     * @param price Preço em reais (não pode ser null)
     * @throws NullPointerException se address ou price forem null
     */
    public Property(String address, BigDecimal price) {
        if (address == null) {
            throw new NullPointerException("Address não pode ser null");
        }
        if (price == null) {
            throw new NullPointerException("Price não pode ser null");
        }

        this.id = UUID.randomUUID();
        this.address = address;
        this.price = price;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Retorna representação em string formatada da propriedade.
     * Usa ponto (.) como separador decimal para manter compatibilidade com locale.
     *
     * @return String contendo endereço e preço formatado com ponto decimal
     */
    @Override
    public String toString() {
        // Usar Locale.US para garantir ponto (.) como separador decimal
        return String.format(
                java.util.Locale.US,
                "%s | R$ %.2f",
                address,
                price.doubleValue()
        );
    }

    /**
     * Atualiza o timestamp de modificação para agora.
     * Deve ser chamado sempre que a propriedade for alterada.
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Valida se o preço está dentro da faixa permitida.
     *
     * @return true se o preço está entre R$ 8.000,00 e R$ 11.999,99
     */
    public boolean isValidPrice() {
        BigDecimal minPrice = new BigDecimal("8000.00");
        BigDecimal maxPrice = new BigDecimal("11999.99");
        return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
    }
}
