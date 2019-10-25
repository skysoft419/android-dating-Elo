package ello.helpers;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

import com.appolica.flubber.interpolator.providers.Linear;

import java.util.ArrayList;

import ello.datamodels.chat.MatchedProfileModel;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.SettingsModel;

/**
 * Created by ranaasad on 28/03/2019.
 */
public class StaticData {
    public static String ALREADY_USER;
    public static String PHONE_NUMBER;
    public static String COUNTRY_CODE;
    public static String EMAIL;
    public static  String VERFIED;
    public static  int FROM=0;
    public static  int SETTING=0;
    public static  String PIN;
    public static MatchedProfileModel matchedProfileModel=new MatchedProfileModel();
    public static SettingsModel settingsModel;
    public static ExternalLinks externalLinks;
    public static EditProfileModel editProfileModel;
    public static ArrayList<Reasons> unmatch;
    public static ArrayList<Reasons> delete;
    public static ArrayList<Reasons> report;
    public static  int CHAT=0;
    public static Bitmap image;
    public static Bitmap croppedImage;
    public static Boolean fromFacebook=false;
    public static Boolean fromFacebookIsDob=false;
    public static String facebookEmail;
    public static String facebookDob;
    public static Boolean isCardsAvailable=false;
    public static Boolean REPORTED=false;
    public static Boolean FROMPROFILE=false;
    public static  Integer unReadCount=0;
    public static  Integer unReadNewMatch=0;
    public static  ArrayList<String> TOPCARDIMAGES;
    public static  int touchX;
    public static  int touchY;
    public static  boolean isSwiped=false;
    public static boolean isFromFavourite=false;
    public static boolean isFirstRequest=true;
    public static boolean isFavourite=false;
    public static  int favouriteIndex=0;
}
