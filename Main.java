import java.util.Scanner;

public class Main {

   public static void main(String[] args){
      Scanner scanner = new Scanner(System.in);
      CRUD crud = new CRUD();
      boolean running = true;
      
      try{
         while (running) {
            // Exibe o menu
            System.out.println("Menu:");
            System.out.println("1. Criar");
            System.out.println("2. Ler");
            System.out.println("3. Atualizar");
            System.out.println("4. Deletar");
            System.out.println("5. Carregar");
            System.out.println("6. Ordenar");
            System.out.println("7. Sair");

            // Obtém a escolha do usuário
            System.out.print("Escolha uma opção: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // Executa o método apropriado com base na escolha do usuário
            switch (choice) {
                case 1:
                  crud.create(scanner);
                  break;
                case 2:
                  Planet tmp = new Planet();
                  System.out.print("Insira o ID do planeta desejado: ");
                  tmp = crud.read(scanner.nextInt());
                  System.out.println(tmp);
                  scanner.nextLine();
                  break;
                  case 3:
                  crud.update(scanner);
                  break;
                  case 4:
                  System.out.print("Insira o ID do planeta desejado: ");
                  tmp = crud.delete(scanner.nextInt());
                  scanner.nextLine();
                  break;
                case 5:
                  crud.load();
                  break;
                case 6:
                  //ordenar
                  break;
                case 7:
                  running = false;
                  System.out.println("Encerrando o programa...");
                  break;
                default:
                  System.out.println("Opção inválida. Por favor, escolha entre 1 e 6.");
                  break;
            }
        }
      }catch(Exception e){
         e.printStackTrace();
      }

      scanner.close();
   }
}