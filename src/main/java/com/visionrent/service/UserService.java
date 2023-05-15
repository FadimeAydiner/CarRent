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
//UserJWTController metotları
    public void saveUser(RegisterRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ConflictException(ErrorMessage.EMAIL_ALREADY_EXISTS_MESSAGE);
        }
        //şifreyi veri tabanına kaydetmeden önce encode işlemi yapıyoruz.
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        //customer role var mı diye bakıyoruz
        Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        //yeni bi kullanıcı oluşturup RegisterRequest nesnesinden gelen verileri kullanıcıya ekliyoruz.
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setAddress(registerRequest.getAddress());
        user.setZipCode(registerRequest.getZipCode());
        user.setRoles(roles);
        //kullanıcıyı veri tabanına kaydediyoruz
        userRepository.save(user);
    }

    //UserController methodları

    public List<UserDTO>getAllUsers(){
        List<User> users=userRepository.findAll();
        List<UserDTO> userDTOS=userMapper.map(users);
        return userDTOS;
    }

    public UserDTO getPrincipal(){
        User currentUser=getCurrentUser();
        UserDTO userDTO=userMapper.userToUserDTO(currentUser);
        System.out.println(currentUser.getId());
        System.out.println(userDTO.getId());
        return userDTO;
    }

    //SecurityContext'ten o an sistemde olan kullanıcı bilgileriniz alıyoruz
    public User getCurrentUser(){

        String email= SecurityUtils.getCurrentUserLogin().orElseThrow(()->
                new ResourceNotFoundException(ErrorMessage.PRINCIPAL_NOT_FOUND_MESSAGE));
        User user=getUserByEmail(email);

        return user;
    }

    public UserDTO getUserById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(
                String.format(ErrorMessage.USER_NOT_FOUND_MESSAGE,id)));
        return userMapper.userToUserDTO(user);
    }



    //delete

    public void removeUserById(Long id)  {
        User user=getById(id);

        //O an sisteme giriş yapmış olan kullanıcının silinmesine izin vermiyoruz
        if(user==getCurrentUser()){
            throw new BadRequestException(ErrorMessage.CURRENT_USER_NOT_DELETE_MESSAGE);
        }

        //kullanıcının builtIn özelliği true ise kullanıcıyı silebiliriz değilse silemeyiz bunun için checkUserBuiltIn(user);metodunu yazdım aşağıda.
        checkUserBuiltIn(user);
        userRepository.deleteById(user.getId());
    }

    public User getById(Long id){
        User user=userRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));

        return user;
    }

    public Page<UserDTO> getUserPage(Pageable pageable){
        Page<User> userPage=userRepository.findAll(pageable);

        return getUserDTOPage(userPage);

    }

    private Page<UserDTO> getUserDTOPage(Page<User> userPage){
       // Page<UserDTO> userDTOPage=userPage.map(userMapper::userToUserDTO);

        //Özel bir functional interface yazıp apply metodunu oberride ediyoruz
        Page<UserDTO> userDTOPage=userPage.map(new Function<User, UserDTO>() {
            @Override
            public UserDTO apply(User user) {
                return userMapper.userToUserDTO(user);
            }
        });

        return userDTOPage;
    }


    //Bu metot sadece sistemde o an bulunan current userı günceller
    // @Transactional sadece bir kullanıcının aynı kullanıcı güncellemesine izin verir.
    //Mesela aynı anda admin ve customer aynı customerı güncelleyemez
    @Transactional
    public void updateUser(UserUpdateRequest userUpdateRequest){
        User user=getCurrentUser();

        //Kullanıcının builtIn özelliği true ise değişikliğe izin vemiyor
       checkUserBuiltIn(user);

       boolean emailExist=userRepository.existsByEmail(userUpdateRequest.getEmail());

       //Current user'ın emaili güncellenmek istenen yeni email ile aynı olmamalı
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
                }else if(roleStr.equals(RoleType.ROLE_CUSTOMER.getName())){
                    Role userRole=roleService.findByType(RoleType.ROLE_CUSTOMER);
                    roles.add(userRole);
                }else{
                    throw new BadRequestException(String.format(ErrorMessage.ROLE_NOT_FOUND_EXCEPTION,roleStr));

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

    public List<User>getUsers(){
        return userRepository.findAll();
    }

}
