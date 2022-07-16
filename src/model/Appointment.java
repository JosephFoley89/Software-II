package model;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**Model class for the appointment object
 * @author Joe Foley*/
public class Appointment {
    private int appID;
    private int customerID;
    private int userID;
    private int contactID;
    private String title;
    private String description;
    private String location;
    private String type;
    private String contactName;
    private LocalDate startDate;
    private LocalDate endDate;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String startTimeString;
    private String endTimeString;
    private ZonedDateTime zDateTime;

    /**Class constructor*/
    public Appointment(int appID, int customerID, int userID, int contactID, String title, String description, String location, String type, String contactName, ZonedDateTime startTime, ZonedDateTime endTime, ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        this.appID = appID;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.contactName = contactName;
        this.startDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.startTime = startTime.toInstant().atZone(ZoneId.systemDefault());
        this.endTime = endTime.toInstant().atZone(ZoneId.systemDefault());
        this.startTimeString = startTime.toInstant().atZone(ZoneId.systemDefault()).format(formatter);
        this.endTimeString = endTime.toInstant().atZone(ZoneId.systemDefault()).format(formatter);
        this.zDateTime = date;
    }

    /**Getters and setters*/
    public int getAppID() { return appID; }
    public int getCustomerID() { return customerID; }
    public int getUserID() { return userID; }
    public int getContactID() { return contactID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getContactName() { return contactName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalTime getStartTime() { return startTime.toLocalTime(); }
    public LocalTime getEndTime() { return endTime.toLocalTime(); }
    public String getStartTimeString() {return startTimeString; }
    public String getEndTimeString() { return endTimeString; }


    public void setAppID(int appID) { this.appID = appID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public void setContactID(int contactID) { this.contactID = contactID; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setStartTime(LocalTime startTime) { this.startTime = ZonedDateTime.of(LocalDate.now(), startTime, ZoneId.systemDefault()); }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setEndTime(LocalTime endTime) { this.endTime = ZonedDateTime.of(LocalDate.now(), endTime, ZoneId.systemDefault());; }
}
