package model;
/**Model class for users
 * @author Joe Foley*/
public class User {
    private int ID;
    private String name;
    private String password;

    /**Class constructor*/
    public User(int ID, String name, String password) {
        this.ID = ID;
        this.name = name;
        this.password = password;
    }

    /**Getters,Setters*/
    public int getID() { return ID; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    public void setID(int ID) { this.ID = ID; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
}
