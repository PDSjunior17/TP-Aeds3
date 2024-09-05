import java.io.File;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CRUD {//modificar linha 966 , 4953 a 4957, 4992, 4991 a 4996, 36162 do dataset
    private static String FILE_NAME = "planets.db";
    private static String DATASET = "planets-dataset.csv";

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
    public void update(Scanner entrada){
        
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
}
