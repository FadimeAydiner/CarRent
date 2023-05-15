package com.visionrent.mapper;

import com.visionrent.domain.Car;
import com.visionrent.domain.ImageFile;
import com.visionrent.dto.CarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CarMapper {

    List<CarDTO> map(List<Car> cars);

    //Map işlemi sırasında targetta belirtilen değişkeni yoksayıyoruz.
    //Çünkü carDTO'da olan image Car sınıfında  @OneToMany ilişkisi ile ImageFile tablosuna kaydediliyor.
    //Dolayısıyla Car tablosunda image kaydedilecek alan olmadığından CarDTo'dan gelen image'i ignore yani görmezden geliyoruz.
    //Diğer alanları map ediyoruz.
    @Mapping(target = "image",ignore = true)
    Car carDTOToCar(CarDTO carDTO);


    //Yukarıdaki işlemin tersini yapıyoruz. Car nesnesini CarDTO'a çevireceğiz.Ancak Car'da image yokkan CarDTO da var.
    //@Named verilen qualifiedByName ile tetiklenir. Bir method ya da bean'e map yapacağımız zaman kullanılır.
    //qualifiedByName ile çalışan getImageIds()'den image bilgisi alınır CarDTO'ya iletilir.
    @Mapping(source = "image",target="image",qualifiedByName = "getImageAsString")
    CarDTO carToCarDTO(Car car);

    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles){
        Set<String> imgs=new HashSet<>();
        imgs=imageFiles.stream().map(imFile->imFile.getId().toString()).collect(Collectors.toSet());

        return imgs;
    }
}
