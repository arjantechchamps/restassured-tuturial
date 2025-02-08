package Builders;

import generated.dtos.AddressRequest;
import generated.dtos.UserRequest;
import org.checkerframework.checker.units.qual.A;

public class AdressRequestBuilder {

    private final AddressRequest addressRequest;

    public  AdressRequestBuilder() {
        addressRequest = new AddressRequest();
        addressRequest.setZipcode("1111AA");
        addressRequest.setHouseNumber("5");
        addressRequest.setCity("Enschede");
        addressRequest.setStreet("Some street");
        addressRequest.setCountry("Netherlands");
    }

    // With method to override zipcode.
    public AdressRequestBuilder withZipCode(String zipCode) {
        addressRequest.setZipcode(zipCode);
        return this;
    }

    public AdressRequestBuilder withHouseNumber(String houseNumber){
        addressRequest.setHouseNumber(houseNumber);
        return this;
    }

    public AdressRequestBuilder withCity(String city){
        addressRequest.setCity(city);
        return this;
    }

    public AdressRequestBuilder withStreet(String street){
        addressRequest.setStreet(street);
        return this;
    }

    public AdressRequestBuilder withCountry(String country){
        addressRequest.setCountry(country);
        return this;
    }


    // Build the AddresRequest object.
    public AddressRequest build() {
        return addressRequest;
    }


}
