package model;
/**Model Class for Contacts
 * @author Joe Foley*/
public class Contact {
    private int ID;
    private String name;
    private String email;

    /**Class constructor*/
    public Contact(int ID, String name, String email) {
        this.ID = ID;
        this.name = name;
        this.email = email;
    }

    /**Getters, Setters*/
    public int getID() { return ID; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setID(int ID) { this.ID = ID; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
