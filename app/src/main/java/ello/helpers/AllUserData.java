package ello.helpers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.main.ImageModel;
import ello.datamodels.main.LocationModel;

/**
 * Created by ranaasad on 16/05/2019.
 */
public class AllUserData {
    @SerializedName("external_links")
    @Expose
    private ExternalLinks links;
    @SerializedName("status_message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private String code;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("email_otp")
    @Expose
    private String pin;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("remaining_slikes_count")
    @Expose
    private int remainingLikes;
    @SerializedName("image_url")
    @Expose
    private ArrayList<ImageModel> imageList = new ArrayList<>(5);
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("instagram_id")
    @Expose
    private String instagramId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("show_my_age")
    @Expose
    private String showMyAge;
    @SerializedName("distance_invisible")
    @Expose
    private String distanceInvisible;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("image_id")
    @Expose
    private String image_id;
    @SerializedName("email_verify")
    @Expose
    private String verifyEmail;
    @SerializedName("city")
    @Expose
    private String city;
    /*@SerializedName("location")
    @Expose
    private String location;*/
    @SerializedName("minimum_age")
    @Expose
    private Integer minimumAge;
    @SerializedName("maximum_age")
    @Expose
    private Integer maximumAge;
    @SerializedName("minimum_distance")
    @Expose
    private Integer minimumDistance;
    @SerializedName("maximum_distance")
    @Expose
    private Integer maximumDistance;
    @SerializedName("max_distance")
    @Expose
    private Integer maxDistance;

    @SerializedName("min_age")
    @Expose
    private Integer minAge;
    @SerializedName("max_age")
    @Expose
    private Integer maxAge;
    @SerializedName("show_me")
    @Expose
    private String showMe;
    @SerializedName("username")
    @Expose
    private String userName;
    @SerializedName("matching_profile")
    @Expose
    private String matchingProfile;
    @SerializedName("new_match")
    @Expose
    private String newMatch;
    @SerializedName("receiving_message")
    @Expose
    private String receivingMessage;
    @SerializedName("message_likes")
    @Expose
    private String messageLikes;
    @SerializedName("super_likes")
    @Expose
    private String superLikes;
    @SerializedName("distance_type")
    @Expose
    private String distanceType;
    @SerializedName("distance_unit")
    @Expose
    private String distanceUnit;
    @SerializedName("profile_url")
    @Expose
    private String profileUrl;
    @SerializedName("help_url")
    @Expose
    private String helpUrl;
    @SerializedName("license_url")
    @Expose
    private String licenseUrl;
    @SerializedName("privacy_policy_url")
    @Expose
    private String privacyPolicyUrl;
    @SerializedName("terms_of_service_url")
    @Expose
    private String termsOfServiceUrl;
    @SerializedName("community_url")
    @Expose
    private String communityUrl;
    @SerializedName("safety_url")
    @Expose
    private String safetyUrl;
    @SerializedName("search_location")
    @Expose
    private String searchLocation;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("phone_code")
    @Expose
    private String countryCode;
    @SerializedName("new_match_count")
    @Expose
    private Integer newMatchCount;
    @SerializedName("unread_count")
    @Expose
    private Integer unReadCount;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("location")
    @Expose
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    @SerializedName("conversation_not_started_array")
    @Expose
    private ArrayList<NewMatchProfileModel> newMatchProfile = new ArrayList<>();
    @SerializedName("conversation_started_array")
    @Expose
    private ArrayList<NewMatchProfileModel> chat = new ArrayList<>();
    @SerializedName("delete_reasons")
    @Expose
    private ArrayList<Reasons> deleteReason = new ArrayList<>();
    @SerializedName("report_reasons")
    @Expose
    private ArrayList<Reasons> reportReason = new ArrayList<>();
    @SerializedName("unmatch_reasons")
    @Expose
    private ArrayList<Reasons> unmatchReason = new ArrayList<>();
    public ArrayList<Reasons> getDeleteReasons(){
        return deleteReason;
    }
    public ArrayList<Reasons> getUnmatchReasons(){
        return unmatchReason;
    }
    public ArrayList<Reasons> getReportReasons(){
        return reportReason;
    }
    public ArrayList<NewMatchProfileModel> getNewMatchProfile() {
        return newMatchProfile;
    }

    public void setNewMatchProfile(ArrayList<NewMatchProfileModel> newMatchProfile) {
        this.newMatchProfile = newMatchProfile;
    }
    public void setRemainingLikes(int remainingLikes){
        this.remainingLikes=remainingLikes;
    }
    public int getRemainingLikes(){
        return  remainingLikes;
    }
    public ArrayList<NewMatchProfileModel> getChat() {
        return chat;
    }
    public Integer getNewMatchCount() {
        return newMatchCount;
    }

    public void setNewMatchCount(Integer newMatchCount) {
        this.newMatchCount = newMatchCount;
    }

    public Integer getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(Integer unReadCount) {
        this.unReadCount = unReadCount;
    }
    public void setMessage(ArrayList<NewMatchProfileModel> message) {
        this.chat = message;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String isVerifyEmail(){
        return verifyEmail;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getCity(){
        return city;
    }
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /*public String getLocation() {
        return location;
    }*/

    /*public void setLocation(String location) {
        this.location = location;
    }*/

    public String getMatchingProfile() {
        return matchingProfile;
    }

    public void setMatchingProfile(String matchingProfile) {
        this.matchingProfile = matchingProfile;
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getShowMe() {
        return showMe;
    }

    public void setShowMe(String showMe) {
        this.showMe = showMe;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewMatch() {
        return newMatch;
    }

    public void setNewMatch(String newMatch) {
        this.newMatch = newMatch;
    }

    public String getReceivingMessage() {
        return receivingMessage;
    }

    public void setReceivingMessage(String receivingMessage) {
        this.receivingMessage = receivingMessage;
    }

    public String getMessageLikes() {
        return messageLikes;
    }

    public void setMessageLikes(String messageLikes) {
        this.messageLikes = messageLikes;
    }

    public String getSuperLikes() {
        return superLikes;
    }

    public void setSuperLikes(String superLikes) {
        this.superLikes = superLikes;
    }

    public String getAdminDistanceType() {
        return distanceUnit;
    }

    public void setAdminDistanceType(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getCommunityUrl() {
        return communityUrl;
    }

    public void setCommunityUrl(String communityUrl) {
        this.communityUrl = communityUrl;
    }

    public String getSafetyUrl() {
        return safetyUrl;
    }

    public void setSafetyUrl(String safetyUrl) {
        this.safetyUrl = safetyUrl;
    }

    public ArrayList<LocationModel> getLocationModels() {
        return locationModels;
    }

    public void setLocationModels(ArrayList<LocationModel> locationModels) {
        this.locationModels = locationModels;
    }

    public Integer getMaximumDistance() {
        return maximumDistance;
    }

    public void setMaximumDistance(Integer maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public Integer getMinimumDistance() {
        return minimumDistance;
    }

    public void setMinimumDistance(Integer minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }


    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public void setPin(String pin){
        this.pin=pin;
    }

    public String getPin() {
         return pin;
    }


    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShowMyAge() {
        return showMyAge;
    }

    public void setShowMyAge(String showMyAge) {
        this.showMyAge = showMyAge;
    }

    public String getDistanceInvisible() {
        return distanceInvisible;
    }

    public void setDistanceInvisible(String distanceInvisible) {
        this.distanceInvisible = distanceInvisible;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }

    public ExternalLinks getLinks() {
        return links;
    }

    public void setLinks(ExternalLinks links) {
        this.links = links;
    }
}
