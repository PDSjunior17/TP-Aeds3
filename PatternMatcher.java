import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PatternMatcher {
    private List<Integer> foundIntegers;
    private static final int INTEGER_SIZE = 4;
    private long comparisons; 
    private long executionTime; 

    public PatternMatcher() {
        this.foundIntegers = new ArrayList<>();
    }

    public List<Integer> searchKMP(String pattern) throws IOException {
        foundIntegers.clear();
        comparisons = 0; // Reseta contador
        long startTime = System.nanoTime(); // Comeca a marcar o tempo
        
        byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        int[] failure = computeKMPFailure(patternBytes);
        
        try (RandomAccessFile raf = new RandomAccessFile("data/planets.db", "r")) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;
            int j = 0;
            
            while ((bytesRead = raf.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    while (j > 0 && patternBytes[j] != buffer[i]) {
                        comparisons++; // Conta comparacoes
                        j = failure[j - 1];
                    }
                    comparisons++; // Conta comparacoes
                    if (patternBytes[j] == buffer[i]) {
                        j++;
                    }
                    if (j == patternBytes.length) {
                        long matchPosition = totalBytesRead + i - patternBytes.length + 1;
                        int extractedInt = extractIntegerAfterLastMarker(raf, matchPosition);
                        if (extractedInt != Integer.MIN_VALUE) {
                            foundIntegers.add(extractedInt);
                        }
                        j = failure[j - 1];
                    }
                }
                totalBytesRead += bytesRead;
            }
        }
        
        executionTime = System.nanoTime() - startTime; // Calculo empo de execucao
        
        // Print metrics
        System.out.println("\nKMP Search Metrics:");
        System.out.println("Number of comparisons: " + comparisons);
        System.out.println("Execution time: " + executionTime / 1_000_000.0 + " ms");
        System.out.println("Number of matches found: " + foundIntegers.size());
        
        return foundIntegers;
    }

    public List<Integer> searchBoyerMoore(String pattern) throws IOException {
        foundIntegers.clear();
        comparisons = 0;
        long startTime = System.nanoTime();
        
        byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        
        int[] badChar = new int[256];
        for (int i = 0; i < 256; i++) {
            badChar[i] = -1;
        }
        for (int i = 0; i < patternBytes.length; i++) {
            badChar[patternBytes[i] & 0xFF] = i;
        }
        
        try (RandomAccessFile raf = new RandomAccessFile("data/planets.db", "r")) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;
            
            while ((bytesRead = raf.read(buffer)) != -1) {
                int i = 0;
                while (i <= bytesRead - patternBytes.length) {
                    int j = patternBytes.length - 1;
                    
                    while (j >= 0 && patternBytes[j] == buffer[i + j]) {
                        comparisons++; // Conta comparacoes
                        j--;
                    }
                    if (j >= 0) {
                        comparisons++; // Conta comparacoes
                    }
                    
                    if (j < 0) {
                        long matchPosition = totalBytesRead + i;
                        int extractedInt = extractIntegerAfterLastMarker(raf, matchPosition);
                        if (extractedInt != Integer.MIN_VALUE) {
                            foundIntegers.add(extractedInt);
                        }
                        i += (i + patternBytes.length < bytesRead) ? 1 : patternBytes.length;
                    } else {
                        i += Math.max(1, j - badChar[buffer[i + j] & 0xFF]);
                    }
                }
                totalBytesRead += bytesRead;
            }
        }
        
        executionTime = System.nanoTime() - startTime; // tempo de execucao
        
        // Print metrics
        System.out.println("\nBoyer-Moore Search Metrics:");
        System.out.println("Number of comparisons: " + comparisons);
        System.out.println("Execution time: " + executionTime / 1_000_000.0 + " ms");
        System.out.println("Number of matches found: " + foundIntegers.size());
        
        return foundIntegers;
    }


    public long getComparisons() {
        return comparisons;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    private int extractIntegerAfterLastMarker(RandomAccessFile raf, long position) throws IOException {
        long savedPosition = raf.getFilePointer();
        
        try {
            for (long pos = position; pos >= 2; pos--) {
                raf.seek(pos - 2);
                char marker = raf.readChar();
                if (marker == '+' || marker == '!') {
                    byte[] intBytes = new byte[INTEGER_SIZE];
                    raf.read(intBytes);
                    return ByteBuffer.wrap(intBytes).getInt();
                }
            }
            return Integer.MIN_VALUE; 
        } finally {
            raf.seek(savedPosition);
        }
    }

    private int[] computeKMPFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int j = 0;
        
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }

    public List<Integer> getFoundIntegers() {
        return new ArrayList<>(foundIntegers);
    }
}