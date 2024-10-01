package io.fusion.air.microservice.domain.ports.services;

import io.fusion.air.microservice.domain.entities.example.CountryEntity;
import java.util.List;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public interface CountryService {

    /**
     * Returns all the Countryy
     * @return
     */
    public List<CountryEntity> getAllCountries();
}
