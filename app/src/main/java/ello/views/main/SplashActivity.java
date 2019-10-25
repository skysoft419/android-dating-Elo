package ello.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category SplashActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.SettingsModel;
import ello.helpers.AllUserData;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.RequestCallback;
import ello.views.chat.ChatConversationActivity;
import ello.views.signup.SignUpActivity;

/*****************************************************************
 Application splash screen
 ****************************************************************/
public class SplashActivity extends AppCompatActivity implements ServiceListener {
    @Inject
    ApiService apiService;
    @Inject
    SessionManager sessionManager;
    private boolean isSuper=false;
    private LottieAnimationView animationView;
    private ImageView iconImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppController.getAppComponent().inject(this);
        animationView=findViewById(R.id.animationview);
        iconImageView=findViewById(R.id.iconImageView);
        animationView.setImageAssetsFolder("images/");
        animationView.setAnimation("splash.json");


        callActivityIntent();
    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void callActivityIntent() {
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MainActivity: ", "Key: " + key + " Value: " + value);
            }
            if (getIntent().hasExtra("custom") && !TextUtils.isEmpty(sessionManager.getToken())) {
                try {
                    final JSONObject custom = new JSONObject(getIntent().getStringExtra("custom"));
                    if(custom.has("chat_status")) {
                        final String status = custom.getJSONObject("chat_status").getString("status");
                        animationView.playAnimation();
                        iconImageView.setVisibility(View.GONE);
                        animationView.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                getToIntent(status, custom);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                    }else if (custom.has("super_status") || custom.has("match_status")){
                        isSuper=true;
                        getData();
                    }
                } catch (JSONException e) {

                }
            } else {
                goNormalIntent();
            }
        } else {
            goNormalIntent();
        }
    }
    public void goNormalIntent() {
        if (TextUtils.isEmpty(sessionManager.getToken())) {
            animationView.playAnimation();
            iconImageView.setVisibility(View.GONE);
            animationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
//                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    StaticData.isFirstRequest=true;

                    String phoneNumberString = "86392934937";
                    String countryCode = "+86";
                    sessionManager.setPhoneNumber(phoneNumberString);
                    sessionManager.setCountryCode(countryCode);

                    StaticData.COUNTRY_CODE = countryCode;
                    StaticData.PHONE_NUMBER = phoneNumberString;
                    Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        } else {
            getData();
        }
    }

    private void getData(){
        apiService.getUserData(sessionManager.getToken()).enqueue(new RequestCallback(1,this));
    }

    public void getToIntent(String status, JSONObject jsonObject) {

        Intent notificationIntent;
        notificationIntent = new Intent(getApplicationContext(), ChatConversationActivity.class);
        if (status.equals("New message")) {
            notificationIntent = newMessage(jsonObject);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(notificationIntent);
        finish();
    }

    public Intent newMessage(JSONObject jsons) {
        try {
            //JSONObject data = new JSONObject(String.valueOf(jsons));
            Intent notificationIntent = new Intent(getApplicationContext(), ChatConversationActivity.class);
            notificationIntent.putExtra("matchId", Integer.valueOf(jsons.getJSONObject("chat_status").getString("match_id")));
            notificationIntent.putExtra("userId", Integer.valueOf(jsons.getJSONObject("chat_status").getString("sender_id")));
            return notificationIntent;
        } catch (JSONException e) {

        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if(jsonResp.isOnline()){
            if (jsonResp.isSuccess()){
                sessionManager.setUserData(jsonResp.getStrResponse());
                AllUserData allUserData=new Gson().fromJson(jsonResp.getStrResponse(),AllUserData.class);
                assignData(allUserData);
            }
        }
    }
    private void assignData(AllUserData userData){
        StaticData.externalLinks=userData.getLinks();
        StaticData.unReadNewMatch=userData.getNewMatchCount();
        StaticData.unReadCount=userData.getUnReadCount();
        SettingsModel settingsModel=new SettingsModel();
        settingsModel.setAccessToken(userData.getAccessToken());
        settingsModel.setAdminDistanceType(userData.getAdminDistanceType());
        settingsModel.setCommunityUrl(userData.getCommunityUrl());
        settingsModel.setCountryCode(userData.getCountryCode());
        settingsModel.setPhoneNumber(userData.getPhoneNumber());
        settingsModel.setEmail(userData.getEmail());
        settingsModel.setPin(userData.getPin());
        settingsModel.setVerifyEmail(userData.isVerifyEmail());
        settingsModel.setDistanceType(userData.getDistanceType());
        settingsModel.setHelpUrl(userData.getHelpUrl());
        settingsModel.setIsOrder(userData.getIsOrder());
        settingsModel.setLicenseUrl(userData.getLicenseUrl());
        settingsModel.setCity(userData.getCity());
        settingsModel.setMinimumAge(userData.getMinimumAge());
        settingsModel.setMaximumAge(userData.getMaximumAge());
        settingsModel.setMinAge(userData.getMinAge());
        settingsModel.setMaxAge(userData.getMaxAge());
        settingsModel.setMaxDistance(userData.getMaxDistance());
        settingsModel.setMaximumDistance(userData.getMaximumDistance());
        settingsModel.setMinimumDistance(userData.getMinimumDistance());
        settingsModel.setShowMe(userData.getShowMe());
        settingsModel.setMatchingProfile(userData.getMatchingProfile());
        settingsModel.setUserName(userData.getUserName());
        settingsModel.setUserId(userData.getUserId());
        settingsModel.setNewMatch(userData.getNewMatch());
        settingsModel.setReceivingMessage(userData.getReceivingMessage());
        settingsModel.setMessageLikes(userData.getMessageLikes());
        settingsModel.setSuperLikes(userData.getSuperLikes());
        settingsModel.setPrivacyPolicyUrl(userData.getPrivacyPolicyUrl());
        settingsModel.setTermsOfServiceUrl(userData.getTermsOfServiceUrl());
        settingsModel.setSafetyUrl(userData.getSafetyUrl());
        settingsModel.setPlanType(userData.getPlanType());
        settingsModel.setSearchLocation(userData.getSearchLocation());
        settingsModel.setLocationModels(userData.getLocationModels());
        EditProfileModel editProfileModel=new EditProfileModel();
        editProfileModel.setAccessToken(userData.getAccessToken());
        editProfileModel.setUserId(userData.getUserId());
        editProfileModel.setImageList(userData.getImageList());
        editProfileModel.setAbout(userData.getAbout());
        editProfileModel.setJobTitle(userData.getJobTitle());
        editProfileModel.setWork(userData.getWork());
        editProfileModel.setCollege(userData.getCollege());
        editProfileModel.setInstagramId(userData.getInstagramId());
        editProfileModel.setShowMyAge(userData.getShowMyAge());
        editProfileModel.setGender(userData.getGender());
        editProfileModel.setDistanceInvisible(userData.getDistanceInvisible());
        editProfileModel.setIsOrder(userData.getIsOrder());
        editProfileModel.setImage_id(userData.getImage_id());
        editProfileModel.setPlanType(userData.getPlanType());
        editProfileModel.setRemainingLikes(userData.getRemainingLikes());

        StaticData.matchedProfileModel.setMessage(userData.getChat());
        StaticData.matchedProfileModel.setNewMatchProfile(userData.getNewMatchProfile());
        StaticData.settingsModel=settingsModel;
        StaticData.editProfileModel=editProfileModel;
        StaticData.PIN=settingsModel.getPin();
        StaticData.unmatch=userData.getUnmatchReasons();
        StaticData.delete=userData.getDeleteReasons();
        StaticData.report=userData.getReportReasons();
        animationView.playAnimation();
        iconImageView.setVisibility(View.GONE);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                StaticData.isFirstRequest=true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });





    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
       String json=sessionManager.getUserData();
        AllUserData allUserData=new Gson().fromJson(json,AllUserData.class);
        assignData(allUserData);
    }
}
