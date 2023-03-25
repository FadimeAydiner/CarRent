package com.visionrent.repository;

import com.visionrent.domain.ImageFile;
import com.visionrent.service.ImageFileService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageFileRepository extends JpaRepository <ImageFile,String > {


}
