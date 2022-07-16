package model;

public class Customer {
    private int customerID;
    private int divisionID;
    private String name;
    private String address;
    private String postalCode;
    private String division;
    private String country;
    private String phone;

    /**Customer constructor method*/
    public Customer(int customerID, int divisionID, String name, String address, String phone, String postalCode, String division, String country) {
        this.customerID = customerID;
        this.divisionID = divisionID;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.postalCode = postalCode;
        this.division = division;
        this.country = country;
    }

    /**Getters, Setters*/
    public int getCustomerID() { return customerID; }
    public int getDivisionID() { return divisionID; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCountry() { return country; }
    public String getDivision() { return division; }
    public String getPostalCode() { return postalCode; }
    public String getPhone() { return phone; }

    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }
    public void setAddress(String address) {  this.address = address; }
    public void setCountry(String country) { this.country = country; }
    public void setName(String name) { this.name = name; }
    public void setDivision(String division) { this.division = division; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
