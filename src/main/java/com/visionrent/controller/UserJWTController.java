package com.visionrent.controller;

import com.visionrent.dto.request.LoginRequest;
import com.visionrent.dto.request.RegisterRequest;
import com.visionrent.dto.response.LoginResponse;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.repository.UserRepository;
import com.visionrent.security.jwt.JwtUtils;
import com.visionrent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserJWTController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<VRResponse> registerUser (@Valid @RequestBody RegisterRequest registerRequest){
        userService.saveUser(registerRequest);
        VRResponse response = new VRResponse();
        response.setMessage(ResponseMessage.REGISTER_RESPONSE_MESSAGE);
        response.setSuccess(true);
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

@PostMapping("/login")
    public ResponseEntity<LoginResponse>authenticate(@Valid @RequestBody LoginRequest loginRequest){
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword());

    //burada kullanıcıyı authenticate (doğrulama) yapıyor
    Authentication authentication=authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    //token için UserDetails kullanılıyor
    UserDetails userDetails= (UserDetails) authentication.getPrincipal();

    String jwtToken=jwtUtils.generateToken(userDetails);
    LoginResponse loginResponse=new LoginResponse(jwtToken);

    return new ResponseEntity<>(loginResponse,HttpStatus.OK);

    }




}
