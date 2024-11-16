import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class Hash {

    private static String DIR = "dataHash/diretorio";
    private static int maxBucket;
    private static int numUltimoBucket;

    public Hash() {
        numUltimoBucket = 0;
        maxBucket = 1819;
    }

    // Método para criar um novo bucket
    private static String newBucket() throws IOException {
        // Cria o diretório "dataHash" se ele não existir
        File directory = new File("dataHash");
        if (!directory.exists()) {
            directory.mkdir();
        }
    
        // Define o caminho completo para o bucket dentro do diretório "dataHash"
        String name = "dataHash/bucket" + numUltimoBucket + ".db";
        numUltimoBucket++;
        
        // Cria o arquivo do bucket
        RandomAccessFile arquivo = new RandomAccessFile(name, "rw");
    
        // Inicializa a profundidade local do bucket
        arquivo.writeInt(2);
    
        // Inicializa a quantidade de elementos no bucket como 0
        arquivo.writeInt(0);
    
        // Fecha o arquivo
        arquivo.close();
    
        return name;
    }
    

    // Método para escrever uma string em um arquivo
    public static void escreverString(RandomAccessFile raf, String texto) throws IOException {
        byte[] bytes = texto.getBytes("UTF-8");
        raf.writeInt(bytes.length);
        raf.write(bytes);
    }

    // Função que retorna o endereço do bucket de um determinado id, com base na profundidade global
    private static String hashBucket(int id) throws IOException {
        RandomAccessFile rafDir = new RandomAccessFile(DIR, "rw");
        int p = rafDir.readInt();
        int potencia = (int) Math.pow(2, p);
        int h = id % potencia;
        int comprimento;

        // Pulando os endereços dos buckets menores que o hash
        for (int i = 0; i < h; i++) {
            comprimento = rafDir.readInt();
            rafDir.skipBytes(comprimento);
        }

        // Lendo o endereço do bucket do hash
        comprimento = rafDir.readInt();
        byte[] bytes = new byte[comprimento];
        rafDir.readFully(bytes);
        String bucket = new String(bytes, "UTF-8");
        rafDir.close();
        return bucket;
    }

    // Método que cria um novo elemento no arquivo de índice hash
    public void create(int id, long endereco) throws IOException {
        RandomAccessFile rafDir;
        File dir = new File(DIR);

        // Se o diretório não existe, cria o arquivo com profundidade inicial de 2 e 4 buckets
        if (!dir.exists()) {
            rafDir = new RandomAccessFile(DIR, "rw");
            rafDir.writeInt(2); // Profundidade global
            for (int i = 0; i < 4; i++) {
                escreverString(rafDir, newBucket());
            }
            rafDir.close();
        }

        // Abrir diretório e bucket correspondente
        rafDir = new RandomAccessFile(DIR, "rw");
        rafDir.seek(0);
        int pGlobal = rafDir.readInt();
        String bucket = hashBucket(id);
        RandomAccessFile rafBuck = new RandomAccessFile(bucket, "rw");

        int profundidadeLocal = rafBuck.readInt();
        int numIds = rafBuck.readInt();

        // Verifica se há espaço no bucket
        if (numIds < maxBucket) {
            // Inserindo novo id e endereço
            rafBuck.seek(rafBuck.length());
            rafBuck.writeInt(id);
            rafBuck.writeLong(endereco);
            rafBuck.seek(4);
            rafBuck.writeInt(numIds + 1); // Atualiza número de elementos no bucket
        } else if (profundidadeLocal < pGlobal) {
            // Caso de dividir bucket
            dividirBucket(id, endereco, rafBuck, profundidadeLocal, numIds, pGlobal);
        } else {
            // Expandir diretório e dividir bucket
            expandirDiretorio(id, endereco, profundidadeLocal, pGlobal);
        }

        rafBuck.close();
        rafDir.close();
    }

    // Método para dividir o bucket quando a profundidade local é menor que a global
    private void dividirBucket(int id, long endereco, RandomAccessFile rafBuck, int profundidadeLocal, int numIds, int pGlobal) throws IOException {
        // Atualiza a profundidade local
        rafBuck.seek(0);
        rafBuck.writeInt(profundidadeLocal + 1);
    
        // Criar um buffer para armazenar os IDs e endereços que permanecerão neste bucket
        int[] idsParaPermanecer = new int[maxBucket];
        long[] enderecosParaPermanecer = new long[maxBucket];
        int contadorPermanecer = 0;
    
        // Redistribuir os IDs
        rafBuck.seek(8); // Pula profundidade local e número de IDs
        for (int i = 0; i < numIds; i++) {
            int idTmp = rafBuck.readInt();
            long endTmp = rafBuck.readLong();
            if (hashBucket(idTmp).equals(hashBucket(id))) {
                idsParaPermanecer[contadorPermanecer] = idTmp;
                enderecosParaPermanecer[contadorPermanecer] = endTmp;
                contadorPermanecer++;
            } else {
                create(idTmp, endTmp); // Reinsere o ID no hash
            }
        }
    
        // Reescrever os IDs que permanecerão neste bucket
        rafBuck.seek(8); // Volta para o início dos dados
        for (int i = 0; i < contadorPermanecer; i++) {
            rafBuck.writeInt(idsParaPermanecer[i]);
            rafBuck.writeLong(enderecosParaPermanecer[i]);
        }
    
        // Inserir o novo ID
        rafBuck.writeInt(id);
        rafBuck.writeLong(endereco);
    
        // Atualizar o número de IDs no bucket
        rafBuck.seek(4);
        rafBuck.writeInt(contadorPermanecer + 1);
    
        // Truncar o arquivo se necessário
        rafBuck.setLength(8 + (contadorPermanecer + 1) * 12); // 8 bytes de cabeçalho + (número de IDs * 12 bytes por entrada)
    }

    // Método para expandir o diretório e aumentar a profundidade global
    private void expandirDiretorio(int id, long endereco, int profundidadeLocal, int pGlobal) throws IOException {
        RandomAccessFile rafDir = new RandomAccessFile(DIR, "rw");

        // Aumenta a profundidade global
        pGlobal++;
        rafDir.seek(0);
        rafDir.writeInt(pGlobal);

        // Duplica os buckets existentes
        int numBuckets = (int) Math.pow(2, pGlobal);
        for (int i = 0; i < numBuckets; i++) {
            escreverString(rafDir, newBucket());
        }

        rafDir.close();
        create(id, endereco); // Reinsere o ID
    }

    // Método para deletar um id do hash
    public void delete(int id) throws IOException {
        String bucket = hashBucket(id);
        RandomAccessFile rafBuck = new RandomAccessFile(bucket, "rw");
    
        int profundidadeLocal = rafBuck.readInt();
        int numIds = rafBuck.readInt();
        rafBuck.seek(8);
    
        boolean encontrado = false;
        for (int i = 0; i < numIds; i++) {
            long posicao = rafBuck.getFilePointer();
            int idTmp = rafBuck.readInt();
            long endTmp = rafBuck.readLong();
            if (idTmp == id) {
                // Encontrou o ID, removendo-o
                rafBuck.seek(posicao);
                rafBuck.writeInt(-1); // Marca o ID como removido
                encontrado = true;
                break;
            }
        }
    
        if (encontrado) {
            // Atualiza o número de IDs no bucket
            rafBuck.seek(4);
            rafBuck.writeInt(numIds - 1);
        }
    
        rafBuck.close();
    
        if (!encontrado) {
            System.out.println("ID " + id + " não encontrado no bucket.");
        }
    }

    //metodo que atualiza um registro do bucket 
    public void update(int id, long novoEndereco) throws IOException {
        String bucket = hashBucket(id);
        RandomAccessFile rafBuck = new RandomAccessFile(bucket, "rw");
    
        int profundidadeLocal = rafBuck.readInt();
        int numIds = rafBuck.readInt();
        rafBuck.seek(8);
    
        boolean encontrado = false;
        for (int i = 0; i < numIds; i++) {
            long posicao = rafBuck.getFilePointer();
            int idTmp = rafBuck.readInt();
            if (idTmp == id) {
                // Encontrou o ID, atualizando o endereço
                rafBuck.seek(posicao + 4); // Pula o ID para chegar ao endereço
                rafBuck.writeLong(novoEndereco);
                encontrado = true;
                break;
            }
            rafBuck.skipBytes(8); // Pula o endereço para o próximo par ID-endereço
        }
    
        rafBuck.close();
    
        if (!encontrado) {
            System.out.println("ID " + id + " não encontrado. Não foi possível atualizar.");
        }
    }

    //metodo que procura um registro na hash
    public long read(int id) throws IOException {
        String bucket = hashBucket(id);
        RandomAccessFile rafBuck = new RandomAccessFile(bucket, "r");
    
        int profundidadeLocal = rafBuck.readInt();
        int numIds = rafBuck.readInt();
        rafBuck.seek(8);
    
        long endereco = -1; // Valor padrão se o ID não for encontrado
        for (int i = 0; i < numIds; i++) {
            int idTmp = rafBuck.readInt();
            long endTmp = rafBuck.readLong();
            if (idTmp == id) {
                endereco = endTmp;
                break;
            }
        }
    
        rafBuck.close();
    
        if (endereco == -1) {
            System.out.println("ID " + id + " não encontrado.");
        }
    
        return endereco;
    }
    
}
