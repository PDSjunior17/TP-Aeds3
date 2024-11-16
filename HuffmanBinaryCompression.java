import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode implements Serializable {
    Byte data; // Suporta bytes
    int frequency;
    HuffmanNode left, right;

    // Construtor
    HuffmanNode(Byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
        left = right = null;
    }
}

public class HuffmanBinaryCompression {

    public static void main(String[] args) {
        String inputFile = "data/planets.db";
        String compressedFile = "data/arquivo_comprimido.huff";
        String decompressedFile = "data/arquivo_descomprimido.dat";

        try {
            // 1. Compressão
            compressFile(inputFile, compressedFile);

            // 2. Descompressão
            decompressFile(compressedFile, decompressedFile);

            System.out.println("Arquivo descomprimido salvo em: " + decompressedFile);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    // Método para comprimir o arquivo
    public static void compressFile(String inputFile, String compressedFile) throws IOException {
        // Ler o arquivo binário
        byte[] fileData = Files.readAllBytes(Paths.get(inputFile));

        // Calcular a frequência de cada byte
        HashMap<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : fileData) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }

        // Criar a fila de prioridade (min-heap)
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>((a, b) -> a.frequency - b.frequency);
        for (Byte b : frequencyMap.keySet()) {
            priorityQueue.add(new HuffmanNode(b, frequencyMap.get(b)));
        }

        // Construir a árvore de Huffman
        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode newNode = new HuffmanNode(null, left.frequency + right.frequency);
            newNode.left = left;
            newNode.right = right;
            priorityQueue.add(newNode);
        }
        HuffmanNode root = priorityQueue.poll(); // Raiz da árvore

        // Construir o mapa de códigos
        HashMap<Byte, String> codeMap = new HashMap<>();
        buildCodeMap(root, new StringBuilder(), codeMap);

        // Codificar os dados
        StringBuilder compressedData = new StringBuilder();
        for (byte b : fileData) {
            compressedData.append(codeMap.get(b));
        }

        // Converter a sequência de bits para bytes
        byte[] compressedBytes = convertBitsToBytes(compressedData);

        // Salvar o arquivo comprimido (dados + árvore)
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(compressedFile))) {
            outputStream.writeObject(root); // Salvar a árvore de Huffman
            outputStream.writeObject(compressedBytes); // Salvar os dados comprimidos
        }

        System.out.println("Arquivo comprimido salvo em: " + compressedFile);
    }

    // Método para descomprimir o arquivo
    public static void decompressFile(String compressedFile, String decompressedFile) throws IOException, ClassNotFoundException {
        // Ler o arquivo comprimido
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(compressedFile))) {
            HuffmanNode root = (HuffmanNode) inputStream.readObject(); // Árvore de Huffman
            byte[] compressedBytes = (byte[]) inputStream.readObject(); // Dados comprimidos

            // Converter os bytes comprimidos de volta para uma sequência de bits
            StringBuilder bitString = convertBytesToBits(compressedBytes);

            // Decodificar os dados usando a árvore de Huffman
            ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
            HuffmanNode currentNode = root;

            for (int i = 0; i < bitString.length(); i++) {
                char bit = bitString.charAt(i);
                currentNode = (bit == '0') ? currentNode.left : currentNode.right;

                // Se chegarmos a um nó folha, escrevemos o byte no resultado
                if (currentNode.left == null && currentNode.right == null) {
                    decompressedData.write(currentNode.data);
                    currentNode = root;
                }
            }

            // Salvar os dados descomprimidos em um arquivo
            Files.write(Paths.get(decompressedFile), decompressedData.toByteArray());
        }
    }

    // Método recursivo para construir o mapa de códigos
    public static void buildCodeMap(HuffmanNode root, StringBuilder code, HashMap<Byte, String> codeMap) {
        if (root == null) return;

        if (root.data != null) { // Nó folha contém um byte válido
            codeMap.put(root.data, code.toString());
        }

        if (root.left != null) {
            buildCodeMap(root.left, code.append('0'), codeMap);
            code.deleteCharAt(code.length() - 1);
        }

        if (root.right != null) {
            buildCodeMap(root.right, code.append('1'), codeMap);
            code.deleteCharAt(code.length() - 1);
        }
    }

    // Converte uma sequência de bits em um array de bytes
    public static byte[] convertBitsToBytes(StringBuilder bitString) {
        int length = (bitString.length() + 7) / 8; // Arredonda para cima
        byte[] bytes = new byte[length];
        int byteIndex = 0;
        int bitIndex = 0;

        for (int i = 0; i < bitString.length(); i++) {
            if (bitString.charAt(i) == '1') {
                bytes[byteIndex] |= (1 << (7 - bitIndex));
            }
            bitIndex++;
            if (bitIndex == 8) {
                bitIndex = 0;
                byteIndex++;
            }
        }

        return bytes;
    }

    // Converte um array de bytes em uma sequência de bits
    public static StringBuilder convertBytesToBits(byte[] bytes) {
        StringBuilder bitString = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                bitString.append((b >> i) & 1);
            }
        }
        return bitString;
    }
}
