package com.visionrent.controller;

import com.visionrent.dto.UserDTO;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    All endpoints for user management except login and register
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    //http://localhost:8084/user/auth/all
    @GetMapping("/auth/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers=userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    //http://localhost:8084/user
    //getting current user information
    @GetMapping()
    public ResponseEntity<UserDTO> getUser(){
        UserDTO userDTO=userService.getPrincipal();
        return ResponseEntity.ok(userDTO);
    }

     //http://localhost:8084/user/1/auth
    //Admin create a request to get a user with id
    @GetMapping("{id}/auth")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        UserDTO userDTO=userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}/auth")
    public ResponseEntity<VRResponse> deleteUser(@PathVariable Long id){

        userService.removeUserById(id);
        VRResponse response=new VRResponse();
        response.setMessage(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/pages")
    public ResponseEntity<Page<UserDTO>> getAllUsersByPage(@RequestParam("page") int page,
                                                           @RequestParam("size")int size,
                                                           @RequestParam("sort")String prop,
                                                           @RequestParam(value="direction",required = false,
                                                           defaultValue = "DESC")Direction direction){
        Pageable pageable= PageRequest.of(page,size, Sort.by(direction,prop));
        //Page<UserDTO> userDTOPage=userService

        return null;
    }



}
