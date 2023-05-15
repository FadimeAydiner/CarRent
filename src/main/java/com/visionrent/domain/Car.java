package com.visionrent.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.EntityGraph;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="t_car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30,nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer doors;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private Integer luggage;

    @Column(nullable = false)
    private String transmission;

    @Column(nullable = false)
    private boolean airConditioning;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double pricePerHour;

    @Column(length = 30,nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private Boolean builtIn=false;


    /*
    orphanRemoval
       eğer bir image silmeye çalışırsak onun bağlı olduğu bir car olacak ve hibernate silmeye izin vermeyecek.
       Ama orphanRemoval ile bir entity'i bağlı olduğu entitylerle birlikte silebiliriz.
     */
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name="car_id")
    private Set<ImageFile> image;
}

