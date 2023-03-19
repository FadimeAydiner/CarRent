package com.visionrent.service;

import com.visionrent.domain.Role;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.RoleType;
import com.visionrent.dto.UserDTO;
import com.visionrent.dto.request.AdminUserUpdateRequest;
import com.visionrent.dto.request.RegisterRequest;
import com.visionrent.dto.request.UpdatePasswordRequest;
import com.visionrent.dto.request.UserUpdateRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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

    /**
     * From security context we are fetching current user information
     * @return
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
        //TODO your own repo you can change this function and add more endpoints
        // to change the built in property in DB
        // but check the entity class for default value

        //Current user
        if(user==getCurrentUser()){
            throw new BadRequestException(ErrorMessage.CURRENT_USER_NOT_DELETE_MESSAGE);
        }

       //we are checking if we are allowed to delete this user
      /*  if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }*/
        checkUserBuiltIn(user);
        userRepository.deleteById(user.getId());
    }

    public Page<UserDTO> getUserPage(Pageable pageable){
        Page<User> userPage=userRepository.findAll(pageable);

        return getUserDTOPage(userPage);

    }

    /*
        A custom method that maps Page<User> userPage to Page<UserDTO> userDTOPage
        @param userPage page of Users
        @return page of userDTOs
     */
    private Page<UserDTO> getUserDTOPage(Page<User> userPage){
       // Page<UserDTO> userDTOPage=userPage.map(userMapper::userToUserDTO);

        //We are writing a custom functional interface and we are overriding apply method here.
        Page<UserDTO> userDTOPage=userPage.map(new Function<User, UserDTO>() {
            @Override
            public UserDTO apply(User user) {
                return userMapper.userToUserDTO(user);
            }
        });

        return userDTOPage;
    }


    //this is updating just current user
    // @Transactional is used to just allow one user to update the same user at the same time
    @Transactional
    public void updateUser(UserUpdateRequest userUpdateRequest){
        User user=getCurrentUser();

        // TODO please move this code part and create a private custom method and call this method here
      /*
       if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
       */
       checkUserBuiltIn(user);

       boolean emailExist=userRepository.existsByEmail(userUpdateRequest.getEmail());

       //no duplication
       if(emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())){
           throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS_MESSAGE,userUpdateRequest.getEmail()));
       }

       userRepository.update(user.getId(), userUpdateRequest.getFirstName(),
                                            userUpdateRequest.getLastName(),
                                           userUpdateRequest.getPhoneNumber(),
                                            userUpdateRequest.getEmail(), userUpdateRequest.getAddress(),
                                           userUpdateRequest.getZipCode());
    }

    private void checkUserBuiltIn(User user){
        if(user.getBuiltIn()){
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }
    public UserDTO updateBuiltInProperty(Long id,boolean builtIn){
        User user=getById(id);

        if(user.getBuiltIn()!=builtIn){
            user.setBuiltIn(builtIn);
        }
        userRepository.save(user);
        UserDTO userDTO=userMapper.userToUserDTO(user);
        return userDTO;
    }


    public void updateUserAuth(Long id, AdminUserUpdateRequest adminUserUpdateRequest){
        User user=getById(id);

        checkUserBuiltIn(user);

        boolean emailExist=userRepository.existsByEmail(adminUserUpdateRequest.getEmail());
        if(emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())){
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXISTS_MESSAGE,adminUserUpdateRequest.getEmail()));
        }

        if(adminUserUpdateRequest.getPassword()==null){
            adminUserUpdateRequest.setPassword(user.getPassword());
        }else{
            String encodedPassword=passwordEncoder.encode(adminUserUpdateRequest.getPassword());
            adminUserUpdateRequest.setPassword(encodedPassword);
        }

        Set<String> userStrRoles=adminUserUpdateRequest.getRoles();
        Set<Role> roles=convertRoles(userStrRoles);

        user.setFirstName(adminUserUpdateRequest.getFirstName());
        user.setLastName(adminUserUpdateRequest.getLastName());
        user.setEmail(adminUserUpdateRequest.getEmail());
        user.setPassword(adminUserUpdateRequest.getPassword());
        user.setAddress(adminUserUpdateRequest.getAddress());
        user.setZipCode(adminUserUpdateRequest.getZipCode());
        user.setPhoneNumber(adminUserUpdateRequest.getPhoneNumber());
        user.setRoles(roles);
        user.setBuiltIn(adminUserUpdateRequest.getBuiltIn());
        userRepository.save(user);
    }
    private Set<Role> convertRoles(Set<String> pRoles){
       //TODO we do not have any custom exception that handles the wrong type of role entry
        Set<Role> roles=new HashSet<>();
        if(pRoles==null){
            Role userRole=roleService.findByType(RoleType.ROLE_CUSTOMER);
            roles.add(userRole);
        }
        else{
            pRoles.forEach(roleStr->{
                if(roleStr.equals(RoleType.ROLE_ADMIN.getName())){
                    Role adminRole=roleService.findByType(RoleType.ROLE_ADMIN);
                    roles.add(adminRole);
                }else{
                    Role userRole=roleService.findByType(RoleType.ROLE_CUSTOMER);
                    roles.add(userRole);
                }
            });
        }

        return roles;
    }


    public void updatePassword(UpdatePasswordRequest updatePasswordRequest){
        User user=getCurrentUser();

        checkUserBuiltIn(user);

        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())){
            throw new BadRequestException(ErrorMessage.PASSWORD_NOT_MATCHED_MESSAGE);
            }

        String hashedPassword=passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

}
