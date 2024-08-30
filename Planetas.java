import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class Planetas {
    private int id;
    private long dataRelaseSecond;
    private String name;//string de tamanho variavel
    private String houst;//string de tamanho fixo
    private int numStars;
    private int numPlanets;
    private String discoveryMethod;//string de tamanho fixo
    private int discoveryYear;
    private String discoveryFacility;//string de tamanho variavel
    private boolean controv;
    private long  massa;
    private double temperatureStar;//usar o numero -1 para representar que não tem o valor da temperatura
    private String[] metalRatio;//lista de valores

    public Planetas(int id,long dataRelase, String name, String houst, int numStars, int numPlanets,
            String discoveryMethod, int discoveryYear, String discoveryFacility, boolean controv, long massa,
            double temperatureStar, String metalRatio) {
        this.id = id;
        this.dataRelaseSecond = dataRelase;
        this.name = name;
        this.houst = houst;
        this.numStars = numStars;
        this.numPlanets = numPlanets;
        this.discoveryMethod = discoveryMethod;
        this.discoveryYear = discoveryYear;
        this.discoveryFacility = discoveryFacility;
        this.controv = controv;
        this.massa = massa;
        this.temperatureStar = temperatureStar;
        metalRatio = metalRatio.replaceAll("[\\[\\]]", "");
        this.metalRatio = metalRatio.split("/");
    }

    //construtor com parametro
    public Planetas() {
        this.id = -1;
        this.dataRelaseSecond = -1;
        this.name = null;
        this.houst = null;
        this.numStars = -1;
        this.numPlanets = -1;
        this.discoveryMethod = null;
        this.discoveryYear = -1;
        this.discoveryFacility = null;
        this.controv = false;
        this.massa = -1;
        this.temperatureStar = -1;
        this.metalRatio = null;
    }


    private String catString(){
        int tam = metalRatio.length;
        String tmp = "";
        for(int i = 0;i<tam;i++){
            tmp = tmp +";" + metalRatio[i];
        }
        return tmp;
    }

    //metodo para transformar o objetio em uma sequencia de bytes
     public byte[] toByteArray()throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] by;
        int tamBytes;
        dos.writeChar('+');//subtrair 2 bytes na main
        dos.writeInt(id);
        dos.writeLong(dataRelaseSecond);

        by = name.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(name);

        by = houst.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(houst);

        dos.writeInt(numStars);
        dos.writeInt(numPlanets);

        by = discoveryMethod.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(discoveryMethod);

        dos.writeInt(discoveryYear);

        by = discoveryFacility.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(discoveryFacility);

        dos.writeBoolean(controv);
        dos.writeLong(massa);
        dos.writeDouble(temperatureStar);

        String s = catString();
        by = s.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(s);
        return baos.toByteArray();

     }

    public void setId(int id) {
        this.id = id;
    }

    public void setDataRelase(long dataRelase) {
        this.dataRelaseSecond = dataRelase;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHoust(String houst) {
        this.houst = houst;
    }

    public void setNumStars(int numStars) {
        this.numStars = numStars;
    }

    public void setNumPlanets(int numPlanets) {
        this.numPlanets = numPlanets;
    }

    public void setDiscoveryMethod(String discoveryMethod) {
        this.discoveryMethod = discoveryMethod;
    }

    public void setDiscoveryYear(int discoveryYear) {
        this.discoveryYear = discoveryYear;
    }

    public void setDiscoveryFacility(String discoveryFacility) {
        this.discoveryFacility = discoveryFacility;
    }

    public void setControv(boolean controv) {
        this.controv = controv;
    }

    public void setMassa(long massa) {
        this.massa = massa;
    }

    public void setTemperatureStar(double temperatureStar) {
        this.temperatureStar = temperatureStar;
    }

    public void setMetalRatio(String[] metalRatio) {
        this.metalRatio = metalRatio;
    }

    public int getId() {
        return id;
    }

    public long getDataRelase() {
        return dataRelaseSecond;
    }

    public String getName() {
        return name;
    }

    public String getHoust() {
        return houst;
    }

    public int getNumStars() {
        return numStars;
    }

    public int getNumPlanets() {
        return numPlanets;
    }

    public String getDiscoveryMethod() {
        return discoveryMethod;
    }

    public int getDiscoveryYear() {
        return discoveryYear;
    }

    public String getDiscoveryFacility() {
        return discoveryFacility;
    }

    public boolean isControv() {
        return controv;
    }

    public long getMassa() {
        return massa;
    }

    public double getTemperatureStar() {
        return temperatureStar;
    }

    public String[] getMetalRatio() {
        return metalRatio;
    }

    

    

    

    
    


}
