package com.visionrent.controller;

import com.visionrent.dto.UserDTO;
import com.visionrent.dto.request.AdminUserUpdateRequest;
import com.visionrent.dto.request.UpdatePasswordRequest;
import com.visionrent.dto.request.UserUpdateRequest;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.validation.Valid;
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
    //authorization implemented here
    @PreAuthorize("(hasRole('ADMIN'))")
    @GetMapping("/auth/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers=userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    //http://localhost:8084/user
    //getting current user information
   @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @GetMapping()
    public ResponseEntity<UserDTO> getUser(){
        UserDTO userDTO=userService.getPrincipal();
        return ResponseEntity.ok(userDTO);
    }

     //http://localhost:8084/user/1/auth
    //Admin create a request to get a user with id
     @PreAuthorize("(hasRole('ADMIN'))")
    @GetMapping("{id}/auth")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        UserDTO userDTO=userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }
    @PreAuthorize("(hasRole('ADMIN'))")
    @DeleteMapping("/{id}/auth")
    public ResponseEntity<VRResponse> deleteUser(@PathVariable Long id){

        userService.removeUserById(id);
        VRResponse response=new VRResponse();
        response.setMessage(ResponseMessage.USER_DELETE_RESPONSE_MESSAGE);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("(hasRole('ADMIN'))")
    //http://localhost:8084/user/auth/pages?page=0&size=3&sort=id&direction=DESC
    @GetMapping("/auth/pages")
    public ResponseEntity<Page<UserDTO>> getAllUsersByPage(@RequestParam("page") int page,
                                                           @RequestParam("size")int size,
                                                           @RequestParam("sort")String prop,
                                                           @RequestParam(value="direction",required = false,
                                                           defaultValue = "DESC")Direction direction){
        Pageable pageable= PageRequest.of(page,size, Sort.by(direction,prop));
        Page<UserDTO> userDTOPage=userService.getUserPage(pageable);
        return ResponseEntity.ok(userDTOPage);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PutMapping
    public ResponseEntity<VRResponse> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest){
       userService.updateUser(userUpdateRequest);
        VRResponse response=new VRResponse();
        response.setSuccess(true);
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        return ResponseEntity.ok(response);
    }

    //we are updating the logged-in user
    @PutMapping("/auth/builtIn")
    public ResponseEntity<UserDTO> updateBuiltIn(@RequestParam("id") Long id,@RequestParam("builtIn") boolean builtIn){
        UserDTO userDTO=userService.updateBuiltInProperty(id,builtIn);
        return ResponseEntity.ok(userDTO);
    }


    //http://localhost:8084/user/2/auth
    //we are updating the user according to its id
    @PreAuthorize("(hasRole('ADMIN'))")
    @PutMapping("/{id}/auth")
    public ResponseEntity<VRResponse> updateUserAuth(@PathVariable Long id,
                                                     @Valid @RequestBody AdminUserUpdateRequest adminUserUpdateRequest){

        userService.updateUserAuth(id,adminUserUpdateRequest);
        VRResponse response=new VRResponse();
        response.setSuccess(true);
        response.setMessage(ResponseMessage.USER_UPDATE_RESPONSE_MESSAGE);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PatchMapping("/auth")
    public ResponseEntity<VRResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
        userService.updatePassword(updatePasswordRequest);
        VRResponse response=new VRResponse();
        response.setSuccess(true);
        response.setMessage(ResponseMessage.USER_PASSWORD_CHANGED_MESSAGE);
        return ResponseEntity.ok(response);
    }

}
