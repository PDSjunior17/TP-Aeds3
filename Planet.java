import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Planet {
    private int id;
    private long dateRelaseSecond;
    private String name;//string de tamanho variavel
    private String host;//string de tamanho fixo, colocar um teste de avaliação
    private int numStars;
    private int numPlanets;
    private String discoveryMethod;//string de tamanho fixo, colocar um teste de avaliação
    private int discoveryYear;
    private String discoveryFacility;//string de tamanho variavel
    private boolean controv;
    private long  mass;
    private double starTemperature;//usar o numero -1 para representar que não tem o valor da temperatura
    private String[] metalRatio;//lista de valores

    public Planet(int id, String name, String host, int numStars, int numPlanets,
            String discoveryMethod, int discoveryYear, String discoveryFacility, boolean controv, long mass,
            double starTemperature, String metalRatio,long dataRelase) {
        this.id = id;
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
        //tratando caso de nao haver informações
        String[] tmp = {"V"};
        if(metalRatio == null){this.metalRatio = tmp;}
        else{
            metalRatio = metalRatio.replaceAll("[\\[\\]]", "");
            this.metalRatio = metalRatio.split("/");
        }
        
        this.dateRelaseSecond = dataRelase;
    }

        public Planet(int id, String name, String host, int numStars, int numPlanets,
            String discoveryMethod, int discoveryYear, String discoveryFacility, boolean controv, long mass,
            double starTemperature, String metalRatio,String dataRelase) {
        this.id = id;
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
        //tratando caso de nao haver informações
        String[] tmp = {"V"};
        if(metalRatio == null){this.metalRatio = tmp;}
        else{
            metalRatio = metalRatio.replaceAll("[\\[\\]]", "");
            this.metalRatio = metalRatio.split("/");
        }
        
        this.dateRelaseSecond = dateToSeconds(dataRelase);
    }

    

    //construtor com parametro
    public Planet() {
        this.id = -1;
        this.dateRelaseSecond = -1;
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
        if(metalRatio[0].compareTo("V") == 0){return "*";}
        int tam = metalRatio.length;
        String tmp = "";
        tmp += metalRatio[0];
        for(int i = 1;i<tam;i++){
            tmp = tmp +";" + metalRatio[i];
        }
        return tmp;
    }

    //Método para transformar o objeto em uma sequencia de bytes
     public byte[] toByteArray()throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        //lapide marcada !
        dos.writeChar('+');//lapide nao marcada - subtrair 2 bytes na main 
        dos.writeInt(id);
        dos.writeLong(dateRelaseSecond);
        dos.writeUTF(name);
        dos.writeUTF(host);
        dos.writeInt(numStars);
        dos.writeInt(numPlanets);
        dos.writeUTF(discoveryMethod);
        dos.writeInt(discoveryYear);
        dos.writeUTF(discoveryFacility);
        dos.writeBoolean(controv);
        dos.writeLong(mass);
        dos.writeDouble(starTemperature);

        String s = catString();
        dos.writeUTF(s);
        return baos.toByteArray();
     }

    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        

        this.id = dis.readInt();
        this.dateRelaseSecond = dis.readLong();
        this.name = dis.readUTF();
        this.host = dis.readUTF();
        this.numStars = dis.readInt();
        this.numPlanets = dis.readInt();
        this.discoveryMethod = dis.readUTF();
        this.discoveryYear = dis.readInt();
        this.discoveryFacility = dis.readUTF();
        this.controv = dis.readBoolean();
        this.mass = dis.readLong();
        this.starTemperature = dis.readDouble();
    }

    public String toString(){
        return "\nID: " + id + "\nDate: " + dateRelaseSecond + "\nName: " + name + "\nHost: " + host + 
        "\nNumber of Stars: " + numStars + "\nNumber of Planets: " + numPlanets + "\nDiscovery Method: " + discoveryMethod + 
        "\nDiscovery Year: " + discoveryYear + "\nDiscovery Facility: " + discoveryFacility + "\nControversial Flag: " + controv + 
        "\nStellar Mass: " + mass + "\nStellar Temperature: " + starTemperature;
    }

    public static long dateToSeconds(String date) {
        if(date == null){return 0L;}
        LocalDate localDate = LocalDate.parse(date);
        
        Instant instant = localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        
        long secondsSinceEpoch = instant.getEpochSecond();
        
        return secondsSinceEpoch*1000;
       }

    public void setId(int id) {
        this.id = id;
    }

    public void setDataRelase(String dataRelase) {
        this.dateRelaseSecond = dateToSeconds(dataRelase);
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
        return dateRelaseSecond;
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
