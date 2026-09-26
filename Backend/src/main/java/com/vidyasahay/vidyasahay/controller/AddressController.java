package com.vidyasahay.vidyasahay.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.response.AddressCityOption;
import com.vidyasahay.vidyasahay.entity.Address;
import com.vidyasahay.vidyasahay.repository.AddressRepository;

@RestController
@RequestMapping("/api/address")
@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
public class AddressController {

    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @GetMapping("/states")
    public List<String> getStates() {
        return addressRepository.findDistinctStates();
    }

    @GetMapping("/districts")
    public List<String> getDistricts(@RequestParam String state) {
        return addressRepository.findDistinctDistrictsByState(state.trim());
    }

    @GetMapping("/cities")
    public List<AddressCityOption> getCities(
            @RequestParam String state,
            @RequestParam String district) {

        LinkedHashMap<String, AddressCityOption> citiesByName = new LinkedHashMap<>();
        for (Address address : addressRepository.findCitiesByStateAndDistrict(state.trim(), district.trim())) {
            String key = address.getCity().trim().toLowerCase(Locale.ROOT);
            citiesByName.putIfAbsent(key, new AddressCityOption(address.getId(), address.getCity()));
        }
        return List.copyOf(citiesByName.values());
    }
}
