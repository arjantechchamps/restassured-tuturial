package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.Address;
import io.techchamps.restbackend.entity.PhoneNumber;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.request.ProfileRequest;
import io.techchamps.restbackend.response.AddressResponse;
import io.techchamps.restbackend.response.PhoneNumberResponse;
import io.techchamps.restbackend.response.ProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService {
    @Autowired
    UserService userService;

    public ProfileResponse getProfile(User user) {
        ProfileResponse profileResponse = new ProfileResponse();

        // Map Address entities to AddressResponse DTOs
        List<AddressResponse> addressResponses = user.getAddresses().stream().map(address -> {
            AddressResponse response = new AddressResponse();
            response.setStreet(address.getStreet());
            response.setHouseNumber(address.getHouseNumber());
            response.setZipcode(address.getZipcode());
            response.setCity(address.getCity());
            response.setCountry(address.getCountry());
            return response;
        }).toList();
        profileResponse.setAddresses(addressResponses);

        // Map PhoneNumber entities to PhoneNumberResponse DTOs
        List<PhoneNumberResponse> phoneNumberResponses = user.getPhoneNumbers().stream().map(phoneNumber -> {
            PhoneNumberResponse response = new PhoneNumberResponse();
            response.setNumber(phoneNumber.getNumber());
            response.setType(phoneNumber.getType());
            return response;
        }).toList();
        profileResponse.setPhoneNumbers(phoneNumberResponses);

        // Add interests directly (assuming interests are stored as strings)
        profileResponse.setInterests(user.getInterests());

        return profileResponse;
    }

    public void updateProfile(User user, ProfileRequest profileRequest) {
        // Update addresses
        if (profileRequest.getAddresses() != null) {
            user.getAddresses().clear();
            List<Address> addresses = profileRequest.getAddresses().stream().map(req -> {
                Address address = new Address();
                address.setStreet(req.getStreet());
                address.setHouseNumber(req.getHouseNumber());
                address.setZipcode(req.getZipcode());
                address.setCity(req.getCity());
                address.setCountry(req.getCountry());
                address.setUser(user);
                return address;
            }).toList();
            user.getAddresses().addAll(addresses);
        }

        // Update phone numbers
        if (profileRequest.getPhoneNumbers() != null) {
            user.getPhoneNumbers().clear();
            List<PhoneNumber> phoneNumbers = profileRequest.getPhoneNumbers().stream().map(req -> {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setNumber(req.getNumber());
                phoneNumber.setType(req.getType());
                phoneNumber.setUser(user);
                return phoneNumber;
            }).toList();
            user.getPhoneNumbers().addAll(phoneNumbers);
        }

        // Update interests
        if (profileRequest.getInterests() != null) {
            user.getInterests().clear();
            user.getInterests().addAll(profileRequest.getInterests());
        }

        // Save updated user
        userService.save(user);
    }

    public void deleteProfile(User user) {
        // Remove associated addresses and phone numbers
        user.getAddresses().clear();
        user.getPhoneNumbers().clear();
        user.getInterests().clear();

        userService.save(user);

    }
}

