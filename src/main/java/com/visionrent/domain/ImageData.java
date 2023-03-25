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
        Specifies that a persistent propery or field should be
        persisted as a large object to a database-supported loarge
        object type.
     */
    @Lob
    private byte[] data;

    /*
        custom constructor to initialize the image data object
        @param data array of bytes
     */
    public ImageData(byte[] data){
        this.data=data;
    }

    public ImageData(Long id){
        this.id=id;
    }
}
