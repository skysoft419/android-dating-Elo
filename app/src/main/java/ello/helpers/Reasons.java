package ello.helpers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ranaasad on 16/05/2019.
 */
public class Reasons {
    @SerializedName("reason_id")
    @Expose
    private String reasonId;
    @SerializedName("reason_message")
    @Expose
    private String reasonMessage;
    public String getReasonMessage(){
        return reasonMessage;
    }
    public String getReasonId(){
        return reasonId;
    }
}
