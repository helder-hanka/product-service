package com.ff.products_service.service;

import com.ff.products_service.entity.Image;
import com.ff.products_service.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    @Autowired
    ImageRepository imageRepository;

    public List<Image> getImagesByProductId(Long productId){
        return imageRepository.findByProductId(productId);
    }

    public void createImage(Image image){
        imageRepository.save(image);
    }

    public Image updateImage(Long id, Image newImage){
        return imageRepository.findById(id).map(image -> {
            image.setUrl(newImage.getUrl());
            image.setTitle(newImage.getTitle());
            return imageRepository.save(image);
        }).orElse(null);
    }

    public void deleteImageById(Long id){
        imageRepository.deleteById(id);
    }

    public void deleteImageAllByProductId(Long productId){
        List<Image> images = imageRepository.findByProductId(productId);
        imageRepository.deleteAll(images);
    }
}
