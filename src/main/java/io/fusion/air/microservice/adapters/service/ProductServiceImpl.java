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

import io.fusion.air.microservice.adapters.repository.ProductRepository;
import io.fusion.air.microservice.domain.entities.example.ProductEntity;
import io.fusion.air.microservice.domain.exceptions.DataNotFoundException;

import io.fusion.air.microservice.domain.models.example.Product;
import io.fusion.air.microservice.domain.ports.services.ProductService;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.RequestScope;


import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * An Example of Standard CRUD Operations in a Jpa Repository
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Service
@RequestScope
@Transactional
public class ProductServiceImpl implements ProductService {

    // Set Logger -> Lookup will automatically determine the class name.
    private static final Logger log = getLogger(lookup().lookupClass());

    @Autowired
    private ProductRepository productRepository;

    /**
     * WARNING:
     * This Method is ONLY For Demo Purpose. In Real World Scenario there should NOT be any
     * method which will return the whole data without any Conditions. Unless it's a very
     * small data set.
     *
     * Get All the Products
     * @return
     */
    @Override
    public List<ProductEntity> getAllProduct() {
        return this.productRepository.findAll();
    }

    /**
     * Search for the Product By the Product Names Like 'name'
     * @param _name
     * @return
     */
    public List<ProductEntity> fetchProductsByName(String _name) {
        String name = _name != null ? _name.trim() : "%";
        List<ProductEntity> products = productRepository.findByProductNameContains(name);
        return checkProducts(products, name);
    }

    /**
     * Search for the Product By Price Greater Than or Equal To
     * @param price
     * @return
     */
    public List<ProductEntity> fetchProductsByPriceGreaterThan(BigDecimal price) {
        List<ProductEntity> products = productRepository.fetchProductsByPriceGreaterThan(price);
        return checkProducts(products, price);
    }

    /**
     * Returns Active Products Only
     * @return
     */
    public List<ProductEntity> fetchActiveProducts() {
        List<ProductEntity> products = productRepository.fetchActiveProducts();
        return checkProducts(products, "isActive");
    }

    /**
     * Checks if the Products List Contains Data
     * @param products
     * @return
     */
    private List<ProductEntity> checkProducts(List<ProductEntity> products, Object search) {
        if(products == null || products.size() == 0) {
            throw new DataNotFoundException("No Data Found for the Search Query = ["+search+"]");
        }
        return products;
    }

    /**
     * Get Product By Product ID
     * @param productId
     * @return
     */
    @Override
    public ProductEntity getProductById(UUID productId) {
        Optional<ProductEntity> productDb = productRepository.findById(productId);
        if(productDb.isPresent()) {
            return productDb.get();
        }
        throw new DataNotFoundException("Data not found with id : " + productId);
    }

    /**
     * Create Product (from the DTO)
     * @param product
     * @return
     */
    public ProductEntity createProduct(Product product) {
        return createProduct(new ProductEntity(product));
    }

    /**
     * Create Product
     *
     * @param product
     * @return
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity createProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    /**
     * Create Products (from List of DTOs)
     * @param products
     * @return
     */
    public List<ProductEntity> createProducts(List<Product> products) {
        List<ProductEntity> productList = new ArrayList<ProductEntity>();
        for(Product p : products) {
            productList.add(new ProductEntity(p));
        }
        return createProductsEntity(productList);
    }

    /**
     * Create Products (from List of ProductEntity)
     * @param products
     * @return
     */
    @Transactional(rollbackFor = { SQLException.class })
    public List<ProductEntity> createProductsEntity(List<ProductEntity> products) {
        return productRepository.saveAll(products);
    }

    /**
     * Update Product
     *
     * @param product
     * @return
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity updateProduct(ProductEntity product) {
        productRepository.saveAndFlush(product);
        return product;
    }

    /**
     * Update the Product Price
     * @param product
     * @return
     */
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity updatePrice(ProductEntity product) {
        ProductEntity productUpdate = getProductById(product.getUuid()) ;
        productUpdate.setProductPrice(product.getProductPrice());
        productRepository.saveAndFlush(productUpdate);
        return productUpdate;
    }

    /**
     * Update Product (Name & Details)
     *
     * @param product
     * @return
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity updateProductDetails(ProductEntity product) {
        ProductEntity productUpdate = getProductById(product.getUuid()) ;
        productUpdate.setProductName(product.getProductName());
        productUpdate.setProductDetails(product.getProductDetails());
        productRepository.saveAndFlush(productUpdate);
        return productUpdate;
    }

    /**
     * De Activate Product
     *
     * @param _productId
     * @return
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity deActivateProduct(UUID _productId) {
        ProductEntity product = getProductById(_productId);
        product.deActivateProduct();
        productRepository.saveAndFlush(product);
        return product;
    }

    /**
     * Activate Product
     *
     * @param _productId
     * @return
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public ProductEntity activateProduct(UUID _productId) {
        ProductEntity product = getProductById(_productId);
        product.activateProduct();
        productRepository.saveAndFlush(product);
        return product;
    }

    /**
     * Delete the product
     * @param _productId
     */
    @Override
    @Transactional(rollbackFor = { SQLException.class })
    public void deleteProduct(UUID _productId) {
        ProductEntity product = getProductById(_productId);
        productRepository.delete(product);
    }
}
