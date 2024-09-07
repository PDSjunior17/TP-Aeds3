import java.util.Scanner;

public class Main {


   public static void main(String[] args){
      Scanner entrada = new Scanner(System.in);
      CRUD crud = new CRUD();
      Planet pla = new Planet(2,  "11 Com b", "11 Com",
         2, 1, "Radial Velocity", 2007, "Xinglong Station", false, 47670000, 4742.00, "[Fe/H]",
         "2014-05-14");
      
      try{
         // crud.create(pla);
         // pla = crud.read(1);
         // System.out.println(pla);

         // crud.update(entrada);

         pla = crud.read(1);
         System.out.println(pla);
      }catch(Exception e){
         e.printStackTrace();
      }
   }
}