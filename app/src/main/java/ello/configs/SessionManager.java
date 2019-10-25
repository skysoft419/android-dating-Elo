package ello.configs;
/**
 * @package com.trioangle.igniter
 * @subpackage configs
 * @category SessionManager
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.SharedPreferences;

import javax.inject.Inject;

/*****************************************************************
 Session manager to set and get glopal values
 ***************************************************************/
public class SessionManager {
    @Inject
    SharedPreferences sharedPreferences;

    public SessionManager() {
        AppController.getAppComponent().inject(this);
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }

    public String getProfileImg() {
        return sharedPreferences.getString("imageUrl", "");
    }

    public void setProfileImg(String imageUrl) {
        sharedPreferences.edit().putString("imageUrl", imageUrl).apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", "");
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString("phoneNumber", phoneNumber).apply();
    }

    public boolean getIsSawLike() {
        return sharedPreferences.getBoolean("isSawLike", true);
    }

    public void setIsSawLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawLike", isSaw).apply();
    }

    public boolean getIsSawUnLike() {
        return sharedPreferences.getBoolean("isSawUnLike", true);
    }

    public void setIsSawUnLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawUnLike", isSaw).apply();
    }

    public boolean getIsSawSuperLike() {
        return sharedPreferences.getBoolean("isSawSuperLike", true);
    }

    public void setIsSawSuperLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawSuperLike", isSaw).apply();
    }

    public boolean getIsSwipeLike() {
        return sharedPreferences.getBoolean("isSwipeLike", true);
    }

    public void setIsSwipeLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSwipeLike", isSaw).apply();
    }

    public boolean getIsSwipeUnLike() {
        return sharedPreferences.getBoolean("isSwipeUnLike", true);
    }

    public void setIsSwipeUnLike(Boolean isSwipe) {
        sharedPreferences.edit().putBoolean("isSwipeUnLike", isSwipe).apply();
    }

    public boolean getIsSwipeSuperLike() {
        return sharedPreferences.getBoolean("isSwipeSuperLike", true);
    }

    public void setIsSwipeSuperLike(Boolean isSwipe) {
        sharedPreferences.edit().putBoolean("isSwipeSuperLike", isSwipe).apply();
    }

    public String getCountryCode() {
        return sharedPreferences.getString("countryCode", "");
    }

    public void setCountryCode(String countryCode) {
        sharedPreferences.edit().putString("countryCode", countryCode).apply();
    }

    public int getDeviceWidth() {
        return sharedPreferences.getInt("deviceWidth", 0);
    }

    public void setDeviceWidth(int width) {
        sharedPreferences.edit().putInt("deviceWidth", width).apply();
    }

    public void clearToken() {
        sharedPreferences.edit().putString("token", "").apply();
    }

    public void clearAll() {
        sharedPreferences.edit().putString("token", "").apply();
        sharedPreferences.edit().putString("userName", "").apply();
        sharedPreferences.edit().putInt("userId", 0).apply();
        sharedPreferences.edit().putString("countryCode", "").apply();
        sharedPreferences.edit().putString("imageUrl", "").apply();


    }

    public int getUserId() {
        return sharedPreferences.getInt("userId", 0);
    }

    public void setUserId(int userId) {
        sharedPreferences.edit().putInt("userId", userId).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString("userName", "");
    }

    public void setUserName(String userName) {
        sharedPreferences.edit().putString("userName", userName).apply();
    }

    public String getSwipeType() {
        return sharedPreferences.getString("swipeType", "");
    }

    public void setSwipeType(String swipeType) {
        sharedPreferences.edit().putString("swipeType", swipeType).apply();
    }

    public boolean getSettingUpdate() {
        return sharedPreferences.getBoolean("settingUpdate", false);
    }

    public void setSettingUpdate(boolean settingUpdate) {
        sharedPreferences.edit().putBoolean("settingUpdate", settingUpdate).apply();
    }

    public int getTouchX() {
        return sharedPreferences.getInt("touchX", 0);
    }
    public void setUserData(String json){
        sharedPreferences.edit().putString("userdata",json).apply();
    }
    public String getUserData(){
        return sharedPreferences.getString("userdata","");
    }
    public void setTouchX(int touchX) {
        sharedPreferences.edit().putInt("touchX", touchX).apply();
    }

    public int getTouchY() {
        return sharedPreferences.getInt("touchY", 0);
    }

    public void setTouchY(int touchY) {
        sharedPreferences.edit().putInt("touchY", touchY).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString("deviceId", "");
    }

    public void setDeviceId(String deviceId) {
        sharedPreferences.edit().putString("deviceId", deviceId).apply();
    }

    public int getImageid() {
        return sharedPreferences.getInt("image_id", 0);
    }

    public void setImageid(int imageid) {
        sharedPreferences.edit().putInt("image_id", imageid).apply();
    }

    public Integer getUnMatchReasonId() {
        return sharedPreferences.getInt("unMatchReasonId", 0);
    }

    public void setUnMatchReasonId(Integer unMatchReasonId) {
        sharedPreferences.edit().putInt("unMatchReasonId", unMatchReasonId).apply();
    }

    public boolean getIsUnmatch() {
        return sharedPreferences.getBoolean("isUnmatch", false);
    }

    public void setIsUnmatch(Boolean isUnmatch) {
        sharedPreferences.edit().putBoolean("isUnmatch", isUnmatch).apply();
    }

    public String getPushNotification() {
        return sharedPreferences.getString("pushNotification", "");
    }

    public void setPushNotification(String pushNotification) {
        sharedPreferences.edit().putString("pushNotification", pushNotification).apply();
    }

    public boolean getIsOrder() {
        return sharedPreferences.getBoolean("isOrder", false);
    }

    public void setIsOrder(Boolean isOrder) {
        sharedPreferences.edit().putBoolean("isOrder", isOrder).apply();
    }

    public String getPlanType() {
        return sharedPreferences.getString("planType", "");
    }

    public void setPlanType(String planType) {
        sharedPreferences.edit().putString("planType", planType).apply();
    }

    public int getRemainingSuperLikes() {
        return sharedPreferences.getInt("remaningsuperlikes", 0);
    }

    public void setRemainingSuperLikes(int remainingSuperLikes) {
        sharedPreferences.edit().putInt("remaningsuperlikes", remainingSuperLikes).apply();
    }

    public int getRemainingLikes() {
        return sharedPreferences.getInt("remaninglikes", 20);
    }

    public void setRemainingLikes(int remainingLikes) {
        sharedPreferences.edit().putInt("remaninglikes", remainingLikes).apply();
    }

    public String getIsRemainingLikeLimited() {
        return sharedPreferences.getString("remaninglikeslimited", "");
    }

    public void setIsRemainingLikeLimited(String remainingLikeslimited) {
        sharedPreferences.edit().putString("remaninglikeslimited", remainingLikeslimited).apply();
    }

    public int getRemainingBoost() {
        return sharedPreferences.getInt("remainingBoost", 0);
    }

    public void setRemainingBoost(int remainingBoost) {
        sharedPreferences.edit().putInt("remainingBoost", remainingBoost).apply();
    }

    public boolean getIsFBUser() {
        return sharedPreferences.getBoolean("isFbUser", false);
    }

    public void setIsFBUser(boolean isFbUser) {
        sharedPreferences.edit().putBoolean("isFbUser", isFbUser).apply();
    }

    public String getMinAge() {
        return sharedPreferences.getString("minAge", "18");
    }

    public void setMinAge(String minAge) {
        sharedPreferences.edit().putString("minAge", minAge).apply();
    }

    public String getMaxAge() {
        return sharedPreferences.getString("maxAge", "100");
    }

    public void setMaxAge(String maxAge) {
        sharedPreferences.edit().putString("maxAge", maxAge).apply();
    }

    public String getMatched() {
        return sharedPreferences.getString("Matched", "0");
    }
    public void setFirstTime(String value){
        sharedPreferences.edit().putString("first", value).apply();
    }
    public String isFirst(){
        return sharedPreferences.getString("first","0");
    }
    public void setMatched(String Matched) {
        sharedPreferences.edit().putString("Matched", Matched).apply();
    }

}
