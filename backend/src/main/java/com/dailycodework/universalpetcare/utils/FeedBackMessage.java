package com.dailycodework.universalpetcare.utils;

public class FeedBackMessage {

          /*======================== Start User API=====================================*/
          public static final String CREATE_USER_SUCCESS = "User account created successfully";
          public static final String DELETE_USER_SUCCESS = "User account deleted successfully";
          public static final String USER_UPDATE_SUCCESS = "User updated successfully";
          public static final String USER_FOUND = "User found";
          public static final String USER_NOT_FOUND = "Sorry, user not found";
          public static final String NO_USER_FOUND = "Oops!, no user found with : ";
          public static final String LOCKED_ACCOUNT_SUCCESS = "Account locked successfully";
          public static final String UNLOCKED_ACCOUNT_SUCCESS = "Account unlocked successfully";
          /*======================== End User API=====================================*/



    /*======================== Start Password API=====================================*/
    public static final String PASSWORD_CHANGE_SUCCESS = "Password changed success!, you can now close this form.";
    public static final String PASSWORD_RESET_EMAIL_SENT = "A link was sent to your email, please check your to complete your password request";
    public static final String MISSING_PASSWORD = "Missing token or password";
    public static final String INVALID_RESET_TOKEN = "Invalid password reset token";
    public static final String INVALID_EMAIL = "Please, enter the email that is associated with your account." ;
    public static final String PASSWORD_RESET_SUCCESS = "Your password has been reset successfully!" ;

    /*======================== End Password API=====================================*/


    /*======================== Start Appointment API=====================================*/
    public static final String APPOINTMENT_UPDATE_SUCCESS = "Appointment updated successfully";
    public static final String APPOINTMENT_APPROVED_SUCCESS = "Appointment approved successfully";
    public static final String APPOINTMENT_DECLINED_SUCCESS = "Appointment declined successfully";
    public static final String APPOINTMENT_CANCELLED_SUCCESS = "Appointment cancelled successfully";
    public static final String APPOINTMENT_DELETE_SUCCESS = "Appointment deleted successfully";
    public static final String APPOINTMENT_BOOKED_SUCCESS = "Appointment booked successfully";
    public static final String APPOINTMENT_FOUND = "Appointment found";
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found";
    public static final String APPOINTMENT_UPDATE_NOT_ALLOWED = "Cannot update or cancel appointment";
    public static final String OPERATION_NOT_ALLOWED = "Operation not allowed";
    /*======================== End Appointment API=====================================*/



    /*======================== Start Pet API=====================================*/
    public static final String PET_ADDED_SUCCESS  = "Pet added to appointment successfully";
    public static final String PET_UPDATE_SUCCESS = "Pet updated successfully";
    public static final String PET_DELETE_SUCCESS = "Pet deleted successfully";
    public static final String PET_FOUND = "Pet (s) found";
    public static final String PET_NOT_FOUND = "Pet not found";

    /*======================== End Pet API=====================================*/


    /*======================== Start Review API=====================================*/
    public static final String REVIEW_NOT_ALLOWED = "Sorry, only patients that have a completed appointments with this veterinarian can leave a review";
    public static final String ALREADY_REVIEWED = "You have already rated this veterinarian, you may edit your previous review";
    public static final String CANNOT_REVIEW = "Veterinarians can not review themselves";
    public static final String VET_OR_PATIENT_NOT_FOUND = "Veterinarian or Patient not found";
    public static final String NO_VETS_AVAILABLE = "No veterinarians available for the requested date and time";
    public static final String REVIEW_SUBMIT_SUCCESS = "Review submitted successfully";
    public static final String REVIEW_UPDATE_SUCCESS = "Review updated successfully";
    public static final String REVIEW_DELETE_SUCCESS = "Review deleted successfully";
    public static final String REVIEW_FOUND = "Review (s)  found";


    /*======================== End Review API=====================================*/
    public static final String NOT_ALLOWED = "You must have a completed appointment with this veterinarian to leave a review";


    /*======================== Start General feedback =====================================*/
    public static final String SUCCESS = "Success!";
    public static final String RESOURCE_FOUND = "Resource found";
    public static final String SENDER_RECIPIENT_NOT_FOUND = "sender or recipient not found";
    public static final String ERROR = "Error occurred" ;
    public static final String RESOURCE_NOT_FOUND = "Resource not found"; ;
    /*======================== End general feedback =====================================*/


    /*======================== Start authentication feedback =====================================*/
    public static final String EMPTY_PASSWORD = "All fields are required" ;
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String PASSWORD_MISMATCH = "Password confirmation mismatch";
    public static final String AUTHENTICATION_SUCCESS = "Authentication Successful" ;
    public static final String ACCOUNT_DISABLED = "Sorry, your account is disabled, please contact the service desk";
    public static final Object INVALID_PASSWORD = "Invalid username or password";
    public static final String UNAUTHORIZED = "Please, login to submit a review.";

    /*======================== End authentication feedback =====================================*/

    /*======================== Start Token API =====================================*/
    public static final String INVALID_TOKEN = "INVALID";
    public static final String TOKEN_ALREADY_VERIFIED = "VERIFIED";
    public static final String EXPIRED_TOKEN = "EXPIRED";
    public static final String VALID_VERIFICATION_TOKEN = "VALID";
    public static final String TOKEN_VALIDATION_ERROR = "Token validation error";
    public static final String TOKEN_SAVED_SUCCESS = "Verification token saved successfully";
    public static final String TOKEN_DELETE_SUCCESS = "User token deleted successfully";
    public static final String INVALID_VERIFICATION_TOKEN = "Invalid verification token";
    public static final String NEW_VERIFICATION_TOKEN_SENT = "A new verification link has been sent to your email. Please check to complete your registration.";

    /*======================== End Token API =====================================*/

    /*======================== Start Photo API =====================================*/
    public static final String PHOTO_UPDATE_SUCCESS = "Photo updated successfully"; ;
    public static final String PHOTO_REMOVE_SUCCESS = "Photo updated successfully";
    /*======================== End Photo API =====================================*/


    /*======================== Start Photo API =====================================*/
    public static final String ROLE_NOT_FOUND = "Role not found";

    /*======================== End Photo API =====================================*/

}
