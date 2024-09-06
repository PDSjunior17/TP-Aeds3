import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;



public class Main {
    public static long dateToSeconds(String date) {
    if(date == null){return 0L;}
    LocalDate localDate = LocalDate.parse(date);
    
    Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    
    long secondsSinceEpoch = instant.getEpochSecond();
    
    return secondsSinceEpoch*1000;
    }
   

    public static void main(String[] args){
         Planet pla = new Planet(2,  "11 Com b", "11 Com",
         2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]",
        dateToSeconds("2014-05-14"));
      
         try{
            System.out.println(pla);
         
         }catch(Exception e){
            e.printStackTrace();
         }

    }

}
