import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.io.RandomAccessFile;
import java.io.File;

public class Main {
    public static long dateToSeconds(String date) {
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
            ba = planet.toByteArray();
            dos = new DataOutputStream(arq);
            planet.setId(ultimoID);
            dos.writeInt(ba.length);
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

                dos.writeInt(ba.length);
                dos.write(ba);
    
                arq.close();
                dos.close();
             }catch(Exception e){
                e.printStackTrace();
             }

        }
    }

    public static Planet read(int id)throws Exception{
        FileInputStream arq2;
        DataInputStream dis;
        InputStream is = new FileInputStream(FILE_NAME);
        int byteRead;
        arq2 = new FileInputStream(FILE_NAME);
        dis = new DataInputStream(arq2);
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
        //int ultimoID = raf.readInt();
        int ultimoID = dis.readInt();
        char lapide;
        if(id > ultimoID){
            return null;
            //raf.close()
        }
        else{
             
            byte[] ba;
            int tam;
            Planet tmp = new Planet();
            while((byteRead = is.read()) != -1){
                 tam = dis.readInt();
                // tam = raf.readInt();
                ba = new byte[tam];
                 lapide = dis.readChar();
                // lapide = raf.readChar();
                // dis.read(ba);
                // raf.read(ba);
                tmp.fromByteArray(ba);
                if(lapide != '!'){
                    if(tmp.getId() == id){
                         dis.close();
                         arq2.close();
                        raf.close();
                        return tmp;
                    }
                }
            }
             dis.close();
             arq2.close();
            raf.close();
            is.close();
            return null;
        }

    }
    public static void main(String[] args){
         Planet pla = new Planet(2, dateToSeconds("2014-05-14"), "11 Com b", "11 Com",
         2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]");
        
         // FileOutputStream arq;
         FileInputStream arq2;
         //DataOutputStream dos;
         DataInputStream dis;
         byte[] ba;

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
            // create(pla);
            // arq2 = new FileInputStream(FILE_NAME);
            // dis = new DataInputStream(arq2);
            // int ultimoID = dis.readInt();

            // tam = dis.readInt();
            // ba = new byte[tam];
            // System.out.println("Lápide: " + dis.readChar());

            // dis.read(ba);
            // test.fromByteArray(ba);
            Planet test = read(2);
            System.out.println(test);
         
         }catch(Exception e){
            e.printStackTrace();
         }

    }

}
