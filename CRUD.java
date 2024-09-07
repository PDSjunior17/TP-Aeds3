import java.util.Scanner;
import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public class CRUD {//modificar linha 966 , 4953 a 4957, 4992, 4991 a 4996, 36162 do dataset
    private static String FILE_NAME = "data/planets.db";
    private static String DATASET = "data/planets-dataset.csv";
    private static String FileUrl = "https://raw.githubusercontent.com/PDSjunior17/TP-Aeds3/main/data/planets-dataset.csv";

    public static long dateToSeconds(String date) {
        if(date == null){return 0L;}
        LocalDate localDate = LocalDate.parse(date);
        
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        
        long secondsSinceEpoch = instant.getEpochSecond();
        
        return secondsSinceEpoch*1000;
    }

    //Método que cria um novo registro no arquivo binário
    public void create(Planet planet)throws Exception{
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
    
    //Método que recebe um ID e lê um registro no arquivo binário
    public Planet read(int id)throws Exception{//quando for imprimir na main tratar a exceção do -1 e do null e o da flag
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
    
    //Método que atualiza as informações de um registro no arquivo binário
    public void update(Scanner entrada)throws Exception{
        Planet updatedPlanet = new Planet();
        System.out.print("\nInsira o ID do planeta a ser atualizado: ");
        updatedPlanet.setId(entrada.nextInt());
        entrada.nextLine();
        System.out.print("\nNovo nome: ");
        updatedPlanet.setName(entrada.nextLine());
        System.out.print("\nNova data: ");
        updatedPlanet.setDataRelase(entrada.nextLine());
        System.out.print("\nNova estrela hospedeira: ");
        updatedPlanet.setHost(entrada.nextLine());
        System.out.print("\nNovo número de estrelas: ");
        updatedPlanet.setNumStars(entrada.nextInt());
        entrada.nextLine();
        System.out.print("\nNovo número de planetas: ");
        updatedPlanet.setNumPlanets(entrada.nextInt());
        entrada.nextLine();
        System.out.print("\nNovo método de descoberta: ");
        updatedPlanet.setDiscoveryMethod(entrada.nextLine());
        System.out.print("\nNovo de ano de descoberta: ");
        updatedPlanet.setDiscoveryYear(entrada.nextInt());
        entrada.nextLine();
        System.out.print("\nNovo local de descoberta: ");
        updatedPlanet.setDiscoveryFacility(entrada.nextLine());
        System.out.print("\nNova flag de controvérsia: ");
        updatedPlanet.setControv(entrada.nextBoolean());
        entrada.nextLine();
        System.out.print("\nNova massa estelar: ");
        updatedPlanet.setMass(entrada.nextLong());
        entrada.nextLine();
        System.out.print("\nNova temperatura estelar: ");
        updatedPlanet.setstarTemperature(entrada.nextDouble());
        entrada.nextLine();

        System.out.print("\nInsira as duas próximas linhas para a proporção metálica: ");
        String[] metalRatioLines = new String[2];
        metalRatioLines[0] = entrada.nextLine();
        metalRatioLines[1] = entrada.nextLine();
    
        updatedPlanet.setMetalRatio(metalRatioLines);
        System.out.println("monto certim o planeta");

        update(updatedPlanet);
    }

    public void update(Planet updatedPlanet) throws Exception {
        System.out.println(updatedPlanet);
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw");
        int lastId = raf.readInt();  //Lê o último ID
    
        // Se o ID de updatedPlanet for maior que o último ID, ele não existe
        if (updatedPlanet.getId() > lastId) {
            raf.close();
            throw new Exception("Record with ID " + updatedPlanet.getId() + " not found.");
        }
    
        byte[] newRecordData = updatedPlanet.toByteArray();
        int newRecordSize = newRecordData.length - 2; // exclui o tamanho da lapide
        boolean recordFound = false;
    
        while (raf.getFilePointer() < raf.length()) {
            long recordPosition = raf.getFilePointer();
            int recordSize = raf.readInt(); //Lê o tam do registro antigo
            char lapide = raf.readChar();  // Lê 'lapide'
    
            byte[] oldRecordData = new byte[recordSize];
            raf.read(oldRecordData);  // Lê os dados do registro antigo
    
            Planet existingPlanet = new Planet();
            existingPlanet.fromByteArray(oldRecordData);
    
            if (lapide == '+' && existingPlanet.getId() == updatedPlanet.getId()) {
                recordFound = true;
                // Marca a 'lapide' do registro antigo como '!'
                raf.seek(recordPosition + 4);
                raf.writeChar('!');
    
                // Concatena o novo registro no final do arquivo
                raf.seek(raf.length());
                raf.writeInt(newRecordSize);
                raf.write(newRecordData);
    
                raf.close();
                return;
            }
        }
    
        raf.close();
        throw new Exception("Record with ID " + updatedPlanet.getId() + " not found.");
    }
    
    //Método que marca como lápide um registro no arquivo binário e retorna ele como objeto
    public Planet delete(int id)throws Exception{
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

    //Método que carrega os dados da base selecionada
    public void load()throws Exception{
        try {
            File file = new File(FILE_NAME);
            if(file.exists()){file.delete();}
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

    //função que lê os dados do dataset que estão em um repositorio no github
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
                    CRUD cr = new CRUD();
                    cr.create(tmp);
                }
                in.close();
            }
        } catch (URISyntaxException e) {
            // Lançar IOException se a URL for inválida
            throw new IOException("URL fornecida é inválida: " + FileUrl, e);
        }
    }

     //Método que carrega os dados do dataset que estão no arquivo local
     private static void loadFileLocal()throws Exception{

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
                CRUD cr = new CRUD();
                cr.create(tmp);
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
