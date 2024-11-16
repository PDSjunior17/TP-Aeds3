 /*
 criar uma função que vai receber um planet, pegar o metal ratio do planeta fazer um split
 separando por [] e / e se o elemento não estiver na lista adiciona ele e coloca o 


 */

 import java.io.*;
 import java.nio.charset.StandardCharsets;
 import java.util.*;
 
 
 public class InvertedIndex {
 
     public InvertedIndex(){}
 
     private static final String FILE_PATH1 = "data/indexMetalRatio.db";
 
     public  void processPlanet(Planet planet) throws IOException {
         String[] terms = planet.getMetalRatio();
 
         // Processar cada termo
         for (String term : terms) {
             term = term.trim(); // Remover espaços desnecessários
             if (term.isEmpty()) continue; // Ignorar termos vazios
 
             if (!updateTermInFile(term, planet.getId())) {
                 // Caso o termo não esteja no arquivo, cria uma nova entrada
                 addNewTermToFile(term, planet.getId());
             }
         }
     }
 
     private static boolean updateTermInFile(String term, int planetId) throws IOException {
         RandomAccessFile file = new RandomAccessFile(FILE_PATH1, "rw");
         try {
             file.seek(0); // Voltar ao início do arquivo
     
             // Iterar pelo arquivo para verificar se o termo já existe
             while (file.getFilePointer() < file.length()) {
                 if (file.getFilePointer() + 5 > file.length()) {
                     // Não há espaço suficiente para ler uma lápide, número de IDs e a string
                     break;
                 }
     
                 boolean tombstone = file.readBoolean(); // Lê a lápide
                 int totalIds = file.readInt();          // Lê o total de IDs
                 
                 if (file.getFilePointer() >= file.length()) {
                     // Se não há dados suficientes para ler a string, sair do loop
                     break;
                 }
     
                 String existingTerm = readString(file); // Lê o termo
     
                 // Se o termo já estiver no arquivo
                 if (!tombstone && existingTerm.equals(term)) {
                     // Marcar como "removido" (lápide true)
                     long currentPointer = file.getFilePointer();
                     file.seek(currentPointer - (existingTerm.length() + 5)); // Volta à lápide
                     file.writeBoolean(true); // Marca lápide como true
     
                     // Copiar a entrada para o final do arquivo e adicionar o novo ID
                     file.seek(file.length()); // Move para o final do arquivo
                     file.writeBoolean(false); // Nova lápide (false)
                     file.writeInt(totalIds + 1); // Atualiza o total de IDs
                     writeString(file, term); // Escreve o termo
                     for (int i = 0; i < totalIds; i++) {
                         int existingId = file.readInt(); // Copiar IDs anteriores
                         file.writeInt(existingId);
                     }
                     file.writeInt(planetId); // Adicionar o novo ID
                     return true;
                 } else {
                     // Pular os IDs associados a este termo
                     file.seek(file.getFilePointer() + (4 * totalIds));
                 }
             }
         } catch (EOFException e) {
             
         } finally {
             file.close();
         }
         return false;
     }
     
 
     private static void addNewTermToFile(String term, int planetId) throws IOException {
         RandomAccessFile file = new RandomAccessFile(FILE_PATH1, "rw");
         try {
             file.seek(file.length()); // Ir para o final do arquivo
 
             // Criar uma nova entrada para o termo
             file.writeBoolean(false); // Lápide como false
             file.writeInt(1); // Apenas 1 ID por enquanto
             writeString(file, term); // Escreve o termo
             file.writeInt(planetId); // Escreve o ID do planeta
         } finally {
             file.close();
         }
     }
 
     private static String readString(RandomAccessFile file) throws IOException {
         int length = file.readInt(); // Primeiro, lê o comprimento da string
         byte[] bytes = new byte[length];
         file.readFully(bytes); // Lê os bytes da string
         return new String(bytes, StandardCharsets.UTF_8);
     }
 
     private static void writeString(RandomAccessFile file, String str) throws IOException {
         byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
         file.writeInt(bytes.length); // Escreve o comprimento da string
         file.write(bytes); // Escreve os bytes da string
     }
 
     // Método modificado: leitura de IDs associados a uma palavra específica
     public  void readIndexFile(String searchTerm) throws IOException {
         RandomAccessFile file = new RandomAccessFile(FILE_PATH1, "r");
         boolean found = false;
         try {
             file.seek(0); // Voltar ao início do arquivo
             while (file.getFilePointer() < file.length()) {
                 boolean tombstone = file.readBoolean(); // Lê a lápide
                 int totalIds = file.readInt();          // Lê o total de IDs
                 String term = readString(file);         // Lê o termo
 
                 if (!tombstone && term.equals(searchTerm)) {
                     found = true;
                     System.out.print("Termo: " + term + " | IDs: ");
                     for (int i = 0; i < totalIds; i++) {
                         System.out.print(file.readInt() + " ");
                     }
                     System.out.println();
                     break; // Termo encontrado, sair do loop
                 } else {
                     // Pular os IDs de termos não correspondentes
                     file.seek(file.getFilePointer() + (4 * totalIds));
                 }
             }
 
             if (!found) {
                 System.out.println("Termo '" + searchTerm + "' não encontrado.");
             }
         } finally {
             file.close();
         }
     }
 
     // Método para deletar ocorrências de um ID específico
     public  void deleteIdFromFile(int planetId) throws IOException {
         RandomAccessFile file = new RandomAccessFile(FILE_PATH1, "rw");
         try {
             file.seek(0); // Voltar ao início do arquivo
 
             while (file.getFilePointer() < file.length()) {
                 long currentPosition = file.getFilePointer();
                 boolean tombstone = file.readBoolean(); // Lê a lápide
                 int totalIds = file.readInt();          // Lê o total de IDs
                 String term = readString(file);         // Lê o termo
 
                 if (!tombstone) {
                     List<Integer> ids = new ArrayList<>();
                     boolean found = false;
 
                     // Verificar se o ID está presente
                     for (int i = 0; i < totalIds; i++) {
                         int existingId = file.readInt();
                         if (existingId == planetId) {
                             found = true;
                         } else {
                             ids.add(existingId);
                         }
                     }
 
                     // Se o ID foi encontrado, atualiza ou remove o termo
                     if (found) {
                         // Marcar a lápide como true
                         file.seek(currentPosition);
                         file.writeBoolean(true);
 
                         if (!ids.isEmpty()) {
                             // Se ainda existem IDs, copiar para o final do arquivo
                             file.seek(file.length());
                             file.writeBoolean(false); // Nova lápide
                             file.writeInt(ids.size()); // Atualiza o número de IDs
                             writeString(file, term); // Escreve o termo
                             for (int id : ids) {
                                 file.writeInt(id);
                             }
                         }
                     }
                 } else {
                     // Pular os IDs de termos removidos
                     file.seek(file.getFilePointer() + (4 * totalIds));
                 }
             }
         } catch (IOException e) {
             System.err.println("Erro ao deletar ID: " + e.getMessage());
         } finally {
             file.close();
         }
     }
 
 }