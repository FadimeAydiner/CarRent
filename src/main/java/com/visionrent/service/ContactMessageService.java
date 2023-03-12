package com.visionrent.service;

import com.visionrent.domain.ContactMessage;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
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

    public void saveMessage(ContactMessage contactMessage){
        contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessage>getAll(){
        return contactMessageRepository.findAll();
    }

    //Program to interface not to implementation
    //https://medium.com/javarevisited/oop-good-practices-coding-to-the-interface-baea84fd60d3
    public ContactMessage getContactMessage(Long id){
        return contactMessageRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE,id)));
    }

    public void deleteContactMessage(Long id){
        //first of all we need to find contactMessage by id information
        ContactMessage contactMessage = getContactMessage(id);
        contactMessageRepository.delete(contactMessage);
    }

    public void updateContactMessage(Long id,ContactMessage contactMessage){
        ContactMessage foundContactMessage=getContactMessage(id);
        foundContactMessage.setName(contactMessage.getName());
        foundContactMessage.setSubject(contactMessage.getSubject());
        foundContactMessage.setBody(contactMessage.getBody());
        foundContactMessage.setEmail(contactMessage.getEmail());

        contactMessageRepository.save(foundContactMessage);
    }

    public Page<ContactMessage> getAll(Pageable pageable){
        return contactMessageRepository.findAll(pageable);
    }

}
