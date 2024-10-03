package io.fusion.air.microservice.domain.ports.services;

import io.fusion.air.microservice.domain.entities.order.CountryEntity;
import io.fusion.air.microservice.domain.entities.order.CountryGeoEntity;
import org.springframework.data.domain.Page;

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

    /**
     * Get All Geo Countries
     * @return
     */
    public Page<CountryGeoEntity> getAllGeoCountries();

    /**
     * Get All Geo Countries by Page Number and No. of Records (Size)
     * @param page
     * @param size
     * @return
     */
    public Page<CountryGeoEntity> getAllGeoCountries(int page, int size);

}
