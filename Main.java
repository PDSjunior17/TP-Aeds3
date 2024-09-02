import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Main {
    public static long dateToSeconds(String date) {
    LocalDate localDate = LocalDate.parse(date);
    
    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    
    long secondsSinceEpoch = instant.getEpochSecond();
    
    return secondsSinceEpoch*1000;
    }
    public static void main(String[] args){
         Planet pla = new Planet(2, dateToSeconds("2014-05-14"), "11 Com b", "11 Com",
         2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]");
        
         FileOutputStream arq;
         FileInputStream arq2;
         DataOutputStream dos;
         DataInputStream dis;
         byte[] ba;

         try{
            arq = new FileOutputStream("data/planets.db");
            ba = pla.toByteArray();
            dos = new DataOutputStream(arq);

            dos.writeInt(ba.length);
            dos.write(ba);

            arq.close();
            dos.close();

            Planet test = new Planet();
            int tam;

            arq2 = new FileInputStream("data/planets.db");
            dis = new DataInputStream(arq2);

            tam = dis.readInt();
            ba = new byte[tam];
            System.out.println("Lápide: " + dis.readChar());

            dis.read(ba);
            test.fromByteArray(ba);
            System.out.println(test);
         }catch(Exception e){
            e.printStackTrace();
         }

    }

}
