import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Scanner;
import java.io.RandomAccessFile;
import java.io.File;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


public class Main {
    private static String DATASET = "planets-dataset.csv";
    private static String FileUrl = "https://raw.githubusercontent.com/PDSjunior17/TP-Aeds3/main/data/planets-dataset.csv";

    public static long dateToSeconds(String date) {
    if(date == null){return 0L;}
    LocalDate localDate = LocalDate.parse(date);
    
    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    
    long secondsSinceEpoch = instant.getEpochSecond();
    
    return secondsSinceEpoch*1000;
    }

    private static String FILE_NAME = "planets.db";

    //Método que cria um novo registro no arquivo binário
    public static void create(Planet planet)throws Exception{
        File file = new File(FILE_NAME);
        boolean exists = file.exists();
        
        DataOutputStream dos;
        FileOutputStream arq;
        byte[] ba;

        if(exists){
            FileInputStream arq2;
            DataInputStream dis;
            
            int ultimoID;
            arq2 = new FileInputStream(FILE_NAME);
            dis = new DataInputStream(arq2);
            ultimoID = dis.readInt();
            arq2.close();
            RandomAccessFile raf = new RandomAccessFile(FILE_NAME,"rw");
            raf.seek(0);
            raf.writeInt(++ultimoID);
            
            arq = new FileOutputStream(FILE_NAME,true);
            planet.setId(ultimoID);
            ba = planet.toByteArray();
            dos = new DataOutputStream(arq);
            int tamRegistro = ba.length;
            tamRegistro -= 2;
            dos.writeInt(tamRegistro);
            dos.write(ba);

            arq.close();
            dos.close();
            dis.close();
            raf.close();

        }else{
            try{
                arq = new FileOutputStream(FILE_NAME);
                
                planet.setId(1);
                ba = planet.toByteArray();
                dos = new DataOutputStream(arq);
                //escrevendo o último id
                dos.writeInt(1);

                int tamRegistro = ba.length;
                tamRegistro -= 2;
                dos.writeInt(tamRegistro);
                dos.write(ba);
    
                arq.close();
                dos.close();
             }catch(Exception e){
                e.printStackTrace();
             }

        }
    }

    public static Planet read(int id)throws Exception{
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
        char lapide;
        int ultimoID = raf.readInt();
        if(id > ultimoID){
            raf.close();
            return null;
        }
        else{
            byte[] ba;
            int tam;
            Planet tmp = new Planet();
            while(raf.getFilePointer() < raf.length()){
                tam = raf.readInt();
                lapide = raf.readChar();// tamanho - lapide
                
                ba = new byte[tam];
                raf.read(ba);
                tmp.fromByteArray(ba);
                if(lapide != '!'){
                    if(tmp.getId() == id){
                        raf.close();
                        return tmp;
                    }
                }
            }
            raf.close();
            return null;
        }

    }

    public static void load()throws Exception{
        try {
            // Tentar ler do arquivo no GitHub
            loadFileGithub();
        } catch (IOException e) {
            // Captura qualquer erro ao tentar ler do GitHub
            System.err.println("Erro ao ler do GitHub: " + e.getMessage());
            System.err.println("Tentando ler do arquivo local...");
            try {
                // Tenta ler do arquivo local como fallback
                loadFileLocal();
            } catch (IOException ex) {
                // Captura qualquer erro ao tentar ler do arquivo local
                System.err.println("Erro ao ler do arquivo local: " + ex.getMessage());
            }
        }
    }

    private static void  loadFileGithub()throws IOException, Exception{
        try {
            // Criar URI e então URL a partir do URI
            URI uri = new URI(FileUrl);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String linha;
                //pulando três linhas do dataset que não são informações relevantes
                linha = in.readLine();
                linha = in.readLine();
                linha = in.readLine();
                
                String[] dados;
                while((linha = in.readLine()) != null){
                    //separando os dados do planeta
                    dados = linha.split(",");
                    
                    //instanciando um planeta temporario para gravar no arquivo e tratando as exceções
                    Planet tmp = new Planet((dados[0].compareTo("*") == 0) ? null : Integer.parseInt(dados[0]),
                    (dados[1].compareTo("*") == 0) ? null : dados[1],
                    (dados[2].compareTo("*") == 0) ? null :dados[2],
                    (dados[3].compareTo("*") == 0) ? null : Integer.parseInt(dados[3].replace(".", "")),
                    (dados[4].compareTo("*") == 0) ? null :Integer.parseInt(dados[4].replace(".", "")),
                    (dados[5].compareTo("*") == 0) ? null : dados[5],
                    (dados[6].compareTo("*") == 0) ? null : Integer.parseInt(dados[6].replace(".", "")),
                    (dados[7].compareTo("*") == 0) ? null : dados[7],
                    (dados[8].compareTo("*") == 0) ? null : Integer.parseInt(dados[8]) == 1,
                    (dados[9].compareTo("*") == 0) ? null : Long.parseLong(dados[9].replace(".", "")),
                    (dados[10].compareTo("*") == 0) ? null : Double.parseDouble(dados[10].replace(".", "")),
                    (dados[11].compareTo("*") == 0) ? null : dados[11],
                    (dados[12].compareTo("*") == 0) ? null : dateToSeconds(dados[12]));

                    //criando o registro
                    create(tmp);
                }
                in.close();
            }
        } catch (URISyntaxException e) {
            // Lançar IOException se a URL for inválida
            throw new IOException("URL fornecida é inválida: " + FileUrl, e);
        }
    }

    //Método que carrega os dados da base selecionada
    private static void loadFileLocal()throws Exception{

        //obtendo os dados do dataset que está no github


        //lendo do dataset que esta no hd
        try(BufferedReader br = new BufferedReader(new FileReader(DATASET))){
            String linha;
            //pulando três linhas do dataset que não são informações relevantes
            linha = br.readLine();
            linha = br.readLine();
            linha = br.readLine();
            
            String[] dados;
            while((linha = br.readLine()) != null){
                //separando os dados do planeta
                dados = linha.split(",");
                
                //instanciando um planeta temporario para gravar no arquivo e tratando as exceções
                Planet tmp = new Planet((dados[0].compareTo("*") == 0) ? null : Integer.parseInt(dados[0]),
                (dados[1].compareTo("*") == 0) ? null : dados[1],
                (dados[2].compareTo("*") == 0) ? null :dados[2],
                (dados[3].compareTo("*") == 0) ? null : Integer.parseInt(dados[3].replace(".", "")),
                (dados[4].compareTo("*") == 0) ? null :Integer.parseInt(dados[4].replace(".", "")),
                (dados[5].compareTo("*") == 0) ? null : dados[5],
                (dados[6].compareTo("*") == 0) ? null : Integer.parseInt(dados[6].replace(".", "")),
                (dados[7].compareTo("*") == 0) ? null : dados[7],
                (dados[8].compareTo("*") == 0) ? null : Integer.parseInt(dados[8]) == 1,
                (dados[9].compareTo("*") == 0) ? null : Long.parseLong(dados[9].replace(".", "")),
                (dados[10].compareTo("*") == 0) ? null : Double.parseDouble(dados[10].replace(".", "")),
                (dados[11].compareTo("*") == 0) ? null : dados[11],
                (dados[12].compareTo("*") == 0) ? null : dateToSeconds(dados[12]));

                //criando o registro
                create(tmp);
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Planet delete(int id)throws Exception, IOException{
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw");
        long posLapide;
        int ultimoID = raf.readInt();
        if(id > ultimoID){
            raf.close();
            return null;
        }
        else{
            char lapide;
            byte[] ba;
            int tam;
            Planet tmp = new Planet();
            while(raf.getFilePointer() < raf.length()){
                tam = raf.readInt();
                posLapide = raf.getFilePointer();
                lapide = raf.readChar();// tamanho - lapide
                
                ba = new byte[tam];
                raf.read(ba);
                tmp.fromByteArray(ba);
                if(tmp.getId() == id){
                    raf.seek(posLapide);
                    lapide = '!';
                    raf.writeChar(lapide);
                    raf.close();
                    return tmp;
                }
            }
            raf.close();
            return null;
        }
    }
    public static void main(String[] args){
        // Planet pla = new Planet(2,  "11 Com b", "11 Com",
        // 2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]",
        //dateToSeconds("2014-05-14"));
      
         try{
            //arq = new FileOutputStream("data/planets.db");
         //    ba = pla.toByteArray();
         //    dos = new DataOutputStream(arq);

         //    dos.writeInt(ba.length);
         //    dos.write(ba);

         //    arq.close();
         //    dos.close();

            // Planet test = new Planet();
            // int tam;
            //create(pla);
            // FileInputStream arq2;
            // DataInputStream dis;
            // int tam;
            // byte[] ba;
            // arq2 = new FileInputStream(FILE_NAME);
            // dis = new DataInputStream(arq2);
            // int ultimoID = dis.readInt();

            //  tam = dis.readInt();
            //  ba = new byte[tam];
            //  System.out.println("Lápide: " + dis.readChar());

            // dis.read(ba);
            // test.fromByteArray(ba);
            Scanner ler = new Scanner(System.in);
            System.out.print("Digite o ID: ");
            int id = ler.nextInt();
            
            Planet test = read(id);
            
            System.out.println(test);

            //removendo
            System.out.print("Digite o ID que deseja remover: ");
            id = ler.nextInt();
            ler.close();
            test = delete(id);
            
            System.out.println(test);
            //load();
         
         }catch(Exception e){
            e.printStackTrace();
         }

    }

}
