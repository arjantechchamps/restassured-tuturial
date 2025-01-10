package io.techchamps.restbackend.services;

import io.techchamps.restbackend.entity.Address;
import io.techchamps.restbackend.entity.User;
import io.techchamps.restbackend.enums.AddressType;
import io.techchamps.restbackend.exception.CustomException;
import io.techchamps.restbackend.repository.AddressRepository;
import io.techchamps.restbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(int userId, Address address, AddressType addressType) {
        User user = getUserWithAdminCheck(userId);

        if (addressType == AddressType.WORK) {
            user.setWorkAddress(address);
        } else if (addressType == AddressType.HOME) {
            user.setHomeAddress(address);
        } else {
            throw new IllegalArgumentException("Invalid address type.");
        }

        userRepository.save(user);
        return address;
    }

    public Address updateAddress(int userId, Address address, AddressType addressType)  {
        User user = getUserWithAdminCheck(userId);

        if (addressType == AddressType.WORK) {
            Address currentWorkAddress = user.getWorkAddress();
            if (currentWorkAddress != null) {
                address.setId(currentWorkAddress.getId());
            }
            user.setWorkAddress(address);
        } else if (addressType == AddressType.HOME) {
            Address currentHomeAddress = user.getHomeAddress();
            if (currentHomeAddress != null) {
                address.setId(currentHomeAddress.getId());
            }
            user.setHomeAddress(address);
        } else {
            throw new IllegalArgumentException("Invalid address type.");
        }

        userRepository.save(user);
        return address;
    }

    public void deleteAddress(int userId, AddressType addressType) {
        User user = getUserWithAdminCheck(userId);

        if (addressType == AddressType.WORK) {
            user.setWorkAddress(null);
        } else if (addressType == AddressType.HOME) {
            user.setHomeAddress(null);
        } else {
            throw new IllegalArgumentException("Invalid address type.");
        }

        userRepository.save(user);
    }

    private User getUserWithAdminCheck(int userId) {
        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        // Check if the authenticated user has the ROLE_ADMIN authority
        boolean isAdmin = userPrinciple.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        // If the user is not an admin, ensure they are accessing their own data
        if (!isAdmin && userPrinciple.getId() != userId) {
            throw new CustomException("You do not have permission to access this resource.", HttpStatus.NOT_FOUND);
        }

        // Retrieve the user by ID
        return userService.findById(userId)
                .orElseThrow(() -> new CustomException("User not found with ID: " + userId, HttpStatus.NOT_FOUND));
    }

}
