package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.repository.AddressRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    AddressRepository addressRepository;

    @GetMapping
    public List<Address> getAddresses(){
        return addressRepository.findAll();
    }

    @PostMapping
    public String addAddress(@RequestBody Address address){
        Address new_address=new Address();
        String city = address.getCity();
        String district = address.getDistrict();
        String street = address.getStreet();
        Integer houseNumber = address.getHouseNumber();
        if(addressRepository.existsByCityAndDistrictAndStreetAndHouseNumber(city,district,street,houseNumber)){
            return "Address already exists";
        }
        new_address.setCity(city);
        new_address.setDistrict(district);
        new_address.setStreet(street);
        new_address.setHouseNumber(houseNumber);
        return "Address added";
    }

    @DeleteMapping("/{id}")
    public String deleteAddress(@PathVariable Integer id){
        Optional<Address> optionalAddress = addressRepository.findById(id);
        if(optionalAddress.isPresent()){
            Address address = optionalAddress.get();
            addressRepository.delete(address);
            return "Address deleted";
        }
        return "Address not found";
    }

    
}
