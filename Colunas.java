import java.io.*;
import java.util.*;

public class Colunas {

    // Função para criptografar usando Transposição de Colunas
    public static void encrypt(String inputFilename, String outputFilename, String key) throws IOException {
        byte[] data = readFromFile(inputFilename);
        int numRows = (int) Math.ceil((double) data.length / key.length());
        byte[][] grid = new byte[numRows][key.length()];

        // Preencher a matriz com os bytes do arquivo
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < key.length(); j++) {
                if (index < data.length) {
                    grid[i][j] = data[index++];
                } else {
                    grid[i][j] = 0; // Preenchimento com zero
                }
            }
        }

        // Obter a ordem das colunas com base na chave
        Integer[] columnOrder = getColumnOrder(key);

        // Construir o array criptografado lendo as colunas na ordem
        ByteArrayOutputStream encryptedData = new ByteArrayOutputStream();
        for (int col : columnOrder) {
            for (int row = 0; row < numRows; row++) {
                encryptedData.write(grid[row][col]);
            }
        }

        saveToFile(outputFilename, encryptedData.toByteArray());
    }

    // Função para descriptografar usando Transposição de Colunas
    public static void decrypt(String inputFilename, String outputFilename, String key) throws IOException {
        byte[] encryptedData = readFromFile(inputFilename);
        int numRows = (int) Math.ceil((double) encryptedData.length / key.length());
        byte[][] grid = new byte[numRows][key.length()];

        // Obter a ordem das colunas com base na chave
        Integer[] columnOrder = getColumnOrder(key);

        // Preencher a matriz com os bytes do arquivo criptografado
        int index = 0;
        for (int col : columnOrder) {
            for (int row = 0; row < numRows; row++) {
                if (index < encryptedData.length) {
                    grid[row][col] = encryptedData[index++];
                }
            }
        }

        // Construir o array original lendo linha por linha
        ByteArrayOutputStream decryptedData = new ByteArrayOutputStream();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < key.length(); j++) {
                decryptedData.write(grid[i][j]);
            }
        }

        saveToFile(outputFilename, decryptedData.toByteArray());
    }

    // Obter a ordem das colunas com base na chave
    private static Integer[] getColumnOrder(String key) {
        Integer[] order = new Integer[key.length()];
        for (int i = 0; i < key.length(); i++) {
            order[i] = i;
        }

        Arrays.sort(order, Comparator.comparingInt(key::charAt));
        return order;
    }

    // Lê dados binários de um arquivo
    public static byte[] readFromFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            return fis.readAllBytes();
        }
    }

    // Salva dados binários em um arquivo
    public static void saveToFile(String filename, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data);
        }
    }
}
