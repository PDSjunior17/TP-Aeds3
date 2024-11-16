import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CompressionSystem {
    private static final String HUFFMAN_EXT = ".huff";
    private static final String LZW_EXT = ".lzw";
    private static int currentVersion = 1;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Sistema de Compressão de Arquivos ===");
            System.out.println("1. Comprimir arquivo");
            System.out.println("2. Descomprimir arquivo");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer
            String version1, version2;
            
            switch (choice) {
                case 1:
                    compressFile(scanner);
                    break;
                case 2:
                    System.out.println("Digite a versão que deseja descomprimir:");
                    version1 = scanner.nextLine();
                    version2 = version1;
                    version1 = "data/planets_huffmanV" + version1 + ".huff";
                    decompressFile(version1);
                    version2 = "data/planets_lzwV" + version2 + ".lzw";
                    decompressFile(version2);
                    break;
                case 3:
                    System.out.println("Encerrando o programa...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
    
    private static void compressFile(Scanner scanner) {
        try {
            String inputFile = "data/planets.db";
            
            if (!new File(inputFile).exists()) {
                System.out.println("Erro: Arquivo não encontrado!");
                return;
            }
            
            // Preparar nomes dos arquivos
            String baseFileName = inputFile.replaceFirst("[.][^.]+$", "");
            String huffmanOutput = baseFileName + "_huffmanV" + currentVersion + HUFFMAN_EXT;
            String lzwOutput = baseFileName + "_lzwV" + currentVersion + LZW_EXT;
            
            // Comprimir com Huffman
            System.out.println("\nIniciando compressão Huffman...");
            long startTime = System.nanoTime();
            long originalSize = new File(inputFile).length();
            HuffmanBinaryCompression.compressFile(inputFile, huffmanOutput);
            long huffmanTime = System.nanoTime() - startTime;
            long huffmanSize = new File(huffmanOutput).length();
            
            // Comprimir com LZW
            System.out.println("\nIniciando compressão LZW...");
            startTime = System.nanoTime();
            LZWCompression.compress(inputFile, lzwOutput);
            long lzwTime = System.nanoTime() - startTime;
            long lzwSize = new File(lzwOutput).length();
            
            // Calcular e mostrar estatísticas
            double huffmanRatio = 100.0 * (1 - (double)huffmanSize/originalSize);
            double lzwRatio = 100.0 * (1 - (double)lzwSize/originalSize);
            
            System.out.println("\n=== Resultados da Compressão ===");
            System.out.printf("Tamanho original: %,d bytes%n", originalSize);
            System.out.println("\nHuffman:");
            System.out.printf("- Tamanho final: %,d bytes%n", huffmanSize);
            System.out.printf("- Taxa de compressão: %.2f%%%n", huffmanRatio);
            System.out.printf("- Tempo de execução: %.2f ms%n", huffmanTime/1e6);
            
            System.out.println("\nLZW:");
            System.out.printf("- Tamanho final: %,d bytes%n", lzwSize);
            System.out.printf("- Taxa de compressão: %.2f%%%n", lzwRatio);
            System.out.printf("- Tempo de execução: %.2f ms%n", lzwTime/1e6);
            
            // Comparar resultados
            System.out.println("\nComparação:");
            if (huffmanRatio > lzwRatio) {
                System.out.println("Huffman teve melhor taxa de compressão!");
            } else if (lzwRatio > huffmanRatio) {
                System.out.println("LZW teve melhor taxa de compressão!");
            } else {
                System.out.println("Ambos algoritmos tiveram a mesma taxa de compressão.");
            }
            
            if (huffmanTime < lzwTime) {
                System.out.println("Huffman foi mais rápido!");
            } else if (lzwTime < huffmanTime) {
                System.out.println("LZW foi mais rápido!");
            } else {
                System.out.println("Ambos algoritmos tiveram o mesmo tempo de execução.");
            }
            
            currentVersion++;
            
        } catch (Exception e) {
            System.err.println("Erro durante a compressão: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void decompressFile(String compressedFile) {
        try {
          
            
            if (!new File(compressedFile).exists()) {
                System.out.println("Erro: Arquivo não encontrado!");
                return;
            }
            
            // Identificar algoritmo pelo nome do arquivo
            boolean isHuffman = compressedFile.contains("_huffmanV");
            boolean isLZW = compressedFile.contains("_lzwV");
            
            if (!isHuffman && !isLZW) {
                System.out.println("Erro: Formato de arquivo não reconhecido!");
                return;
            }
            
            // Extrair nome base do arquivo
            String baseFileName = compressedFile.split("_")[0];
            String outputFile = baseFileName + ".db";
            
            // Descomprimir
            System.out.println("\nIniciando descompressão...");
            long startTime = System.nanoTime();
            
            if (isHuffman) {
                HuffmanBinaryCompression.decompressFile(compressedFile, outputFile);
            } else {
                LZWCompression.decompress(compressedFile, outputFile);
            }
            
            long executionTime = System.nanoTime() - startTime;
            
            System.out.println("\n=== Resultados da Descompressão do arquivo "+ compressedFile + "===");
            System.out.printf("Arquivo descomprimido: %s%n", outputFile);
            System.out.printf("Tempo de execução: %.2f ms%n", executionTime/1e6);
            
        } catch (Exception e) {
            System.err.println("Erro durante a descompressão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
