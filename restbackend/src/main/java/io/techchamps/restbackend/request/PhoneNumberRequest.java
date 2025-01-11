package io.techchamps.restbackend.request;

import io.techchamps.restbackend.entity.PhoneNumber;

public class PhoneNumberRequest {

    private String number;
    private PhoneNumber.PhoneType type;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public PhoneNumber.PhoneType getType() {
        return type;
    }

    public void setType(PhoneNumber.PhoneType type) {
        this.type = type;
    }
}
