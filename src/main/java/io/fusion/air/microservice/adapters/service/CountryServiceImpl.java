/**
 * (C) Copyright 2021 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.adapters.service;

import io.fusion.air.microservice.adapters.repository.CountryGeoRepository;
import io.fusion.air.microservice.domain.entities.order.CountryEntity;
import io.fusion.air.microservice.adapters.repository.CountryRepository;
import io.fusion.air.microservice.domain.entities.order.CountryGeoEntity;
import io.fusion.air.microservice.domain.ports.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
public class CountryServiceImpl implements CountryService {

    // Autowired using Constructor
    private final CountryRepository countryRepositoryImpl;

    // Autowired using Constructor
    private final CountryGeoRepository countryGeoRepositoryImpl;

    public CountryServiceImpl(CountryRepository countryRepo, CountryGeoRepository countryGeoRepo) {
        countryRepositoryImpl = countryRepo;
        countryGeoRepositoryImpl = countryGeoRepo;
    }

    @Override
    public List<CountryEntity> getAllCountries() {
        return (List<CountryEntity>) countryRepositoryImpl.findAll();
    }

    /**
     * Get All Geo Countries
     * @return
     */
    @Transactional(readOnly = true)
    public Page<CountryGeoEntity> getAllGeoCountries() {
        return getAllGeoCountries(1, 10);
    }

    /**
     * Get All Geo Countries by Page Number and No. of Records (Size)
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<CountryGeoEntity> getAllGeoCountries(int page, int size) {
        return countryGeoRepositoryImpl.findAll(PageRequest.of(page, size));
    }
}
