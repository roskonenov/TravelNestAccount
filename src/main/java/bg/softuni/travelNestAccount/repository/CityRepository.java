package bg.softuni.travelNestAccount.repository;


import bg.softuni.travelNestAccount.model.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {

    CityEntity findByName(String city);

    CityEntity findByNameIgnoreCase(String city);
}
