package org.example.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Leitor de arquivos CSV com dados de propriedades.
 *
 * Lê um arquivo CSV do LibreOffice Calc com as colunas:
 * rua, numero, preco, intervalo_preco
 *
 * Exemplo de arquivo CSV:
 * ```
 * rua,numero,preco,intervalo_preco
 * "Rua Vicente da Costa",150,9999.99,ambos
 * "Rua Moreira e Costa",200,8500.50,acima
 * "Rua Bom Pastor",300,11000.00,abaixo
 * ```
 *
 * @version 1.0
 * @author Property Management System
 */
public class CsvPropertyReader {

    private static final String[] HEADERS = {"rua", "numero", "preco", "intervalo_preco"};
    private static final String NEIGHBORHOOD = "Ipiranga";

    /**
     * Lê um arquivo CSV e retorna lista de registros.
     *
     * @param filePath Caminho do arquivo CSV
     * @return Lista de CsvPropertyRecord lidos do arquivo
     * @throws IOException se houver erro ao ler o arquivo
     * @throws IllegalArgumentException se o arquivo tiver formato inválido
     */
    public List<CsvPropertyRecord> read(String filePath) throws IOException {
        return read(Path.of(filePath));
    }

    /**
     * Lê um arquivo CSV usando Path.
     *
     * @param path Path do arquivo CSV
     * @return Lista de CsvPropertyRecord
     * @throws IOException se houver erro ao ler o arquivo
     */
    public List<CsvPropertyRecord> read(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Arquivo não encontrado: " + path);
        }

        List<CsvPropertyRecord> records = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(path);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADERS))) {

            for (CSVRecord record : csvParser) {
                // Pular linhas vazias
                if (record.size() == 0 || record.get(0).isEmpty()) {
                    continue;
                }

                try {
                    CsvPropertyRecord property = parseLine(record);
                    property.formatFullAddress(NEIGHBORHOOD);
                    records.add(property);

                    System.out.println("✓ Registro lido: " + property.getStreet() + ", " + property.getNumber());
                } catch (IllegalArgumentException e) {
                    // Registrar erro, mas continuar processando
                    System.err.println("⚠️ Erro na linha " + record.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }

        if (records.isEmpty()) {
            throw new IllegalArgumentException("Nenhum registro válido encontrado no arquivo");
        }

        System.out.println("\n✅ Total de registros carregados: " + records.size());
        return records;
    }

    /**
     * Parseia uma linha do CSV para CsvPropertyRecord.
     *
     * @param record Registro do CSVParser
     * @return CsvPropertyRecord parseado
     * @throws IllegalArgumentException se houver erro no parsing
     */
    private CsvPropertyRecord parseLine(CSVRecord record) {
        try {
            String street = record.get("rua").trim();
            Integer number = Integer.parseInt(record.get("numero").trim());
            BigDecimal price = new BigDecimal(record.get("preco").trim().replace(",", "."));
            String priceRange = record.get("intervalo_preco").trim().toLowerCase();

            if (street.isBlank()) {
                throw new IllegalArgumentException("Rua não pode estar vazia");
            }
            if (number <= 0) {
                throw new IllegalArgumentException("Número deve ser maior que 0");
            }
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Preço deve ser maior que 0");
            }
            if (!isValidPriceRange(priceRange)) {
                throw new IllegalArgumentException("intervalo_preco deve ser 'acima', 'abaixo' ou 'ambos'");
            }

            return CsvPropertyRecord.builder()
                    .street(street)
                    .number(number)
                    .price(price)
                    .priceRange(priceRange)
                    .build();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato inválido nos dados: " + e.getMessage(), e);
        }
    }

    /**
     * Valida se a string é um intervalo de preço válido.
     *
     * @param priceRange String com o intervalo
     * @return true se válido
     */
    private boolean isValidPriceRange(String priceRange) {
        return priceRange.equals("acima") ||
                priceRange.equals("abaixo") ||
                priceRange.equals("ambos");
    }

    /**
     * Cria um arquivo CSV de exemplo.
     *
     * @param filePath Caminho onde criar o arquivo
     * @throws IOException se houver erro ao escrever
     */
    public static void createExampleFile(String filePath) throws IOException {
        String csv = """
            rua,numero,preco,intervalo_preco
            "Rua Vicente da Costa",150,9999.99,ambos
            "Rua Moreira e Costa",200,8500.50,acima
            "Rua Xavier de Almeida",300,10500.00,abaixo
            "Rua Rodrigues do Prado",400,11000.00,ambos
            "Rua Clóvis Bueno de Azevedo",500,8750.25,acima
            """;

        Files.writeString(Path.of(filePath), csv);
        System.out.println("Arquivo de exemplo criado: " + filePath);
    }
}
