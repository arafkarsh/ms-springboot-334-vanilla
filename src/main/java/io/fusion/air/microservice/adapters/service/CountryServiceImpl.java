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

import io.fusion.air.microservice.domain.entities.example.CountryEntity;
import io.fusion.air.microservice.adapters.repository.CountryRepository;
import io.fusion.air.microservice.domain.ports.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    CountryRepository countryRepositoryImpl;

    @Override
    public List<CountryEntity> getAllCountries() {
        return (List<CountryEntity>) countryRepositoryImpl.findAll();
    }
}
