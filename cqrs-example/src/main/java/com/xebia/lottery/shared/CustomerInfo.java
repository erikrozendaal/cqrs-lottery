package com.xebia.lottery.shared;

import com.xebia.cqrs.domain.ValueObject;

public class CustomerInfo extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String email;
    private final Address address;
    
    public CustomerInfo(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }
    
}
