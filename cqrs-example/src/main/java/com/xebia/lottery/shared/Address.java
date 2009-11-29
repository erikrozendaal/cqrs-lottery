package com.xebia.lottery.shared;

import com.xebia.cqrs.domain.ValueObject;

public class Address extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final String streetName;
    private final String houseNumber;
    private final String postalCode;
    private final String city;
    private final String country;

    public Address(String streetName, String houseNumber, String postalCode, String city, String country) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

}
