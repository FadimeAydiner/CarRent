package com.visionrent.service;

import com.visionrent.domain.ImageData;
import com.visionrent.domain.ImageFile;
import com.visionrent.dto.ImageFileDTO;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.repository.ImageFileRepository;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service

public class ImageFileService {

    private final ImageFileRepository imageFileRepository;

    /*
        a type of constructor injection
     */
    public ImageFileService(ImageFileRepository imageFileRepository) {
        this.imageFileRepository = imageFileRepository;
    }


    public String saveImage(MultipartFile file){
        String fileName= StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        ImageFile imageFile;

        try{
            ImageData imageData=new ImageData(file.getBytes());
            imageFile= new ImageFile(fileName,file.getContentType(),imageData);
        } catch (IOException e) {
            //todo:we need a custom exception and message here
            throw new RuntimeException(e);
        }

        imageFileRepository.save(imageFile);

        return  imageFile.getId();
    }

    public ImageFile getImageById(String imageId){
        return imageFileRepository.findById(imageId).orElseThrow(()->
         new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE,imageId)));
    }

    public List<ImageFileDTO> getAllImages(){
        List<ImageFile> imageFiles=imageFileRepository.findAll();
        return imageFiles.stream().map(imageFile -> {
            String imageUri= ServletUriComponentsBuilder
                    .fromCurrentContextPath().
                    path("/files/download/")
                    .path(imageFile.getId()).toUriString();
            return new ImageFileDTO(imageFile.getName(),imageUri,imageFile.getType(),imageFile.getLength());
        }).collect(Collectors.toList());

    }


    public void removeById(String id){
        ImageFile imageFile=getImageById(id);
        imageFileRepository.delete(imageFile);
    }

}
