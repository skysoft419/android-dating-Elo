package ello.datamodels.chat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ranaasad on 18/05/2019.
 */
public class NotificationData {
    @SerializedName("conversation_not_started")
    @Expose
    private ArrayList<NewMatchProfileModel> newMatchProfile = new ArrayList<>();
    @SerializedName("conversation_started")
    @Expose
    private ArrayList<NewMatchProfileModel> chat = new ArrayList<>();

    public ArrayList<NewMatchProfileModel> getChat() {
        return chat;
    }

    public ArrayList<NewMatchProfileModel> getNewMatchProfile() {
        return newMatchProfile;
    }
}
