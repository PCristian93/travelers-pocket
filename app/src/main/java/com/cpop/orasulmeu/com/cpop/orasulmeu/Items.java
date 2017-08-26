package com.cpop.orasulmeu.com.cpop.orasulmeu;

/**
 * Created by Cristi on 24-Apr-16.
 */
public class Items {
    private int id;
    private String name;
    private String description;
    private String address;
    private String rating;
    private String open_time;
    private String close_time;
    private double latitude;
    private double longitude;

    public Items (String n, String a)
    {
        this.name=n;
//        this.description=d;
        this.address=a;
//        this.rating=r;
//        this.open_time=o;
//        this.close_time=c;
//        this.latitude=lat;
//        this.longitude=longit;
    }

    public Items() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override

    public String toString(){
        return this.name+" "+this.description+" "+this.address+" "+this.rating+" "+this.open_time+" "+this.close_time;
    }
}
