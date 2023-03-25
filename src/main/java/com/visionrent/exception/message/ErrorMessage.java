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


}
