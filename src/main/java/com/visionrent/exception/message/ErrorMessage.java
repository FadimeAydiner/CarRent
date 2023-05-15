package com.visionrent.exception.message;

public abstract class ErrorMessage {


    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource with id %d not found";

    public static final  String USER_NOT_FOUND_MESSAGE="User with email %s not found";

    public static final String JWT_TOKEN_MESSAGE="JWT token validation error";

    public static final String EXPIRED_JWT_MESSAGE="JWT expiration time is over error";

    public static final String SIGNATURE_NOT_MATCH_MESSAGE="Signature does not match error";

    public static final String ILLEGAL_ARGUMENT_MESSAGE="Illegal argument error";

    public static final String EMAIL_ALREADY_EXISTS_MESSAGE="Email: %s  already exists";

    public static final String ROLE_NOT_FOUND_EXCEPTION="Role %s not found";

    public static final String PRINCIPAL_NOT_FOUND_MESSAGE="User not found";

    public static final String NOT_PERMITTED_METHOD_MESSAGE="You don't have any permission to change this data";

    public static final String CURRENT_USER_NOT_DELETE_MESSAGE="You are the logged user. You can not remove yourself";

    public static final String PASSWORD_NOT_MATCHED_MESSAGE="Your passwords are not matched";

    public static final String IMAGE_NOT_FOUND_MESSAGE="ImageFile with id %s not found";

    public static final String IMAGE_USED_MESSAGE="ImageFile is used by other car";

    public static final String IMAGE_NOT_SAVED_MESSAGE="ImageFile can not save. You can try again";

    public static final String IMAGE_NOT_DELETED_MESSAGE="ImageFile can not delete.It is used by a car";



    //Reservation

    public static final String RESERVATION_TIME_INCORRECT_MESSAGE="Reservation pick up time or drop off time is not correct";

    public static final String CAR_NOT_AVAILABLE_MESSAGE="Car is not available for selected time";


    public static final String RESERVATION_STATUS_CAN_NOT_CHANGED_MESSAGE="Reservation can not be updated for CANCELLED or DONE reservation";



    public static final String CAR_USED_BY_RESERVATION_MESSAGE="Car can not be deleted. Car is used by a reservation";

    //Payment
    public static final String EXPIRATION_TIME_INCORRECT_MESSAGE="Expiration time is not correct";
    public static final String PAYMENT_ALREADY_DONE_MESSAGE="Payment is already done with reservation id %d";


    //Excel Report
    public static final String EXCEL_REPORT_ERROR_MESSAGE="Error occurred while generating excel report";
}
