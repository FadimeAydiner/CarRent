package com.visionrent.service;

import com.visionrent.domain.ContactMessage;
import com.visionrent.dto.ContactMessageDTO;
import com.visionrent.dto.request.ContactMessageRequest;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.ContactMessageMapper;
import com.visionrent.repository.ContactMessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactMessageService {

    /**
     * this is a constructor injection example
     */
    private ContactMessageRepository contactMessageRepository;

    private ContactMessageMapper contactMessageMapper;
    public void saveMessage(ContactMessageRequest contactMessageRequest){
        ContactMessage contactMessage = contactMessageMapper.contactMessageRequestToContactMessage(contactMessageRequest);

        contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessageDTO>getAll(){
        List<ContactMessage> contactMessageList=contactMessageRepository.findAll();
        List<ContactMessageDTO>contactMessageDTOList = contactMessageMapper.map(contactMessageList);


        return contactMessageDTOList;
    }

    //Program to interface not to implementation
    //https://medium.com/javarevisited/oop-good-practices-coding-to-the-interface-baea84fd60d3
    public ContactMessageDTO getContactMessage(Long id){

        ContactMessage contactMessage= contactMessageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));
        ContactMessageDTO contactMessageDTO=contactMessageMapper.contactMessageToDTO(contactMessage);
    return contactMessageDTO;
    }

    public void deleteContactMessage(Long id){
        //first of all we need to find contactMessage by id information
        ContactMessageDTO contactMessageDTO = getContactMessage(id);
        ContactMessage contactMessage=contactMessageMapper.contactMessageDTOtoContactMessage(contactMessageDTO);
        contactMessageRepository.delete(contactMessage);
    }

    public void updateContactMessage(Long id,ContactMessageRequest contactMessageRequest){
        ContactMessageDTO contactMessageDTO=contactMessageMapper.contactMessageRequestToContactMessageDTO(contactMessageRequest);
        ContactMessageDTO foundContactMessageDTO=getContactMessage(id);
        foundContactMessageDTO.setName(contactMessageDTO.getName());
        foundContactMessageDTO.setSubject(contactMessageDTO.getSubject());
        foundContactMessageDTO.setBody(contactMessageDTO.getBody());
        foundContactMessageDTO.setEmail(contactMessageDTO.getEmail());

        ContactMessage foundContactMessage=contactMessageMapper.contactMessageDTOtoContactMessage(foundContactMessageDTO);
        contactMessageRepository.save(foundContactMessage);
    }

    public Page<ContactMessageDTO> getAll(Pageable pageable){
        Page<ContactMessage> contactMessagePage=contactMessageRepository.findAll(pageable);
        Page<ContactMessageDTO> contactMessageDTOS=getPageDTO(contactMessagePage);
        return contactMessageDTOS;
    }


    private Page<ContactMessageDTO> getPageDTO(Page<ContactMessage> contactMessagePage){
        return contactMessagePage.map(contactMessage -> contactMessageMapper.contactMessageToDTO(contactMessage));
    }

}
