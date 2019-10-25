package ello.interfaces;
/**
 * @package com.trioangle.igniter
 * @subpackage interfaces
 * @category ApiService
 * @author Trioangle Product Team
 * @version 1.0
 **/

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/*****************************************************************
 ApiService
 ****************************************************************/

public interface ApiService {

    // Login Page Slider Image
    @GET("login_slider")
    Call<ResponseBody> getTutorialSliderImg();

    // FaceBook SignUp
    @GET("socialsignup")
    Call<ResponseBody> facebookSignUp(@Query("fb_id") String fbId, @Query("email_id") String fbEmail, @Query("first_name") String firstName, @Query("last_name") String lastName, @Query("dob") String token, @Query("work") String fbWork, @Query("college") String fbEducation, @Query("gender") String fbGender, @Query("user_image_url") String userImgUrl, @Query("job_title") String job_title);
    @GET("user_data")
    Call<ResponseBody> getUserData(@Query("token") String token);
    // FaceBook SignUp
    @GET("socialsignup")
    Call<ResponseBody> socialSignUp(@QueryMap HashMap<String, String> hashMap);


    // PhoneNumber Verification
    @GET("phone_number_verification")
    Call<ResponseBody> verifyPhoneNumber(@Query("phone_number") String phoneNumber, @Query("country_code") String countryCode,@Query("access_token") String access_token);

    // PhoneNumber SignUp
    @POST("signup")
    Call<ResponseBody> phoneNumberSignUp(@Body RequestBody RequestBody);

    // Facebook Phone number signup
    @GET("fb_phone_signup")
    Call<ResponseBody> fbPhoneSignup(@QueryMap HashMap<String, String> hashMap);
    //Call<ResponseBody> fbPhoneSignup(@Query("phone_number") String phoneNumber, @Query("country_code") String countryCode,@Query("fb_id") String fbId,@Query("email_id") String fbEmail,@Query("first_name") String firstName, @Query("last_name") String lastName,@Query("dob") String fbAge,@Query("work") String fbWork,@Query("college") String fbEducation,@Query("gender") String fbGender,@Query("user_image_url") String userImgUrl,@Query("job_title") String job_title);

    // login
    @GET("login")
    Call<ResponseBody> login(@Query("phone_number") String phoneNumber, @Query("country_code") String countryCode);
    @GET("send_gradient_no")
    Call<ResponseBody> setGradient(@Query("token") String token, @Query("gradient_no") String gradientNo,@Query("match_id") String matchId);

    // Get Edit Profile details
    @GET("edit_profile")
    Call<ResponseBody> getEditProfileDetail(@Query("token") String token);

    // Upload profile image
    @POST("upload_profile_image")
    Call<ResponseBody> uploadProfileImg(@Body RequestBody RequestBody);
    @POST("update_profile_image")
    Call<ResponseBody> updateImage(@Body RequestBody requestBody);
    // Update Profile
    @GET("update_profile")
    Call<ResponseBody> updateProfile(@QueryMap HashMap<String, String> hashMap);

    // Get My Profile Detail
    @GET("own_profile_view")
    Call<ResponseBody> getMyProfileDetail(@Query("token") String token);

    // Update Device ID for Push notification
    @GET("update_device")
    Call<ResponseBody> updateDeviceId(@Query("token") String token, @Query("device_type") String device_type, @Query("device_id") String device_id);

    // Get Settings
    @GET("user_settings")
    Call<ResponseBody> getUserSettings(@Query("token") String token);

    // Update Settings
    @GET("edit_settings")
    Call<ResponseBody> updateSettings(@QueryMap HashMap<String, String> hashMap);
    @GET("send_email_otp")
    Call<ResponseBody> sendCode(@Query("token") String token,@Query("email") String email);
    @GET("verify_email_otp")
    Call<ResponseBody> verifyCode(@Query("token") String token,@Query("email_otp") String otp);
    @GET("delete_account")
    Call<ResponseBody> deleteAccount(@Query("token") String token,@Query("reason_id") String id);
    @GET("report_account")
    Call<ResponseBody> reportUser(@Query("token") String token,@Query("report_id") String id,@Query("reporter_id") String reporterId);
    @GET("unmatch_account")
    Call<ResponseBody> unmatchUser(@Query("token") String token,@Query("report_id") String id,@Query("user_id") String reporterId);
    // Get Plus
    @GET("plus_settings")
    Call<ResponseBody> getPlusSettings(@Query("token") String token);

    // Update Plus
    @GET("update_plus_settings")
    Call<ResponseBody> updatePlusSettings(@QueryMap HashMap<String, String> hashMap);

    // Get Other Profile View
    @GET("other_profile_view")
    Call<ResponseBody> otherProfileView(@Query("token") String token, @Query("user_id") Integer userId);

    // Home Page
    @GET("home_page")
    Call<ResponseBody> homePage(@Query("token") String token);

    // Chat
    @GET("message_conversation")
    Call<ResponseBody> chat(@Query("token") String token, @Query("match_id") String userId);

    // Swipe Profile
    @GET("swipe_profiles")
    Call<ResponseBody> swipeProfile(@Query("token") String token, @Query("user_id") Integer userId, @Query("status") String status, @Query("used_subscription_id") Integer subscriptionId);

    // Boost user Profile
    @GET("add_user_boost")
    Call<ResponseBody> boostUser(@Query("token") String token);

    // Insert Location
    @GET("insertlocation")
    Call<ResponseBody> insertLocation(@Query("token") String token, @Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("type") String type);


    // Cheange Default Location
    @GET("defaultLocaion")
    Call<ResponseBody> changeDefaultLocation(@Query("token") String token, @Query("id") int id);


    // Show all matched profile
    @GET("matching_profiles")
    Call<ResponseBody> showMatchingProfile(@Query("token") String token, @Query("latitude") Double latitude, @Query("longitude") Double longitude);

    // UserName Claim
    @GET("username_claim")
    Call<ResponseBody> claimUserName(@Query("token") String token, @Query("username") String username);

    // Igniter Plus Slider
    @GET("super_plus_slider")
    Call<ResponseBody> igniterPlusSlider(@Query("token") String token);

    // Igniter Plan Slider
    @GET("plan_slider")
    Call<ResponseBody> igniterPlanSlider(@Query("type") String token);

    // logout
    @GET("logout")
    Call<ResponseBody> logout(@Query("token") String token);

    // Get UnMatch Details
    @GET("unmatch_details")
    Call<ResponseBody> unMatchDetails(@Query("token") String token);

    // Unmatch User
    @GET("unmatch_account")
    Call<ResponseBody> unMatchUser(@Query("token") String token, @Query("report_id") Integer matchId, @Query("user_id") Integer userId);

    // Matched profile list
    @GET("match_details")
    Call<ResponseBody> matchedDetails(@Query("token") String token);
    @GET("likes_to_read")
    Call<ResponseBody> likesToRead(@Query("token") String token,@Query("other_id") String userId);
    // Message Conversation
    @GET("message_conversation")
    Call<ResponseBody> messageConversation(@Query("token") String token, @Query("match_id") Integer matchId);

    // Sent Message
    @GET("send_message")
    Call<ResponseBody> sendMessage(@Query("token") String token,@Query("sender_id") String senderId, @Query("match_id") Integer matchId,@Query("gif_id") String gifId, @Query("message") String message,@Query("is_emoji") String isEmoji,@Query("gif_image_url") String gifUrl,@Query("gif_ratio") String gifRatio);
    @GET("change_read_status")
    Call<ResponseBody> updateReadCount(@Query("token") String token,@Query("message_id") String messageId);
    // Message Like
    @GET("message_like")
    Call<ResponseBody> likeMessage(@Query("token") String token, @Query("message_id") Integer messageId, @Query("user_id") Integer userId, @Query("message") String message);

    //Remove photos
    @GET("remove_profile_image")
    Call<ResponseBody> remove_profile_image(@Query("image_id") Integer imageid, @Query("token") String token);

    // After Payment (Update Order id)
    @GET("after_payment")
    Call<ResponseBody> afterPayment(@Query("payment_type") String paymentType, @Query("transaction_id") String transactionId, @Query("plan_id") Integer planId, @Query("plan_type") String planType, @Query("package_name") String packageName, @Query("product_id") String productId, @Query("purchase_token") String purchaseToken, @Query("token") String token);

}
