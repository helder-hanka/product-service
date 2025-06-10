package com.ff.products_service.controller;

import com.ff.products_service.dto.*;
import com.ff.products_service.entity.Image;
import com.ff.products_service.entity.Product;
import com.ff.products_service.service.ImageService;
import com.ff.products_service.service.ProductService;
import com.ff.products_service.utils.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ImageService imageService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Product not found");
        }
        return ResponseEntity.ok(ResponseBuilder.success("Products found", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {

        Product product = productService.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return ResponseEntity.ok(ResponseBuilder.success("Product found", product));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProductWithImageUrls(@Valid @RequestBody ProductWithImagesRequest request) {
        // 1. Valider qu’il y a exactement une image principale
        ImageValidationUtils.validateSingleMainImage(request.getImages());

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

        ProductResponseDTO productDTO = productMapper.toProductResponseDTO(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseBuilder.created("Product has been successfully save.", productDTO));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductWithImagesRequest request) {
    Product product = productService.findById(id);
    if (product == null) {
        throw new ResourceNotFoundException("Product not found with id " + id);
    }

        // 1. Valider qu’il y a exactement une image principale
        ImageValidationUtils.validateSingleMainImage(request.getImages());
        // 2. Vérifier qu’on ne supprime pas une image principale
        ImageValidationUtils.validateNoMainImageBeingDeleted(request.getImages());

    // Mise à jour des champs principaux
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    productService.create(product);

    // Gestion des images
    for (UpdateProductWithImagesRequest.ImageRequest imageReq : request.getImages()) {
        Long imageId = imageReq.getId();

        if (imageReq.getToDelete()) {            if (imageId != null) {
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
        return ResponseEntity.status(HttpStatus.OK).body(ResponseBuilder.success("Product has been successfully modified", updatedProduct));
}

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
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

        return ResponseEntity.ok(ResponseBuilder.success("Product has been successfully deleted",null ));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getProductStock(@PathVariable Long id) {

       Product product = productService.findById(id);
       if (product == null) {
           throw new ResourceNotFoundException("Product not found with id " + id);
       }
       return ResponseEntity.ok(ResponseBuilder.success("Product stock", product.getStock()));
    }
}
