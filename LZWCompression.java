import java.io.*;
import java.util.*;

public class LZWCompression {
    private static final int MAX_DICT_SIZE = 4096;
    private static final int BITS_PER_CODE = 12;
    
    public static void main(String[] args) {
        String filename = "data/planets.db";
        
        try {
            // Comprime o arquivo
            compress(filename, filename + ".lzw");
            
            // Mostra estatísticas
            File original = new File(filename);
            File compressed = new File(filename + ".lzw");
            System.out.printf("Arquivo original: %d bytes%n", original.length());
            System.out.printf("Arquivo comprimido: %d bytes%n", compressed.length());
            System.out.printf("Taxa de compressão: %.2f%%%n", 
                (1 - (double)compressed.length()/original.length()) * 100);
            
            // Descomprime o arquivo
            decompress(filename + ".lzw", filename + ".decoded");
            
            // Verifica integridade
            File decompressed = new File(filename + ".decoded");
            if (compareFiles(original, decompressed)) {
                System.out.println("Verificação OK: arquivo descomprimido é idêntico ao original");
            } else {
                System.out.println("ERRO: arquivo descomprimido não corresponde ao original!");
            }
            
        } catch (IOException e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static class ByteSequence {
        private final byte[] bytes;
        
        public ByteSequence(byte[] bytes) {
            this.bytes = bytes.clone();
        }
        
        public ByteSequence(byte b) {
            this.bytes = new byte[]{b};
        }
        
        public ByteSequence append(byte b) {
            byte[] newBytes = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
            newBytes[bytes.length] = b;
            return new ByteSequence(newBytes);
        }
        
        public byte[] getBytes() {
            return bytes.clone();
        }
        
        public byte firstByte() {
            return bytes[0];
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteSequence that = (ByteSequence) o;
            return Arrays.equals(bytes, that.bytes);
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(bytes);
        }
    }
    
    private static class BitOutputStream {
        private OutputStream out;
        private int buffer;
        private int bitsInBuffer;
        
        public BitOutputStream(OutputStream out) {
            this.out = out;
            buffer = 0;
            bitsInBuffer = 0;
        }
        
        public void writeBits(int code, int numBits) throws IOException {
            buffer = (buffer << numBits) | code;
            bitsInBuffer += numBits;
            
            while (bitsInBuffer >= 8) {
                out.write((buffer >> (bitsInBuffer - 8)) & 0xFF);
                bitsInBuffer -= 8;
            }
        }
        
        public void close() throws IOException {
            if (bitsInBuffer > 0) {
                out.write((buffer << (8 - bitsInBuffer)) & 0xFF);
            }
            out.close();
        }
    }
    
    private static class BitInputStream {
        private InputStream in;
        private int buffer;
        private int bitsInBuffer;
        
        public BitInputStream(InputStream in) {
            this.in = in;
            buffer = 0;
            bitsInBuffer = 0;
        }
        
        public int readBits(int numBits) throws IOException {
            while (bitsInBuffer < numBits) {
                int nextByte = in.read();
                if (nextByte == -1) {
                    throw new EOFException();
                }
                buffer = (buffer << 8) | nextByte;
                bitsInBuffer += 8;
            }
            
            int result = (buffer >> (bitsInBuffer - numBits)) & ((1 << numBits) - 1);
            bitsInBuffer -= numBits;
            buffer &= (1 << bitsInBuffer) - 1;
            return result;
        }
        
        public void close() throws IOException {
            in.close();
        }
    }
    
    public static void compress(String inputFile, String outputFile) throws IOException {
        Map<ByteSequence, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(new ByteSequence((byte)i), i);
        }
        
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(inputFile));
        BitOutputStream output = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        
        try {
            ByteSequence w = null;
            int dictSize = 256;
            int currentByte;
            
            while ((currentByte = input.read()) != -1) {
                byte b = (byte)currentByte;
                ByteSequence wc = (w == null) ? new ByteSequence(b) : w.append(b);
                
                if (dictionary.containsKey(wc)) {
                    w = wc;
                } else {
                    output.writeBits(dictionary.get(w), BITS_PER_CODE);
                    
                    if (dictSize < MAX_DICT_SIZE) {
                        dictionary.put(wc, dictSize++);
                    }
                    w = new ByteSequence(b);
                }
            }
            
            if (w != null) {
                output.writeBits(dictionary.get(w), BITS_PER_CODE);
            }
        } finally {
            input.close();
            output.close();
        }
    }
    
    public static void decompress(String inputFile, String outputFile) throws IOException {
        Map<Integer, ByteSequence> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, new ByteSequence((byte)i));
        }
        
        BitInputStream input = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
        
        try {
            int dictSize = 256;
            int oldCode = input.readBits(BITS_PER_CODE);
            ByteSequence entry = dictionary.get(oldCode);
            output.write(entry.getBytes());
            
            while (true) {
                int newCode;
                try {
                    newCode = input.readBits(BITS_PER_CODE);
                } catch (EOFException e) {
                    break;
                }
                
                ByteSequence sequence;
                if (dictionary.containsKey(newCode)) {
                    sequence = dictionary.get(newCode);
                } else if (newCode == dictSize) {
                    sequence = entry.append(entry.firstByte());
                } else {
                    throw new IllegalStateException("Arquivo corrompido");
                }
                
                output.write(sequence.getBytes());
                
                if (dictSize < MAX_DICT_SIZE) {
                    dictionary.put(dictSize++, entry.append(sequence.firstByte()));
                }
                
                entry = sequence;
            }
        } finally {
            input.close();
            output.close();
        }
    }
    
    private static boolean compareFiles(File file1, File file2) throws IOException {
        if (file1.length() != file2.length()) {
            return false;
        }
        
        try (InputStream is1 = new BufferedInputStream(new FileInputStream(file1));
             InputStream is2 = new BufferedInputStream(new FileInputStream(file2))) {
            
            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];
            int bytesRead1;
            
            while ((bytesRead1 = is1.read(buffer1)) != -1) {
                int bytesRead2 = is2.read(buffer2);
                if (bytesRead1 != bytesRead2) {
                    return false;
                }
                for (int i = 0; i < bytesRead1; i++) {
                    if (buffer1[i] != buffer2[i]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
