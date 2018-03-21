package com.sf.vivosmainapp;

/**
 * Created by mesutgenc on 21.03.2018.
 */

public class Metro {
    private String code;
    private String name;
    private String type;
    private String postcode;
    private String city;
    private String town;
    private String neighborhood;
    private String yCoor;
    private String xCoor;
    private String address;

    public Metro() {
    }

    public Metro(String code, String name, String type, String postcode, String city, String town, String neighborhood, String yCoor, String xCoor, String address) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.postcode = postcode;
        this.city = city;
        this.town = town;
        this.neighborhood = neighborhood;
        this.yCoor = yCoor;
        this.xCoor = xCoor;
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getyCoor() {
        return yCoor;
    }

    public void setyCoor(String yCoor) {
        this.yCoor = yCoor;
    }

    public String getxCoor() {
        return xCoor;
    }

    public void setxCoor(String xCoor) {
        this.xCoor = xCoor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
