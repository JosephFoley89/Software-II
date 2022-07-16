package model;
/**Model class for Countries
 * @author Joe Foley*/
public class Country {
    private int ID;
    private String name;

    /**Class constructor*/
    public Country(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    /**Getters,Setters*/
    public int getID() { return ID; }
    public String getName() { return name; }

    public void setID(int ID) { this.ID = ID; }
    public void setName(String name) { this.name = name; }
}
