package com.visionrent.service;

import com.visionrent.domain.Car;
import com.visionrent.domain.ImageFile;
import com.visionrent.dto.CarDTO;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.ConflictException;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.CarMapper;
import com.visionrent.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CarService {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private ImageFileService imageFileService;

    @Autowired
    private CarMapper carMapper;

    public void saveCar(String imageId, CarDTO carDTO){

        ImageFile imageFile=imageFileService.getImageById(imageId);

        //Bu resmi kullanan araba var mı
        Integer usedCarCount=carRepository.findCarCountByImageId(imageFile.getId());

        if(usedCarCount>0){
            throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
        }
        Car car=carMapper.carDTOToCar(carDTO);
        Set<ImageFile> setOfImFiles=new HashSet<>();
        setOfImFiles.add(imageFile);
        car.setImage(setOfImFiles);

        carRepository.save(car);

    }

    public List<CarDTO> getAllCars(){
        List<Car> carList=carRepository.findAll();
        return carMapper.map(carList);
    }

    public CarDTO findById(Long id){

        Car car=carRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));

        return carMapper.carToCarDTO(car);
    }

    public Car findCarById(Long id) {
        return carRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
    }
    public void updateCar(Long id,String imageId,CarDTO carDTO){
        Car car=findCarById(id);

        if(car.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }

        ImageFile imageFile=imageFileService.getImageById(imageId);

        List<Car> carList=carRepository.findCarsByImageId(imageFile.getId());

        for(Car c:carList){
            if(car.getId().longValue()!=c.getId().longValue())

                throw new ConflictException(ErrorMessage.IMAGE_USED_MESSAGE);
        }

        car.setAge(carDTO.getAge());
        car.setAirConditioning(carDTO.getAirConditioning());
        car.setBuiltIn(carDTO.getBuiltIn());
        car.setDoors(carDTO.getDoors());
        car.setFuelType(carDTO.getFuelType());
        car.setLuggage(carDTO.getLuggage());
        car.setModel(carDTO.getModel());
        car.setPricePerHour(carDTO.getPricePerHour());
        car.setSeats(carDTO.getSeats());
        car.setTransmission(carDTO.getTransmission());
        car.getImage().add(imageFile);

        carRepository.save(car);

    }

    public void removeById(Long id){
        Car car=findCarById(id);

        if(car.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }


        boolean exist=reservationService.existByCar(car);
        if(exist){
            throw new BadRequestException(String.format(ErrorMessage.CAR_USED_BY_RESERVATION_MESSAGE,id));
        }
        carRepository.delete(car);

    }

    public Page<CarDTO> findAllWithPage(Pageable pageable){
        Page<Car> carPage=carRepository.findAll(pageable);
        return carPage.map(car -> carMapper.carToCarDTO(car));

        /*
             return carPage.map(car -> {
            return carMapper.carToCarDTO(car);
        });
         */

    }

}
