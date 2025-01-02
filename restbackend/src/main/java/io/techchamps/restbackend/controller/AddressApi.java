package io.techchamps.restbackend.controller;

import io.techchamps.restbackend.entity.Address;
import io.techchamps.restbackend.services.AddressService;
import io.techchamps.restbackend.services.UserService;
import io.techchamps.restbackend.enums.AddressType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressApi {

    @Autowired
    private AddressService addressService;

    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/create/{userId}")
    public ResponseEntity<Address> createAddress(@PathVariable int userId,
                                                 @RequestBody Address address,
                                                 @RequestParam AddressType addressType)  {
        Address createdAddress = addressService.createAddress(userId, address, addressType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update/{userId}")
    public ResponseEntity<Address> updateAddress(@PathVariable int userId,
                                                 @RequestBody Address address,
                                                 @RequestParam AddressType addressType) {
        Address updatedAddress = addressService.updateAddress(userId, address, addressType);
        return ResponseEntity.ok(updatedAddress);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteAddress(@PathVariable int userId,
                                                @RequestParam AddressType addressType) {
        addressService.deleteAddress(userId, addressType);
        return ResponseEntity.ok("Address deleted successfully");
    }
}
