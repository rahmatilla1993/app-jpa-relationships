package uz.pdp.appjparelationships.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjparelationships.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    boolean existsByCityAndDistrictAndStreetAndHouseNumber(String city, String district, String street, Integer houseNumber);
}
