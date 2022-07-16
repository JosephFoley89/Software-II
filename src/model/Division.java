package model;
/**Model class for divisions
 * @author Joe Foley*/
public class Division {
    private int ID;
    private int countryID;
    private String name;

    /**Class constructor*/
    public Division(int ID, int countryID, String name) {
        this.ID = ID;
        this.countryID = countryID;
        this.name = name;
    }

    /**Getters,Setters*/
    public int getID() { return ID; }
    public int getCountryID() { return countryID; }
    public String getName() { return name; }

    public void setID(int ID) { this.ID = ID; }
    public void setCountryID(int countryID) { this.countryID = countryID;  }
    public void setName(String name) { this.name = name; }
}
