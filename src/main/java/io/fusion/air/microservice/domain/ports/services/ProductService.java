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
package io.fusion.air.microservice.domain.ports.services;

import io.fusion.air.microservice.domain.entities.example.ProductEntity;
import io.fusion.air.microservice.domain.models.example.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
public interface ProductService {

    /**
     * Get All the Products
     * @return
     */
    public List<ProductEntity> getAllProduct();

    /**
     * Get Product By Product ID
     * @param productId
     * @return
     */
    public ProductEntity getProductById(UUID productId);

    /**
     * Search for the Product By the Product Names Like 'name'
     * @param _name
     * @return
     */
    public List<ProductEntity> fetchProductsByName(String _name);

    /**
     * Search for the Product By Price Greater Than or Equal To
     * @param price
     * @return
     */
    public List<ProductEntity> fetchProductsByPriceGreaterThan(BigDecimal price);

    /**
     * Returns Active Products Only
     * @return
     */
    public List<ProductEntity> fetchActiveProducts();

    /**
     * Create Product (from DTO)
     * @param product
     * @return
     */
    public ProductEntity createProduct(Product product);

    /**
     * Create Product
     * @param product
     * @return
     */
    public ProductEntity createProduct(ProductEntity product);

    /**
     * Create Products (from List of DTOs)
     * @param products
     * @return
     */
    public List<ProductEntity> createProducts(List<Product> products);

    /**
     * Create Products (from List of ProductEntity)
     * @param products
     * @return
     */
    public List<ProductEntity> createProductsEntity(List<ProductEntity> products);

    /**
     * Update Product
     * @param product
     * @return
     */
    public ProductEntity updateProduct(ProductEntity product);

    /**
     * Update the Product Price
     * @param product
     * @return
     */
    public ProductEntity updatePrice(ProductEntity product);

    /**
     * Update Product Name & Details
     * @param product
     * @return
     */
    public ProductEntity updateProductDetails(ProductEntity product);

    /**
     * De Activate Product
     * @param _productId
     * @return
     */
    public ProductEntity deActivateProduct(UUID _productId);

    /**
     * Activate Product
     * @param _productId
     * @return
     */
    public ProductEntity activateProduct(UUID _productId);

    /**
     * Delete the product (Permanently Deletes the Product)
     * @param id
     */
    public void deleteProduct(UUID id);
}
