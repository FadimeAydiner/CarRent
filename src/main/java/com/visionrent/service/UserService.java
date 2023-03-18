package com.visionrent.service;

import com.visionrent.domain.Role;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.RoleType;
import com.visionrent.dto.UserDTO;
import com.visionrent.dto.request.RegisterRequest;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.ConflictException;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.UserMapper;
import com.visionrent.repository.UserRepository;
import com.visionrent.security.SecurityUtils;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;

    private RoleService roleService;

    private  PasswordEncoder passwordEncoder;

    private  UserMapper userMapper;

    public UserService(UserRepository userRepository,RoleService roleService,@Lazy PasswordEncoder passwordEncoder,
                       UserMapper userMapper){
        this.userRepository=userRepository;
        this.roleService=roleService;
        this.passwordEncoder=passwordEncoder;
        this.userMapper=userMapper;
    }

    public User getUserByEmail (String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException(
                        String.format(ErrorMessage.USER_NOT_FOUND_MESSAGE,email)));
    }

    public void saveUser(RegisterRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(ErrorMessage.EMAIL_ALREADY_EXISTS_MESSAGE);
        }
        //we have to encode our password before saving into DB.
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        //we rae searching customer role in db
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        //builder design pattern -> @Builder
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);
        userRepository.save(user);
    }


    public List<UserDTO>getAllUsers(){
        List<User> users=userRepository.findAll();
        List<UserDTO> userDTOS=userMapper.map(users);
        return userDTOS;
    }

    public UserDTO getUserById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(
                String.format(ErrorMessage.USER_NOT_FOUND_MESSAGE,id)));
        return userMapper.userToUserDTO(user);
    }

    public UserDTO getPrincipal(){
        User currentUser=getCurrentUser();
        UserDTO userDTO=userMapper.userToUserDTO(currentUser);
        return userDTO;
    }

    /*
         From security context we are fetching current user information
     */
    public User getCurrentUser(){

        String email= SecurityUtils.getCurrentUserLogin().orElseThrow(()->
                new ResourceNotFoundException(ErrorMessage.PRINCIPAL_NOT_FOUND_MESSAGE));
        User user=getUserByEmail(email);

        return user;
    }

    public User getById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));

        return user;
    }

    //delete

    //TODO the login user should not be deleted
    // add an algorithm if the user that wanted to be deleted must not be logged in user
    public void removeUserById(Long id)  {
        User user=getById(id);
        //TODO your own your repo you can this function and add more end points
        // to change the built in property in db
        // but check the entity class for default value

       //we are checking if we are allowed to delete this user
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        userRepository.deleteById(user.getId());
    }
}
