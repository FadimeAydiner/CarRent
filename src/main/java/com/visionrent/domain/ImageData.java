package com.visionrent.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="t_imagedata")
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        Lob
        Database de yüksek boyutlu veri saklamamızı sağlayan veri tipidir.
        Veriyi btye dizisi halinde saklar
     */
    @Lob
    private byte[] data;

    public ImageData(byte[] data){
        this.data=data;
    }

    public ImageData(Long id){
        this.id=id;
    }
}
