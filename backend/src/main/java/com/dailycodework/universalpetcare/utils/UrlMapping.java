package com.dailycodework.universalpetcare.utils;

public class UrlMapping {
    public static final String API = "/api/v1" ;
    public static final String USERS = API+"/users" ;
    public static final String REGISTER_USER = "/register";
    public static final String UPDATE_USER = "/user/{userId}/update";
    public static final String GET_USER_BY_ID = "/user/{userId}";
    public static final String DELETE_USER_BY_ID ="/user/{userId}/delete";
    public static final String GET_ALL_USERS = "/all-users";
    public static final String AGGREGATE_BY_STATUS = "/account/aggregated-by-status";
    public static final String LOCK_USER_ACCOUNT = "/account/{userId}/lock-user-account";
    public static final String UNLOCK_USER_ACCOUNT = "/account/{userId}/unLock-user-account";



    public static final String COUNT_ALL_USERS = "/count/users" ;
    public static final String COUNT_ALL_VETS = "/count/veterinarians" ;
    public static final String COUNT_ALL_PATIENTS ="/count/patients" ;
    public static final String AGGREGATE_USERS = "/aggregated-users" ;


    

    /*========================== Start Appointment API =================================*/
    public static final String APPOINTMENTS = API+"/appointments";
    public static final String ALL_APPOINTMENT = "/all" ;
    public static final String BOOK_APPOINTMENT = "/book-appointment" ;
    public static final String GET_APPOINTMENT_BY_ID = "/appointment/{id}/fetch/appointment" ;
    public static final String GET_APPOINTMENT_BY_NO = "/appointment/{appointmentNo}/appointment" ;
    public static final String DELETE_APPOINTMENT = "/appointment/{id}/delete" ;
    public static final String UPDATE_APPOINTMENT = "/appointment/{id}/update";
    public static final String CANCEL_APPOINTMENT = "/appointment/{id}/cancel";
    public static final String APPROVE_APPOINTMENT = "/appointment/{id}/approve";
    public static final String DECLINE_APPOINTMENT = "/appointment/{id}/decline";
    public static final String COUNT_APPOINTMENT = "/count/appointments";
    public static final String GET_APPOINTMENT_SUMMARY = "/summary/appointments-summary" ;

    /*========================= End Appointment API ================================*/
    

    /*============================ Start Pet API ===================================*/
    public static final String PETS =API+"/pets" ;
    public static final String SAVE_PETS_FOR_APPOINTMENT ="/save-pets" ;
    public static final String GET_PET_BY_ID = "/pet/{petId}/pet" ;
    public static final String DELETE_PET_BY_ID = "/pet/{petId}/delete" ;
    public static final String UPDATE_PET = "/pet/{petId}/update";
    public static final String GET_PET_TYPES = "/get-types" ;
    public static final String GET_PET_COLORS = "/get-pet-colors" ;
    public static final String GET_PET_BREEDS = "/get-pet-breeds" ;
    /*============================ End Pet API ===================================*/


    /*============================ Start Photo API ===================================*/
    public static final String PHOTOS = API+"/photos" ;
    public static final String UPLOAD_PHOTO = "/photo/upload" ;
    public static final String UPDATE_PHOTO = "/photo/{photoId}/update"; ;
    public static final String DELETE_PHOTO = "/photo/{photoId}/{userId}/delete" ;
    public static final String GET_PHOTO_BY_ID = "/photo/{photoId}/photo" ;
    /*============================ End Photo API ===================================*/

    /*============================ Start Review API ===================================*/
    public static final String REVIEWS = API+"/reviews" ;
    public static final String SUBMIT_REVIEW = "/submit-review" ;
    public static final String GET_USER_REVIEWS = "/user/{userId}/reviews" ;
    public static final String UPDATE_REVIEW = "/review/{reviewId}/update" ;
    public static final String DELETE_REVIEW = "/review/{reviewId}/delete" ;
    public static final String GET_AVERAGE_RATING ="/vet/{vetId}/get-average-rating";
    /*============================ End Review API ===================================*/

    /*============================ Start Veterinarian API ===================================*/
    public static final String VETERINARIANS = API+"/veterinarians";
    public static final String GET_ALL_VETERINARIANS = "/get-all-veterinarians";
    public static final String SEARCH_VETERINARIAN_FOR_APPOINTMENT = "/search-veterinarian";
    public static final String GET_ALL_SPECIALIZATIONS = "vet/get-all-specialization" ;
    public static final String VET_AGGREGATE_BY_SPECIALIZATION = "vet/get-by-specialization" ;
    /*============================ End Veterinarian API ===================================*/

    /*============================ Start Auth API ===================================*/
    public static final String CHANGE_PASSWORD = "/user/{userId}/change-password";
    public static final String AUTH = "/api/v1/auth" ;
    public static final String LOGIN = "/login" ;
    public static final String REQUEST_PASSWORD_RESET = "/request-password-reset" ;
    public static final String RESET_PASSWORD = "/reset-password" ;
    /*============================ End Auth API ===================================*/


    /*============================ Start Patient ===================================*/
    public static final String PATIENTS =API+"/patients";
    public static final String GET_ALL_PATIENTS = "/get-all-patients";
    /*============================ End Patient ===================================*/

    /*============================ Start verification token ===================================*/
    public static final String TOKEN_VERIFICATION = API+"/verification";
    public static final String VALIDATE_TOKEN = "/validate-token";
    public static final String CHECK_TOKEN_EXPIRATION = "/check-token-expiration";
    public static final String SAVE_TOKEN = "/user/save-token" ;
    public static final String GENERATE_NEW_TOKEN_FOR_USER = "/generate-new-token";
    public static final String DELETE_TOKEN = "/delete-token";
    public static final String VERIFY_EMAIL = "/verify-your-email";
    public static final String RESEND_VERIFICATION_TOKEN = "/resend-verification-token" ;
    /*============================ End verification token ===================================*/
}
