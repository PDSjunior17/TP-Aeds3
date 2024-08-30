import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Main2 {
    public static long dateToSeconds(String date) {
    LocalDate localDate = LocalDate.parse(date);
    
    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    
    long secondsSinceEpoch = instant.getEpochSecond();
    
    return secondsSinceEpoch*1000;
    }
    public static void main(String[] args){
        Planetas pla = new Planetas(2, dateToSeconds("2014-05-14"), "11 Com b", "11 Com",
         2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]");
        
         FileOutputStream arq;
         DataOutputStream dos;

         try{
            byte[] ba;
            arq = new FileOutputStream("dados/planetas.db");
            ba = pla.toByteArray();
            dos = new DataOutputStream(arq);

            dos.writeInt(ba.length);
            dos.write(ba);

            arq.close();
            dos.close();
         }catch(Exception e){
            e.printStackTrace();
         }

    }

}
