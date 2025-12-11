package org.example.csv;

import lombok.*;
import java.math.BigDecimal;

/**
 * Representa uma linha do arquivo CSV importado.
 *
 * Contém os dados da propriedade inseridos pelo usuário via LibreOffice Calc.
 * Formato esperado do CSV:
 * rua,numero,preco,intervalo_preco
 *
 * Exemplo:
 * "Rua Vicente da Costa","150","9999.99","ambos"
 *
 * @version 1.0
 * @author Property Management System
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvPropertyRecord {

    /**
     * Nome da rua (ex: "Rua Vicente da Costa")
     */
    private String street;

    /**
     * Número do imóvel (ex: 150)
     */
    private Integer number;

    /**
     * Preço base da propriedade (ex: 9999.99)
     */
    private BigDecimal price;

    /**
     * Intervalo para geração de preços.
     * Valores válidos:
     * - "acima": gerar preços acima do valor base
     * - "abaixo": gerar preços abaixo do valor base
     * - "ambos": gerar preços tanto acima como abaixo
     */
    private String priceRange;

    /**
     * Endereço completo formatado (gerado automaticamente)
     * Formato: "Rua Nome, Número, Ipiranga"
     */
    private String fullAddress;

    /**
     * Flag indicando se a rua foi validada pela API de geolocalização
     */
    private boolean validated = false;

    /**
     * Mensagem de erro (se houver) durante validação ou processamento
     */
    private String errorMessage;

    /**
     * Formata o endereço completo.
     * Deve ser chamado após a validação.
     *
     * @param neighborhood O bairro (ex: "Ipiranga")
     */
    public void formatFullAddress(String neighborhood) {
        this.fullAddress = String.format("%s, %d, %s", street, number, neighborhood);
    }

    /**
     * Valida se os campos obrigatórios estão preenchidos.
     *
     * @return true se válido, false caso contrário
     */
    public boolean isValid() {
        return street != null && !street.isBlank() &&
                number != null && number > 0 &&
                price != null && price.compareTo(java.math.BigDecimal.ZERO) > 0 &&
                priceRange != null && isPriceRangeValid();
    }

    /**
     * Valida se o intervalo de preço é válido.
     *
     * @return true se "acima", "abaixo" ou "ambos"
     */
    private boolean isPriceRangeValid() {
        return priceRange.equalsIgnoreCase("acima") ||
                priceRange.equalsIgnoreCase("abaixo") ||
                priceRange.equalsIgnoreCase("ambos");
    }

    /**
     * Retorna uma descrição legível do registro.
     */
    @Override
    public String toString() {
        return String.format(
                "%s | R$ %.2f (%s) | %s",
                fullAddress != null ? fullAddress : street,
                price.doubleValue(),
                priceRange,
                validated ? "✓ Validado" : "✗ Não validado"
        );
    }
}
