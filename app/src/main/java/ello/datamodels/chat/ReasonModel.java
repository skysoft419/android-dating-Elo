package ello.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category ReasonModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 ReasonModel
 ****************************************************************/
public class ReasonModel {

    @SerializedName("reason_id")
    @Expose
    private Integer reasonId;
    @SerializedName("reason_message")
    @Expose
    private String reasonMessage;
    @SerializedName("reason_icon")
    @Expose
    private String reasonImage;

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

    public String getReasonImage() {
        return reasonImage;
    }

    public void setReasonImage(String reasonImage) {
        this.reasonImage = reasonImage;
    }

}
