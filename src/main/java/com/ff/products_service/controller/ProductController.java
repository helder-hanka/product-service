package com.ff.products_service.controller;

import com.ff.products_service.dto.ProductWithImagesRequest;
import com.ff.products_service.entity.Image;
import com.ff.products_service.entity.Product;
import com.ff.products_service.service.ImageService;
import com.ff.products_service.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @Transactional
    public Product createProductWithImageUrls(@RequestBody ProductWithImagesRequest request) {
        System.out.println("Request: "+request);
        // 1. Créer le produit
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        product = productService.create(product); // save initial product

        // 2. Ajouter les images
        if (request.getImages() != null) {
            int position = 0;
            for (ProductWithImagesRequest.ImageRequest imageReq : request.getImages()) {
                Image image = Image.builder()
                        .url(imageReq.getUrl())
                        .title(imageReq.getTitle())
                        .isMain(imageReq.isMain())
                        .position(position++)
                        .product(product)
                        .build();
                imageService.createImage(image);
            }
        }
        List<Image> images = imageService.getImagesByProductId(product.getId());
        product.setImages(images);
        return product;
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/{id}/stock")
    public int getProductStock(@PathVariable Long id) {

        return productService.getStockByProductId(id);

    }
}
