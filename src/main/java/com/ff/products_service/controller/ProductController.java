package com.ff.products_service.controller;

import com.ff.products_service.dto.ProductResponseDTO;
import com.ff.products_service.dto.ProductSaveResponseDTO;
import com.ff.products_service.dto.ProductWithImagesRequest;
import com.ff.products_service.dto.UpdateProductWithImagesRequest;
import com.ff.products_service.entity.Image;
import com.ff.products_service.entity.Product;
import com.ff.products_service.service.ImageService;
import com.ff.products_service.service.ProductService;
import com.ff.products_service.utils.ApiResponse;
import com.ff.products_service.utils.ProductMapper;
import com.ff.products_service.utils.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final ProductMapper productMapper;

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
    public Product createProductWithImageUrls(@Valid @RequestBody ProductWithImagesRequest request) {
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
            for (ProductWithImagesRequest.ImageRequest imageReq : request.getImages()) {
                Image image = Image.builder()
                        .url(imageReq.getUrl())
                        .title(imageReq.getTitle())
                        .isMain(imageReq.isMain())
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
    @Transactional
    public ResponseEntity<ProductSaveResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductWithImagesRequest request) {
    Product product = productService.findById(id);
    if (product == null) {
        throw new ResourceNotFoundException("Product not found with id " + id);
    }

        // 1. Valider qu’il y a exactement une image principale
        long mainImageCount = request.getImages().stream()
                .filter(img -> !Boolean.TRUE.equals(img.getToDelete()))
                .filter(UpdateProductWithImagesRequest.ImageRequest::getIsMain)
                .count();

        if (mainImageCount == 0) {
            throw new IllegalArgumentException("Il doit y avoir une image principale.");
        }

        if (mainImageCount > 1) {
            throw new IllegalArgumentException("Une seule image peut être marquée comme principale.");
        }

        // 2. Vérifier qu’on ne supprime pas une image principale
        for (UpdateProductWithImagesRequest.ImageRequest imageReq : request.getImages()) {
            if (Boolean.TRUE.equals(imageReq.getToDelete()) && Boolean.TRUE.equals(imageReq.getIsMain())) {
                throw new IllegalArgumentException("Une image principale ne peut pas être supprimée directement. Veuillez d’abord en définir une autre comme principale.");
            }
        }

    // Mise à jour des champs principaux
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    productService.create(product);

    // Gestion des images
    for (UpdateProductWithImagesRequest.ImageRequest imageReq : request.getImages()) {
        Long imageId = imageReq.getId();

        if (imageReq.getToDelete()) {
            if (imageId != null) {
                Image existingImage = imageService.findImageById(imageId);
                if (existingImage == null) {
                    throw new ResourceNotFoundException("Image not found with id " + imageId);
                }
                imageService.deleteImageById(imageId);
            }
            continue;
        }

        if (imageId == null) {
            Image newImage = Image.builder()
                    .url(imageReq.getUrl())
                    .title(imageReq.getTitle())
                    .isMain(imageReq.getIsMain())
                    .product(product)
                    .build();
            imageService.createImage(newImage);
        } else {
            Image existingImage = imageService.findImageById(imageId);
            if (existingImage == null) {
                throw new ResourceNotFoundException("Image not found with id " + imageId);
            }
                existingImage.setUrl(imageReq.getUrl());
                existingImage.setTitle(imageReq.getTitle());
                existingImage.setMain(imageReq.getIsMain());
                imageService.createImage(existingImage);
        }
    }

        Product updatedProduct = productService.findById(id);
        ProductResponseDTO productDTO = productMapper.toProductResponseDTO(updatedProduct);
        ProductSaveResponseDTO response = new ProductSaveResponseDTO("The product has been successfully modified.", productDTO);
        return ResponseEntity.ok(response);
}

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        }

        List<Image> images = imageService.getImagesByProductId(id);

        if (images == null || images.isEmpty()) {
            throw new ResourceNotFoundException("Image not found with id " + id);
        }
        imageService.deleteImageAllByProductId(id);
        productService.delete(id);

        ApiResponse response = new ApiResponse(
                "The product has been successfully deleted." ,
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stock")
    public int getProductStock(@PathVariable Long id) {

        return productService.getStockByProductId(id);

    }
}
