package com.visionrent.dto.response;

public abstract class ResponseMessage {

/*
    ContactMessage related response messages
 */
    public static final String CONTACT_MESSAGE_SAVE_RESPONSE = "ContactMessage Successfully Created";
    public static final String CONTACT_MESSAGE_DELETE_RESPONSE = "ContactMessage Successfully Deleted";
     public static final String CONTACT_MESSAGE_UPDATE_RESPONSE = "ContactMessage Successfully Updated";


     //User message related with response messages
     public static final String REGISTER_RESPONSE_MESSAGE="Registration Successfully Done";

     public static final String USER_DELETE_RESPONSE_MESSAGE="User Successfully Deleted";

    public static final String USER_UPDATE_RESPONSE_MESSAGE="User Successfully Updated";

    public static final String USER_PASSWORD_CHANGED_MESSAGE="Password Successfully Changed";

    //image related response messages
    public static final String IMAGE_SAVED_RESPONSE_MESSAGE="ImageFile Successfully Uploaded";

    public static final String IMAGE_DELETE_RESPONSE_MESSAGE="ImageFile Successfully Deleted";

    //car related response messages

    public static final String CAR_SAVED_RESPONSE_MESSAGE="Car Successfully Saved";

    public static final String CAR_UPDATE_RESPONSE_MESSAGE="Car Successfully Updated";
    public static final String CAR_DELETE_RESPONSE_MESSAGE="Car Successfully Deleted";
    public static final String CAR_AVAILABLE_MESSAGE="Car Availability Calculated";



    //reservation related response messages

    public static final String RESERVATION_CREATED_RESPONSE_MESSAGE="Reservation Successfully Created";

    public static final String RESERVATION_UPDATED_RESPONSE_MESSAGE="Reservation Successfully Updated";

}
