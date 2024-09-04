import java.io.File;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class CRUD {
    private static String FILE_NAME = "planets.db";
    private static String FILE_PLANETS = "planets-dataset.csv";
    //Método que cria um novo registro no arquivo binário
    public void create(Planet planet)throws Exception{//finalizado
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
    
    //Método que recebe um ID e lê um registro no arquivo binário
    public Planet read(int id)throws Exception{
        RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "r");
        int ultimoID = raf.readInt();
        char lapide;
        if(id > ultimoID){
            raf.close();
            return null;
        }
        else{
            FileInputStream arq2;
            DataInputStream dis;
            byte[] ba;
            int tam;
            arq2 = new FileInputStream(FILE_NAME);
            dis = new DataInputStream(arq2);
            Planet tmp = new Planet();
            while(raf.getFilePointer() < raf.length()){
                tam = dis.readInt();
                ba = new byte[tam];
                lapide = dis.readChar();
                dis.read(ba);
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
            return null;
        }

    }
    
    //Método que atualiza as informações de um registro no arquivo binário
    public void update(Scanner entrada){
        
    }
    
    //Método que marca como lápide um registro no arquivo binário e retorna ele como objeto
    public Planet delete(int id){

    }

    //Método que carrega os dados da base selecionada
    public void load(){
        File file = new File(FILE_PLANETS);
    }
}
