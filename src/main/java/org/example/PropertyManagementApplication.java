package org.example;

import org.example.csv.*;
import org.example.export.PropertyExporter;
import org.example.model.Property;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Aplicação para gerar propriedades ao redor de um endereço base.
 *
 * Fluxo:
 * 1. Usuário insere 1 linha no CSV: rua + número + preço + intervalo
 * 2. Sistema busca ruas próximas
 * 3. Gera múltiplas residências com números aleatórios
 * 4. Gera preços (acima/abaixo/ambos)
 * 5. Exporta resultado em TXT formatado
 *
 * @version 3.0
 * @author Property Management System
 */
public class PropertyManagementApplication {

    public static void main(String[] args) {
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("  Sistema de Geração de Propriedades ao Redor do Endereço Base");
        System.out.println("════════════════════════════════════════════════════════════════\n");

        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Obter arquivo CSV
            System.out.println("📁 Opções:");
            System.out.println("1. Importar arquivo CSV");
            System.out.println("2. Usar exemplo (5 ruas pré-configuradas)");
            System.out.println("3. Sair\n");
            System.out.print("Escolha uma opção (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            List<CsvPropertyRecord> csvRecords = null;

            switch (choice) {
                case 1 -> {
                    // Importar CSV
                    System.out.print("\nCaminho do arquivo CSV: ");
                    String filePath = scanner.nextLine();

                    try {
                        CsvPropertyReader reader = new CsvPropertyReader();
                        csvRecords = reader.read(filePath);

                        if (csvRecords.isEmpty()) {
                            System.out.println("❌ Arquivo vazio!");
                            return;
                        }

                        System.out.println("\n✓ Registros carregados com sucesso!");
                        exibirRegistros(csvRecords);

                    } catch (IOException e) {
                        System.err.println("❌ Erro ao ler arquivo: " + e.getMessage());
                        return;
                    }
                }
                case 2 -> {
                    // Exemplo
                    csvRecords = criarExemploRegistros();

                    System.out.println("\n✓ Exemplo carregado com 5 ruas!");
                    exibirRegistros(csvRecords);
                }
                case 3 -> {
                    System.out.println("Saindo...");
                    return;
                }
                default -> {
                    System.out.println("Opção inválida!");
                    return;
                }
            }

            if (csvRecords == null || csvRecords.isEmpty()) {
                System.out.println("Nenhum registro carregado!");
                return;
            }

            // 2. Perguntar quantidade de propriedades
            System.out.print("\n📊 Quantas propriedades gerar POR RUA? (padrão: 5): ");
            int quantityPerStreet = 5;
            if (scanner.hasNextInt()) {
                quantityPerStreet = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
            }

            if (quantityPerStreet < 1) quantityPerStreet = 5;

            // 3. Perguntar quantidade de ruas próximas
            System.out.print("🗺️  Quantas ruas próximas gerar? (padrão: 3): ");
            int nearbyStreetsCount = 3;
            if (scanner.hasNextInt()) {
                nearbyStreetsCount = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
            }

            if (nearbyStreetsCount < 1) nearbyStreetsCount = 3;

            // 4. Gerar propriedades para TODOS os registros
            System.out.println("\n⏳ Gerando propriedades para " + csvRecords.size() + " rua(s)...\n");

            NeighborhoodPropertyGenerator generator = new NeighborhoodPropertyGenerator();
            List<Property> allProperties = new ArrayList<>();

            int recordNumber = 1;
            for (CsvPropertyRecord csvRecord : csvRecords) {
                System.out.println("Processando registro " + recordNumber + "/" + csvRecords.size() + ":");

                List<Property> properties = generator.generateNeighborhoodProperties(
                        csvRecord,
                        quantityPerStreet,
                        nearbyStreetsCount
                );
                allProperties.addAll(properties);
                recordNumber++;
            }

            // 5. Exportar para TXT
            String filename = "imoveis_" + System.currentTimeMillis() + ".txt";
            System.out.println("\n💾 Exportando para arquivo: " + filename);

            PropertyExporter exporter = new PropertyExporter();
            String fullPath = exporter.exportToTxt(
                    allProperties,
                    filename,
                    "Relatório Geral - " + csvRecords.size() + " rua(s) processada(s)"
            );

            System.out.println("✅ Arquivo criado com sucesso!");
            System.out.println("   Caminho: " + fullPath);
            System.out.println("   Total de imóveis: " + allProperties.size());

            // 6. Perguntar se quer exibir
            System.out.print("\n👀 Exibir arquivo no terminal? (s/n): ");
            String displayChoice = scanner.nextLine();

            if (displayChoice.equalsIgnoreCase("s")) {
                System.out.println("\n");
                exporter.displayInTerminal(filename);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Exibe os registros carregados do CSV.
     */
    private static void exibirRegistros(List<CsvPropertyRecord> records) {
        System.out.println("\n📋 Registros carregados:");
        int count = 1;
        for (CsvPropertyRecord record : records) {
            System.out.println(count + ". " + record.getStreet() +
                    ", nº " + record.getNumber() +
                    ", R$ " + String.format("%.2f", record.getPrice().doubleValue()) +
                    ", " + record.getPriceRange());
            count++;
        }
    }

    /**
     * Cria registros de exemplo.
     */
    private static List<CsvPropertyRecord> criarExemploRegistros() {
        List<CsvPropertyRecord> records = new ArrayList<>();

        records.add(CsvPropertyRecord.builder()
                .street("Rua Vicente da Costa")
                .number(150)
                .price(new java.math.BigDecimal("9999.99"))
                .priceRange("ambos")
                .build());

        records.add(CsvPropertyRecord.builder()
                .street("Rua Moreira e Costa")
                .number(200)
                .price(new java.math.BigDecimal("8500.50"))
                .priceRange("acima")
                .build());

        records.add(CsvPropertyRecord.builder()
                .street("Rua Xavier de Almeida")
                .number(300)
                .price(new java.math.BigDecimal("10500.00"))
                .priceRange("abaixo")
                .build());

        records.add(CsvPropertyRecord.builder()
                .street("Rua Rodrigues do Prado")
                .number(400)
                .price(new java.math.BigDecimal("11000.00"))
                .priceRange("ambos")
                .build());

        records.add(CsvPropertyRecord.builder()
                .street("Rua Clóvis Bueno de Azevedo")
                .number(500)
                .price(new java.math.BigDecimal("8750.25"))
                .priceRange("acima")
                .build());

        // Formatar endereços completos
        for (CsvPropertyRecord record : records) {
            record.formatFullAddress("Ipiranga");
        }

        return records;
    }
}

