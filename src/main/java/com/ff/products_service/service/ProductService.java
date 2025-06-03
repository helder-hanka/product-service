package com.ff.products_service.service;

import com.ff.products_service.entity.Product;
import com.ff.products_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id,Product product) {
        Product oldProduct = findById(id);
        if(oldProduct == null) return null;
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        return productRepository.save(oldProduct);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public int getStock(Long id) {
        Product product = findById(id);
        return product != null ? product.getStock() : -1;
    }

}
