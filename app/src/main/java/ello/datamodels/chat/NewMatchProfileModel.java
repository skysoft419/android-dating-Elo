package ello.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category NewMatchProfileModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 NewMatchProfileModel
 ****************************************************************/
public class NewMatchProfileModel {

    @SerializedName("user_image_url")
    @Expose
    private String userImgUrl;
    @SerializedName("like_status")
    @Expose
    private String likeStatus;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("match_id")
    @Expose
    private Integer matchId;
    @SerializedName("read_status")
    @Expose
    private String readStatus;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("message")
    @Expose
    private String lastMessage;
    @SerializedName("is_reply")
    @Expose
    private String isReply;
    @SerializedName("all_images")
    @Expose
    private ArrayList<String> allImages;
    @SerializedName("kilometer")
    @Expose
    private String kilometer;
    @SerializedName("distance_type")
    @Expose
    private String distanceType;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("super_like")
    @Expose
    private String superLike;
    @SerializedName("gradient_no")
    @Expose
    private Integer gradientNo;

    public ArrayList<String> getAllImages() {
        return allImages;
    }

    public void setAllImages(ArrayList<String> allImages) {
        this.allImages = allImages;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public String getSuperLike() {
        return superLike;
    }

    public void setSuperLike(String superLike) {
        this.superLike = superLike;
    }

    public Integer getGradientNo() {
        return gradientNo;
    }

    public void setGradientNo(Integer gradientNo) {
        this.gradientNo = gradientNo;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getIsReply() {
        return isReply;
    }

    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }
}
