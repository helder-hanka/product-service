package com.ff.products_service.service;

import com.ff.products_service.entity.Product;
import com.ff.products_service.rabbitmq.events.ProductEvent;
import com.ff.products_service.rabbitmq.events.ProductEventPublisher;
import com.ff.products_service.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductEventPublisher publisher;



    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public Product findById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    public Product create(Product product) {
        Product savedProduct = productRepo.save(product);

        // Publish product creation event
        publisher.publish(new ProductEvent(
                savedProduct.getId().toString(),
                savedProduct.getName(),
                savedProduct.getPrice()
        ));
        return savedProduct;
    }

    public Product update(Long id, Product product) {
        Product oldProduct = productRepo.findById(id).orElse(null);
        if (oldProduct == null) return null;
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        return productRepo.save(oldProduct);
    }

    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    public int getStockByProductId(Long productId) {
        Product product = productRepo.findById(productId).orElse(null);
        return product != null ? product.getStock() : -1;
    }
}