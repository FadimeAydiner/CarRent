package com.visionrent.controller;


import com.visionrent.domain.ContactMessage;
import com.visionrent.dto.ContactMessageDTO;
import com.visionrent.dto.request.ContactMessageRequest;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.mapper.ContactMessageMapper;
import com.visionrent.repository.ContactMessageRepository;
import com.visionrent.service.ContactMessageService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.PublicKey;
import java.util.List;

@RestController
@RequestMapping("/contactmessage")
@AllArgsConstructor
public class ContactMessageController {



    private ContactMessageService contactMessageService;
  //  private final ContactMessageRepository contactMessageRepository;


    @PostMapping("/visitors")
    public ResponseEntity<VRResponse> createMessage(@Valid @RequestBody ContactMessageRequest contactMessageRequest){

       contactMessageService.saveMessage(contactMessageRequest);

        //VRResponse response = new VRResponse("you made it",true);
        VRResponse response = new VRResponse(ResponseMessage.CONTACT_MESSAGE_SAVE_RESPONSE,true);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ContactMessageDTO>>getAllContactMessage(){
        List<ContactMessageDTO>contactMessageDTOList = contactMessageService.getAll();
        return ResponseEntity.ok(contactMessageDTOList);

    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<VRResponse> deleteContactMessage(@PathVariable Long id){
        contactMessageService.deleteContactMessage(id);
        VRResponse response = new VRResponse(ResponseMessage.CONTACT_MESSAGE_DELETE_RESPONSE,true);

        return ResponseEntity.ok(response);
    }

    //http://localhost:8084/contactmessage/request?id=6
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/request")
    public ResponseEntity<ContactMessageDTO>  getRequestWithRequestParam(@RequestParam("id") Long id){
        ContactMessageDTO contactMessageDTO=contactMessageService.getContactMessage(id);

        return ResponseEntity.ok(contactMessageDTO);

    }

    //http://localhost:8084/contactmessage/6
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<ContactMessageDTO> getRequestWithPath(@PathVariable Long id){
        ContactMessageDTO contactMessageDTO=contactMessageService.getContactMessage(id);

        return ResponseEntity.ok(contactMessageDTO);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<VRResponse> updateContactMessage(@PathVariable Long id,@Valid @RequestBody ContactMessageRequest contactMessageRequest){


        contactMessageService.updateContactMessage(id,contactMessageRequest);
        VRResponse response=new VRResponse(ResponseMessage.CONTACT_MESSAGE_UPDATE_RESPONSE,true);
        return ResponseEntity.ok(response);

    }


    //http://localhost:8084/contactmessage/pages?page=0&size=3&sort=id&direction=DESC
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pages")
    public ResponseEntity<Page<ContactMessageDTO>> getAllContactMessageWithPage(@RequestParam("page") int page,
                                                                                @RequestParam("size") int size,
                                                                                @RequestParam("sort") String prop,
                                                                                @RequestParam(value="direction",
                                                                                              required = false,
                                                                                              defaultValue="DESC")Direction direction){

        Pageable pageable= PageRequest.of(page, size, Sort.by(direction,prop));

        Page<ContactMessageDTO> contactMessageDTOS=contactMessageService.getAll(pageable);


        return ResponseEntity.ok(contactMessageDTOS);
    }



}
