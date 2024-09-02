import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class Planet {
    private int id;
    private long dataRelaseSecond;
    private String name;//string de tamanho variavel
    private String host;//string de tamanho fixo
    private int numStars;
    private int numPlanets;
    private String discoveryMethod;//string de tamanho fixo
    private int discoveryYear;
    private String discoveryFacility;//string de tamanho variavel
    private boolean controv;
    private long  mass;
    private double starTemperature;//usar o numero -1 para representar que não tem o valor da temperatura
    private String[] metalRatio;//lista de valores

    public Planet(int id,long dataRelase, String name, String host, int numStars, int numPlanets,
            String discoveryMethod, int discoveryYear, String discoveryFacility, boolean controv, long mass,
            double starTemperature, String metalRatio) {
        this.id = id;
        this.dataRelaseSecond = dataRelase;
        this.name = name;
        this.host = host;
        this.numStars = numStars;
        this.numPlanets = numPlanets;
        this.discoveryMethod = discoveryMethod;
        this.discoveryYear = discoveryYear;
        this.discoveryFacility = discoveryFacility;
        this.controv = controv;
        this.mass = mass;
        this.starTemperature = starTemperature;
        metalRatio = metalRatio.replaceAll("[\\[\\]]", "");
        this.metalRatio = metalRatio.split("/");
    }

    //construtor com parametro
    public Planet() {
        this.id = -1;
        this.dataRelaseSecond = -1;
        this.name = null;
        this.host = null;
        this.numStars = -1;
        this.numPlanets = -1;
        this.discoveryMethod = null;
        this.discoveryYear = -1;
        this.discoveryFacility = null;
        this.controv = false;
        this.mass = -1;
        this.starTemperature = -1;
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

    //Método para transformar o objeto em uma sequencia de bytes
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

        by = host.getBytes();
        tamBytes = by.length;
        //escrevendo o tamanho da string
        dos.writeInt(tamBytes);
        dos.writeUTF(host);

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
        dos.writeLong(mass);
        dos.writeDouble(starTemperature);

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

    public void setHost(String host) {
        this.host = host;
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

    public void setMass(long mass) {
        this.mass = mass;
    }

    public void setstarTemperature(double starTemperature) {
        this.starTemperature = starTemperature;
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

    public String getHost() {
        return host;
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

    public long getMass() {
        return mass;
    }

    public double getstarTemperature() {
        return starTemperature;
    }

    public String[] getMetalRatio() {
        return metalRatio;
    }
}
