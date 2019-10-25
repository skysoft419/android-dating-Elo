package ello.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category LoginActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.internal.SmartLoginOption;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.obs.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import ello.R;
import ello.adapters.main.ViewPagerAdapter;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.JSONParser;
import ello.configs.SessionManager;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.ImageListModel;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.SettingsModel;
import ello.datamodels.main.SignUpModel;
import ello.datamodels.main.SliderModel;
import ello.helpers.AllUserData;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.customize.CirclePageIndicator;
import ello.views.customize.CustomDialog;
import ello.views.signup.FacebookDOBActivity;
import ello.views.signup.FacebookEmailActivity;
import ello.views.signup.SignUpActivity;

import static android.text.Html.fromHtml;
import static ello.utils.Enums.REQ_FB_SIGNUP;
import static ello.utils.Enums.REQ_GET_LOGIN_SLIDER;
import static ello.utils.Enums.REQ_VERIFY_NUMBER;

/*****************************************************************
 User Login Activity
 ****************************************************************/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener {
    private static int APP_REQUEST_CODE = 99;
    JSONParser jsonparser = new JSONParser();
    JSONObject jobj = null;
    String firstName, lastName, fbId, fbPhone, fbEmail, fbGender, fbProfileImg, fbProfileImg1, fbWork, fbEducation, fbAge, fbjob_title;
    HashMap<String, String> hashMap = new HashMap<>();
    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    @Inject
    CustomDialog customDialog;
    @Inject
    Gson gson;
    private ViewPagerAdapter PagerAdapter;
    private ViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    // private CustomTextView tvDotNotPostFb,tvTakePolicy,  tvLocationMsg ;
    private CustomTextView tvArrowTop, tvArrowBottom, tvLoginFb, tvLoginPhone, tvTermsPolicy;
    private RelativeLayout rltTutorial;
    private LinearLayout lltLoginBottom, arrow_bottom, signup_more, signup_less;
    private AlertDialog dialog;
    private ArrayList<ImageListModel> imageList = new ArrayList<>();
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private boolean isFb=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);
        setContentView(R.layout.login_activity);

        getFbKeyHash(getApplicationContext().getResources().getString(R.string.package_name));
        initView();
        getSliderImageList();
        initViewPagerListener();
        initFacebookLogin();
        sessionManager.setIsFBUser(false);
    }

    /**
     * objectCreation method is used to create all objects.
     */
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        tvLoginFb = (CustomTextView) findViewById(R.id.tv_login_facebook);
        tvLoginPhone = (CustomTextView) findViewById(R.id.tv_login_phone);

        tvArrowBottom = (CustomTextView) findViewById(R.id.tv_arrow_bottom);
        tvArrowTop = (CustomTextView) findViewById(R.id.tv_arrow_top);
        tvTermsPolicy = (CustomTextView) findViewById(R.id.tv_terms_condition);
       /* tvTakePolicy = (CustomTextView) findViewById(R.id.tv_take_policy);

        tvLocationMsg = (CustomTextView) findViewById(R.id.tv_location_not_shown);
        tvDotNotPostFb = (CustomTextView) findViewById(R.id.tv_do_not_fb);*/

        rltTutorial = (RelativeLayout) findViewById(R.id.rlt_tutorial);
        lltLoginBottom = (LinearLayout) findViewById(R.id.llt_login_bottom);
        arrow_bottom = (LinearLayout) findViewById(R.id.arrow_bottom);
        signup_less = (LinearLayout) findViewById(R.id.signup_less);
        signup_more = (LinearLayout) findViewById(R.id.signup_more);
        dialog = commonMethods.getAlertDialog(this);

        tvLoginPhone.setOnClickListener(this);
        tvLoginFb.setOnClickListener(this);
        tvArrowBottom.setOnClickListener(this);
        tvArrowTop.setOnClickListener(this);
        arrow_bottom.setOnClickListener(this);

       /* tvDotNotPostFb.setTextSize(14);
        tvLocationMsg.setTextSize(14);
        tvTermsPolicy.setTextSize(14);*/

        if (Build.VERSION.SDK_INT >= 24) {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy), 0));
            tvTermsPolicy.setTextColor(getResources().getColor(R.color.light_gray));
        } else {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy)));
            tvTermsPolicy.setTextColor(getResources().getColor(R.color.light_gray));

        }
        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.redirect_url) + getString(R.string.terms_url)); // missing 'http://' will cause crashed

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;


            }
        };

        ClickableSpan PrivacyPolicy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.redirect_url) + getString(R.string.privacy_url)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;


            }
        };

//    Static pages links

        makeLinks(tvTermsPolicy, new String[]{"Terms of Service", "Privacy Policy"}, new ClickableSpan[]{
                termsOfServicesClick, PrivacyPolicy
        });
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setTextColor(getResources().getColor(R.color.text_light_gray));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void initFacebookLogin() {
        sessionManager.setUserId(0);
        sessionManager.setIsFBUser(false);
        //AppEventsLogger.activateApp(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.e("accessToken", String.valueOf(accessToken.getToken()));
                getFacebookUserProfile(accessToken);
                Log.e("login result", String.valueOf(loginResult.getAccessToken()));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    private void getFacebookUserProfile(final AccessToken accessToken) {
        final GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject jsonObject = response.getJSONObject();
                try {

                    Log.e("jsonObject", String.valueOf(jsonObject));
                    firstName = jsonObject.getString("first_name");
                    lastName = jsonObject.getString("last_name");
                    fbId = jsonObject.getString("id");
                    fbGender = "";
                    fbEmail = "";
                    fbPhone = "";
                    fbProfileImg1 = "";
                    fbProfileImg = "https://graph.facebook.com/" + fbId + "/picture?type=large";
                    fbWork = "";
                    fbjob_title = "";
                    if (jsonObject.has("email")) {
                        fbEmail = jsonObject.getString("email");
                    }
                    if (jsonObject.has("mobile_phone")) {
                        fbPhone = jsonObject.getString("mobile_phone");
                    }
                    if (jsonObject.has("gender")) {
                        fbGender = jsonObject.getString("gender");
                        if (fbGender.equals("male"))
                            fbGender = "Men";
                        else
                            fbGender = "Women";
                    }

                    fbEducation = "";
                    fbAge = "";
                    if (jsonObject.has("work") || jsonObject.has("education") || jsonObject.has("birthday")) {
                        if (jsonObject.has("work")) {

                            if (jsonObject.getJSONArray("work").getJSONObject(0).has("position")) {
                                fbjob_title = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("position").getString("name");
                                fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");
                            } else {
                                fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");
                            }
                            /*fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("position").getString("name");
                            fbWork = fbWork + " at " + jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");*/
                        }


                        if (jsonObject.has("education")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("education");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getString("type").contains("College")) {
                                    if (fbEducation.equals(""))
                                        fbEducation = jsonArray.getJSONObject(i).getJSONObject("school").getString("name");
                                    else
                                        fbEducation = fbEducation + ", " + jsonArray.getJSONObject(i).getJSONObject("school").getString("name");
                                }
                            }
                        }


                        if (jsonObject.has("birthday")) {
                            if (!jsonObject.getString("birthday").equals("")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                                Date newDate = sdf.parse(jsonObject.getString("birthday"));
                                SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
                                fbAge = spf.format(newDate);
                                //String[] cal = jsonObject.getString("birthday").split("/");
                                //fbAge = getAge(Integer.valueOf(cal[2]), Integer.valueOf(cal[0]), Integer.valueOf(cal[1]));
                            }
                        }

                        Log.e("Age", fbAge + " " + fbWork + " " + fbEducation);
                   /* if (jsonObject.has("picture")) {
                        fbProfileImg = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    }*/

                        // Get Facebook Album
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name");
                        if (fbAge != null && fbEmail != null) {
                            if (!fbAge.isEmpty() && !fbEmail.isEmpty()) {
                                facebookSignUpApi(fbId, fbEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
                            }else{
                                StaticData.fromFacebook=true;
                                if (fbAge.isEmpty() && fbEmail.isEmpty()){
                                    StaticData.fromFacebookIsDob=true;
                                    Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                    startActivity(intent);
                                }else if(fbEmail.isEmpty()){
                                    StaticData.fromFacebookIsDob=false;
                                    Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent=new Intent(LoginActivity.this, FacebookDOBActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }else{
                            StaticData.fromFacebook=true;
                            if (fbAge == null && fbEmail == null){
                                StaticData.fromFacebookIsDob=true;
                                Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                startActivity(intent);
                            }else if(fbEmail == null){
                                StaticData.fromFacebookIsDob=false;
                                Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(LoginActivity.this, FacebookDOBActivity.class);
                                startActivity(intent);
                            }
                        }

                    } else {
                        if (fbAge != null && fbEmail != null) {
                            if (!fbAge.isEmpty() && !fbEmail.isEmpty()) {
                                facebookSignUpApi(fbId, fbEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
                            }else{
                                StaticData.fromFacebook=true;
                                if (fbAge.isEmpty() && fbEmail.isEmpty()){
                                    StaticData.fromFacebookIsDob=true;
                                    Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                    startActivity(intent);
                                }else if(fbEmail.isEmpty()){
                                    StaticData.fromFacebookIsDob=false;
                                    Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent=new Intent(LoginActivity.this, FacebookDOBActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }else{
                            StaticData.fromFacebook=true;
                            if (fbAge == null && fbEmail == null){
                                StaticData.fromFacebookIsDob=true;
                                Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                startActivity(intent);
                            }else if(fbEmail == null){
                                StaticData.fromFacebookIsDob=false;
                                Intent intent=new Intent(LoginActivity.this, FacebookEmailActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent=new Intent(LoginActivity.this, FacebookDOBActivity.class);
                                startActivity(intent);
                            }
                        }
                        }


                    //facebookSignUpApi(fbId, firstName, lastName, fbProfileImg);
                    /*if(fbProfileImg1[0].equals(""))
                        facebookSignUpApi(fbId, fbEmail,fbPhone,firstName, lastName,fbAge,fbWork,fbEducation,fbGender,fbProfileImg);
                    else
                        facebookSignUpApi(fbId,fbEmail,fbPhone,firstName, lastName,fbAge,fbWork,fbEducation,fbGender,fbProfileImg1[0]);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name,last_name, picture.type(large),email,gender,work,birthday,education");
        //parameters.putString("fields", "id, name, first_name,last_name, email, picture.type(large),work,likes,friendlists,age_range,education");
        request.setParameters(parameters);
        request.executeAsync();

    }

    // Get Age from Date of Birth
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    private void facebookSignUpApi(String fbId, String fbEmail, String fbPhone, String firstName, String lastName, String fbAge, String fbWork, String fbEducation, String fbGender, String fbProfileImg, String fbjob_title) {
        commonMethods.showProgressDialog(LoginActivity.this, customDialog);
        /*if(fbAge!=null&& !TextUtils.isEmpty(fbAge)&&fbGender!=null&& !TextUtils.isEmpty(fbGender)) {
            getParams(fbId, fbEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
            apiService.facebookSignUp(fbId, fbEmail, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title).enqueue(new RequestCallback(REQ_FB_SIGNUP, this));
        }else{
            System.out.println("getting Empty");
        }*/
        if(fbAge!=null&&fbGender!=null) {
            if (TextUtils.isEmpty(fbGender)){
                fbGender="Men";
            }
            getParams(fbId, fbEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
            apiService.facebookSignUp(fbId, fbEmail, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title).enqueue(new RequestCallback(REQ_FB_SIGNUP, this));
        }else{
            System.out.println("getting Empty");
        }
    }

    private HashMap<String, String> getParams(String fbId, String fbEmail, String fbPhone, String firstName, String lastName, String fbAge, String fbWork, String fbEducation, String fbGender, String fbProfileImg, String fbjob_title) {
        hashMap.put("fb_id", fbId);
        hashMap.put("email_id", fbEmail);
        hashMap.put("first_name", firstName);
        hashMap.put("last_name", lastName);
        hashMap.put("dob", fbAge);
        hashMap.put("work", fbWork);
        hashMap.put("college", fbEducation);
        hashMap.put("gender", fbGender);
        hashMap.put("user_image_url", fbProfileImg);
        hashMap.put("job_title", fbjob_title);
        return hashMap;
    }

    private void getSliderImageList() {
        commonMethods.showProgressDialog(LoginActivity.this, customDialog);
        apiService.getTutorialSliderImg().enqueue(new RequestCallback(REQ_GET_LOGIN_SLIDER, this));
    }

    /**
     * Method called for make circle page indicator setup.
     */
    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(4 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(this, R.color.gray_indicator));
        pageIndicator.setFillColor(ContextCompat.getColor(this, R.color.color_accent));
        pageIndicator.setStrokeColor(ContextCompat.getColor(this, R.color.gray_indicator));
        viewPager.setCurrentItem(0);
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing(0 * density);
    }

    public void onFbLoginClick() {
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                //"user_likes",
                "user_photos",
                "email",
                "public_profile",
                //"user_about_me",
                "user_birthday",
                "user_gender"
                //"user_friends",
                //"user_location",
                //"user_relationships",
          ));
    }

    public void onPhoneLoginClick() {
        phoneLogin();
    }
    public void phoneLogin() {
        AccountKit.logOut();
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);

        // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }
    public void onArrowTopClick() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        lltLoginBottom.setLayoutParams(params);

        rltTutorial.setVisibility(View.VISIBLE);
        tvArrowBottom.setVisibility(View.VISIBLE);
        tvArrowTop.setVisibility(View.GONE);
        signup_more.setVisibility(View.GONE);
        signup_less.setVisibility(View.VISIBLE);
       /* tvArrowTop.setVisibility(View.GONE);
        tvTakePolicy.setVisibility(View.GONE);
        tvLocationMsg.setVisibility(View.GONE);

        tvDotNotPostFb.setTextSize(14);
        tvLocationMsg.setTextSize(14);
        tvTermsPolicy.setTextSize(14);

        if (Build.VERSION.SDK_INT >= 24) {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy), 0));
        } else {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy)));
        }*/
    }

    public void onArrowBottomClick() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lltLoginBottom.setLayoutParams(params);

        rltTutorial.setVisibility(View.GONE);
        tvArrowBottom.setVisibility(View.GONE);
        tvArrowTop.setVisibility(View.VISIBLE);
        signup_more.setVisibility(View.VISIBLE);
        signup_less.setVisibility(View.GONE);
       /* tvTakePolicy.setVisibility(View.VISIBLE);
        tvLocationMsg.setVisibility(View.VISIBLE);

        tvDotNotPostFb.setTextSize(16);
        tvLocationMsg.setTextSize(16);
        tvTermsPolicy.setTextSize(16);

        tvTermsPolicy.setText(getResources().getString(R.string.login_do_not_contact));*/
    }

    /**
     * Method called for initiate listener which triggered get tutorial page
     * navigation.
     */
    private void initViewPagerListener() {
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        pageIndicator.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        pageIndicator.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        pageIndicator.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_phone:
                onPhoneLoginClick();
                break;
            case R.id.tv_login_facebook:
                onFbLoginClick();
                break;
            case R.id.arrow_bottom:
                onArrowBottomClick();
                break;
            case R.id.tv_arrow_bottom:
                onArrowBottomClick();
                break;
            case R.id.tv_arrow_top:
                onArrowTopClick();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StaticData.fromFacebook){
            StaticData.fromFacebook=false;
            StaticData.fromFacebookIsDob=false;
            if(StaticData.facebookEmail != null){
                fbEmail=StaticData.facebookEmail;
            }
            if(StaticData.facebookDob != null){
               fbAge=StaticData.facebookDob;
            }
            facebookSignUpApi(fbId, fbEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(LoginActivity.this, dialog, data);
            return;
        }

        if (jsonResp.getRequestCode() == REQ_GET_LOGIN_SLIDER && jsonResp.isSuccess()) {
            onSuccessGetSliderImg(jsonResp);
        } else if (jsonResp.getRequestCode() == REQ_FB_SIGNUP && jsonResp.isSuccess()) {
            sessionManager.setProfileImg(fbProfileImg);
            if (jsonResp.getStatusMsg().equalsIgnoreCase("Login Success")) {
                String token = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ACCESS_TOKEN, String.class);
                if (!TextUtils.isEmpty(token)) {
                    sessionManager.setToken(token);
                    LoginManager.getInstance().logOut();
                    //sessionManager.setProfileImg();
                    getData();
                }
            } else if (jsonResp.getStatusMsg().equalsIgnoreCase("Phone Number Already Exist Please Login...")) {
                LoginManager.getInstance().logOut();
                commonMethods.showMessage(LoginActivity.this, dialog, jsonResp.getStatusMsg());
            } else if (jsonResp.getStatusMsg().equals("Your Account Deactivated..Please Contact ello.ie")) {
                LoginManager.getInstance().logOut();
                Toast.makeText(this, jsonResp.getStatusMsg(), Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();*/
            } else {

                isFb=true;
                AccountKit.logOut();
                final Intent intent = new Intent(this, AccountKitActivity.class);
                AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                        new AccountKitConfiguration.AccountKitConfigurationBuilder(
                                LoginType.PHONE,
                                AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
                // ... perform additional configuration ...
                intent.putExtra(
                        AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                        configurationBuilder.build());
                startActivityForResult(intent, APP_REQUEST_CODE);

            }
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(LoginActivity.this, dialog, data);
    }

    private void onSuccessGetSliderImg(JsonResponse jsonResp) {
        SliderModel sliderModel = gson.fromJson(jsonResp.getStrResponse(), SliderModel.class);
        sessionManager.setMinAge(sliderModel.getMinimumAge());
        sessionManager.setMaxAge(sliderModel.getMaximumAge());
        if (sliderModel != null && sliderModel.getImageList() != null && sliderModel.getImageList().size() > 0) {
            imageList.clear();
            imageList.addAll(sliderModel.getImageList());
        }
        setViewPagerAdapter();
    }

    private void setViewPagerAdapter() {
        if (imageList.size() > 0) {
            PagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Constants.VP_LOGIN_SLIDER, imageList.size(), imageList);
            viewPager.setAdapter(PagerAdapter);
            pageIndicator.setViewPager(viewPager);
            initPageIndicator();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else if (requestCode == APP_REQUEST_CODE){
            AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (!result.wasCancelled()&&result.getError() == null){
                final String token=result.getAccessToken().getToken();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        // Get Account Kit ID
                         String accountKitId = account.getId();
                         Log.i("token",token);
                         System.out.println(token);
                        // Get phone number
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        String access=token;
                        if (phoneNumber != null) {
                            final String phoneNumberString = phoneNumber.getPhoneNumber().toString();
                            final String countryCode = phoneNumber.getCountryCode().toString();
                            sessionManager.setPhoneNumber(phoneNumberString);
                            sessionManager.setCountryCode(countryCode);
                            commonMethods.showProgressDialog(LoginActivity.this, customDialog);
                            if (isFb == false) {
                                apiService.verifyPhoneNumber(phoneNumberString, countryCode, token).enqueue(new RequestCallback(REQ_VERIFY_NUMBER, new ServiceListener() {
                                    @Override
                                    public void onSuccess(JsonResponse jsonResp, String data) {
                                        if (!jsonResp.isOnline()) {
                                            commonMethods.showMessage(LoginActivity.this, dialog, data);
                                            return;
                                        }
                                        Log.e("JSON",jsonResp.getStrResponse());
                                        String otp = commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class).toString();
                                        String alreadyUser = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ALREADY_USER, String.class);
                                        String statusMessage = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.STATUS_MSG, String.class);
                                        StaticData.COUNTRY_CODE = countryCode;
                                        StaticData.PHONE_NUMBER = phoneNumberString;
                                        StaticData.ALREADY_USER = alreadyUser;
                                        checkAlreadyUser();
                                    }

                                    @Override
                                    public void onFailure(JsonResponse jsonResp, String data) {
                                        commonMethods.hideProgressDialog();
                                        commonMethods.showMessage(LoginActivity.this, dialog, data);
                                    }
                                }));
                            } else {
                              facebookSignup();
                            }
                        }
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        // Handle Error
                    }
                });
            }

        }
    }
    private void getData(){
        apiService.getUserData(sessionManager.getToken()).enqueue(new RequestCallback(1, new ServiceListener() {
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

            @Override
            public void onFailure(JsonResponse jsonResp, String data) {

            }
        }));
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
        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);






    }
    private void facebookSignup(){
        sessionManager.setFirstTime("Yes");
        commonMethods.showProgressDialog(LoginActivity.this, customDialog);
        //hashMap.put("phone_number", sessionManager.getPhoneNumber());
        //hashMap.put("country_code", sessionManager.getCountryCode());
        hashMap.put("phone_number", sessionManager.getPhoneNumber());
        hashMap.put("country_code", sessionManager.getCountryCode());
        hashMap.put("fb_token",AccessToken.getCurrentAccessToken().getToken());
        apiService.fbPhoneSignup(hashMap).enqueue(new RequestCallback(new ServiceListener() {
            @Override
            public void onSuccess(JsonResponse jsonResp, String data) {
                commonMethods.hideProgressDialog();
                if (!jsonResp.isOnline()) {
                    commonMethods.showMessage(LoginActivity.this, dialog, data);
                    return;
                }

                if (jsonResp.isSuccess()) {
                    onSuccessLogin(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(LoginActivity.this, dialog, jsonResp.getStatusMsg());
                }
            }

            @Override
            public void onFailure(JsonResponse jsonResp, String data) {
                commonMethods.hideProgressDialog();
                commonMethods.showMessage(LoginActivity.this, dialog, data);
            }
        }));
    }
    private void checkAlreadyUser(){
        boolean isAlreadyUser=Boolean.parseBoolean(StaticData.ALREADY_USER);
        if (isAlreadyUser){
            commonMethods.showProgressDialog(LoginActivity.this,customDialog);
            apiService.login(sessionManager.getPhoneNumber(), sessionManager.getCountryCode()).enqueue(new RequestCallback(new ServiceListener() {
                @Override
                public void onSuccess(JsonResponse jsonResp, String data) {
                    commonMethods.hideProgressDialog();
                    if (!jsonResp.isOnline()){
                        commonMethods.showMessage(LoginActivity.this,dialog,data);
                    }
                    if (jsonResp.isSuccess()) {
                         onSuccessLogin(jsonResp);
                    } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                        commonMethods.showMessage(LoginActivity.this, dialog, jsonResp.getStatusMsg());
                    }
                }

                @Override
                public void onFailure(JsonResponse jsonResp, String data) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(LoginActivity.this, dialog, data);
                }
            }));
        }else{
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }
    private void onSuccessLogin(JsonResponse jsonResp) {
        SignUpModel signUpModel = new Gson().fromJson(jsonResp.getStrResponse(), SignUpModel.class);
        if (signUpModel != null) {
            if (!TextUtils.isEmpty(signUpModel.getAccessToken())) {
                sessionManager.setToken(signUpModel.getAccessToken());
            }
            if (!TextUtils.isEmpty(signUpModel.getUserImageUrl())) {
                sessionManager.setProfileImg(signUpModel.getUserImageUrl());
            }
            sessionManager.setUserId(signUpModel.getUserId());
            getData();

        }
    }
    //Create FB KeyHash
    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                System.out.println("hash key value" + something);
                Log.i("hash key", something);
                Log.i("something","something");
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

    }
}
