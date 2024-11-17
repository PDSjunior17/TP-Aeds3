import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CompressionSystem {
    private static final String HUFFMAN_EXT = ".huff";
    private static final String LZW_EXT = ".lzw";
    
    // Retorna uma string com os resultados da compressão para exibição na GUI
    public static String compressFile(String version) throws Exception {
        StringBuilder results = new StringBuilder();
        try {
            String inputFile = "data/planets.db";
            
            if (!new File(inputFile).exists()) {
                throw new Exception("Arquivo não encontrado!");
            }
            
            // Prepara os nomes dos arquivos
            String baseFileName = inputFile.replaceFirst("[.][^.]+$", "");
            String huffmanOutput = baseFileName + "_huffmanV" + version + HUFFMAN_EXT;
            String lzwOutput = baseFileName + "_lzwV" + version + LZW_EXT;
            
            // Comprime com Huffman
            long startTime = System.nanoTime();
            long originalSize = new File(inputFile).length();
            HuffmanBinaryCompression.compressFile(inputFile, huffmanOutput);
            long huffmanTime = System.nanoTime() - startTime;
            long huffmanSize = new File(huffmanOutput).length();
            
            // Comprime com LZW
            startTime = System.nanoTime();
            LZWCompression.compress(inputFile, lzwOutput);
            long lzwTime = System.nanoTime() - startTime;
            long lzwSize = new File(lzwOutput).length();
            
            // Calcula e exibe as estatísticas
            double huffmanRatio = 100.0 * (1 - (double)huffmanSize/originalSize);
            double lzwRatio = 100.0 * (1 - (double)lzwSize/originalSize);
            
            results.append("\n=== Resultados da Compressão ===\n");
            results.append(String.format("Tamanho original: %,d bytes%n", originalSize));
            results.append("\nHuffman:\n");
            results.append(String.format("- Tamanho final: %,d bytes%n", huffmanSize));
            results.append(String.format("- Taxa de compressão: %.2f%%%n", huffmanRatio));
            results.append(String.format("- Tempo de execução: %.2f ms%n", huffmanTime/1e6));
            
            results.append("\nLZW:\n");
            results.append(String.format("- Tamanho final: %,d bytes%n", lzwSize));
            results.append(String.format("- Taxa de compressão: %.2f%%%n", lzwRatio));
            results.append(String.format("- Tempo de execução: %.2f ms%n", lzwTime/1e6));
            
            // Compara os resultados
            results.append("\nComparação:\n");
            if (huffmanRatio > lzwRatio) {
                results.append("Huffman teve melhor taxa de compressão!\n");
            } else if (lzwRatio > huffmanRatio) {
                results.append("LZW teve melhor taxa de compressão!\n");
            } else {
                results.append("Ambos os algoritmos tiveram a mesma taxa de compressão.\n");
            }
            
            if (huffmanTime < lzwTime) {
                results.append("Huffman foi mais rápido!\n");
            } else if (lzwTime < huffmanTime) {
                results.append("LZW foi mais rápido!\n");
            } else {
                results.append("Ambos os algoritmos tiveram o mesmo tempo de execução.\n");
            }
            
            return results.toString();
            
        } catch (Exception e) {
            throw new Exception("Erro durante a compressão: " + e.getMessage());
        }
    }
    
    public static String decompressFile(String compressedFile) throws Exception {
        StringBuilder results = new StringBuilder();
        try {
            if (!new File(compressedFile).exists()) {
                throw new Exception("Arquivo não encontrado!");
            }
            
            // Identifica o algoritmo pelo nome do arquivo
            boolean isHuffman = compressedFile.contains("_huffmanV");
            boolean isLZW = compressedFile.contains("_lzwV");
            
            if (!isHuffman && !isLZW) {
                throw new Exception("Formato de arquivo não reconhecido!");
            }
            
            // Extrai o nome base do arquivo
            String baseFileName = compressedFile.split("_")[0];
            String outputFile = baseFileName + ".db";
            
            // Descomprime
            long startTime = System.nanoTime();
            
            if (isHuffman) {
                HuffmanBinaryCompression.decompressFile(compressedFile, outputFile);
            } else {
                LZWCompression.decompress(compressedFile, outputFile);
            }
            
            long executionTime = System.nanoTime() - startTime;
            
            results.append("\n=== Resultados da Descompressão para " + compressedFile + " ===\n");
            results.append(String.format("Arquivo descomprimido: %s%n", outputFile));
            results.append(String.format("Tempo de execução: %.2f ms%n", executionTime/1e6));
            
            return results.toString();
            
        } catch (Exception e) {
            throw new Exception("Erro durante a descompressão: " + e.getMessage());
        }
    }
}
