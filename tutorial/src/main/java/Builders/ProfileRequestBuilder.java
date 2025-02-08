package Builders;

import generated.dtos.AddressRequest;
import generated.dtos.ProfileRequest;

public class ProfileRequestBuilder {

    private final ProfileRequest profileRequest;

    public ProfileRequestBuilder (){
        profileRequest = new ProfileRequest();
        profileRequest.addAddressesItem(new AdressRequestBuilder().build());
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.FOOD);
        profileRequest.addInterestsItem(ProfileRequest.InterestsEnum.SPORTS);
    }

    public ProfileRequestBuilder withAddress(AddressRequest addressRequest){
        profileRequest.getAddresses().clear();
        profileRequest.addAddressesItem(addressRequest);
        return this;
    }
    public ProfileRequestBuilder withInterest(ProfileRequest.InterestsEnum interrest){
        profileRequest.getInterests().clear();
        profileRequest.addInterestsItem(interrest);
        return this;
    }


    // Build the ProfileRequest object.
    public ProfileRequest build() {
        return profileRequest;
    }


}
