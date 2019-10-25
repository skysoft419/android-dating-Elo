package ello.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category EnlargeProfileActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.adapters.profile.EnlargeSliderAdapter;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.SessionManager;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.MyProfileModel;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.DropDownClickListener;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;
import ello.utils.RequestCallback;
import ello.views.action.ReportUserActivity;
import ello.views.customize.CirclePageIndicator;
import ello.views.customize.CustomDialog;
import ello.views.customize.IgniterViewPager;

import static ello.utils.Enums.REQ_GET_OTHER_PROFILE;

/*****************************************************************
 User or other user view profile and show other details also
 ****************************************************************/
public class EnlargeProfileActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener {

    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    @Inject
    ImageUtils imageUtils;
    DropDownClickListener listener = new DropDownClickListener() {
        @Override
        public void onDropDrownClick(String value) {
            switch (value) {
                case Constants.MUTE_NOTIFICATION:
                    break;
                case Constants.REPORT:
                    break;
                case Constants.UNMATCH:
                    break;
                default:
                    break;
            }
        }
    };
    private CustomTextView tv;
    private IgniterViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    private CustomTextView tvLikedUserIcon,tvReportUser,tvUserNameAge, tvShareIcon, tvMenuIcon, tvDesignation, tvProfession, tvLocation, tvAbout, tvConnectedFriend;
    private CustomTextView tvRecommendFriend, tvRecommendDescription, tvToFriend, tvEditProfileIcon;
    private LinearLayout lltDesignation, lltProfession, lltLocation, lltAbout, lltRecommend, lltConnectFriends,lltReport;
    private RelativeLayout rltBottomIcon, rtl_arrow_down,rltViewPager;
    private View viewBottom, viewCenter;
    private RelativeLayout rltLike, rltSuperLike, rltUnLike;
    private int navType = 0, userId;
    private AlertDialog dialog;
    private MyProfileModel myProfileModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enlarge_profile_activity);
        AppController.getAppComponent().inject(this);
        initView();
        getIntentValues();
        initPageIndicator();
    }

    private void initView() {
        rltViewPager=findViewById(R.id.rlt_view_pager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.cpi_enlarge_profile);
        viewPager = (IgniterViewPager) findViewById(R.id.vp_enlarge_profile);

        viewPager.setAdapter(new EnlargeSliderAdapter(this));
        pageIndicator.setViewPager(viewPager, 0);

        tvLikedUserIcon = (CustomTextView) findViewById(R.id.tv_liked_user_icon);
        tvShareIcon = (CustomTextView) findViewById(R.id.tv_share_icon);
        tvMenuIcon = (CustomTextView) findViewById(R.id.tv_menu_icon);

        rltLike = (RelativeLayout) findViewById(R.id.rlt_like_lay);
        rltSuperLike = (RelativeLayout) findViewById(R.id.rlt_superlike_lay);
        rltUnLike = (RelativeLayout) findViewById(R.id.rlt_unlike_lay);
        rtl_arrow_down = (RelativeLayout) findViewById(R.id.rtl_arrow_down);
        tvEditProfileIcon = (CustomTextView) findViewById(R.id.tv_profile_edit_icon);

        tvUserNameAge = (CustomTextView) findViewById(R.id.tv_user_name_age);
        tvDesignation = (CustomTextView) findViewById(R.id.tv_designation);
        tvProfession = (CustomTextView) findViewById(R.id.tv_profession);
        tvLocation = (CustomTextView) findViewById(R.id.tv_location);
        tvAbout = (CustomTextView) findViewById(R.id.tv_about_user);

        tvRecommendFriend = (CustomTextView) findViewById(R.id.tv_friends_count);
        tvRecommendDescription = (CustomTextView) findViewById(R.id.tv_friends_description);
        tvConnectedFriend = (CustomTextView) findViewById(R.id.tv_connect_friends);
        tvToFriend = (CustomTextView) findViewById(R.id.tv_to_friends);

        lltDesignation = (LinearLayout) findViewById(R.id.llt_designation);
        lltProfession = (LinearLayout) findViewById(R.id.llt_profession);
        lltLocation = (LinearLayout) findViewById(R.id.llt_location);
        lltAbout = (LinearLayout) findViewById(R.id.llt_about);
        lltRecommend = (LinearLayout) findViewById(R.id.llt_recommend);
        lltConnectFriends = (LinearLayout) findViewById(R.id.llt_connect_friends);
        rltBottomIcon = (RelativeLayout) findViewById(R.id.rlt_bottom_icons);
        lltReport=findViewById(R.id.llt_report);
        tvReportUser=findViewById(R.id.tv_report_user);
        tvReportUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.FROMPROFILE=true;
                Intent intent=new Intent(EnlargeProfileActivity.this, ReportUserActivity.class);
                intent.putExtra("id",myProfileModel.getUserId().toString());
                startActivity(intent);
                finish();
            }
        });

        viewBottom = findViewById(R.id.view_bottom);
        viewCenter = findViewById(R.id.view_center);

        dialog = commonMethods.getAlertDialog(this);

        initClickListener();

        rltViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rltViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h=rltViewPager.getHeight();
                int width=rltViewPager.getWidth();
                int height=(int)Math.round(width*1.2);
                rltViewPager.getLayoutParams().height=height;
                rltViewPager.requestLayout();
                int h2=rltViewPager.getHeight();
                int i=0;
            }
        });
    }
    public static float dpFromPx(final Context context, final int px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    private void initClickListener() {
        tvShareIcon.setOnClickListener(this);
        tvMenuIcon.setOnClickListener(this);
        tvEditProfileIcon.setOnClickListener(this);
        lltRecommend.setOnClickListener(this);
        lltConnectFriends.setOnClickListener(this);
        rtl_arrow_down.setOnClickListener(this);
        rltLike.setOnClickListener(this);
        rltUnLike.setOnClickListener(this);
        rltSuperLike.setOnClickListener(this);
    }

    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navType = bundle.getInt("navType", 0);
            userId = bundle.getInt("userId");
            //getProfileDetails();
            setBtnVisibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileDetails();

    }

    private void setBtnVisibility() {
        switch (navType) {
            case 0:  // My Profile
                lltReport.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
                viewCenter.setVisibility(View.GONE);
                tvShareIcon.setVisibility(View.GONE);
                tvMenuIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                tvEditProfileIcon.setVisibility(View.VISIBLE);
                break;
            case 1:   // Igniter Profile
                lltReport.setVisibility(View.VISIBLE);
                tvLikedUserIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.VISIBLE);
                tvEditProfileIcon.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                break;
            case 3:   // Other Profile
                tvLikedUserIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                //lltConnectFriends.setVisibility(View.VISIBLE);
                lltConnectFriends.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
                tvEditProfileIcon.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void getProfileDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.otherProfileView(sessionManager.getToken(), userId).enqueue(new RequestCallback(REQ_GET_OTHER_PROFILE, this));
    }

    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(3 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(this, R.color.gray_text_color));
        pageIndicator.setFillColor(ContextCompat.getColor(this, R.color.color_accent));
        pageIndicator.setStrokeColor(ContextCompat.getColor(this, R.color.gray_text_color));
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing((float) (1.5 * density));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_icon:
                break;
            case R.id.tv_menu_icon:
                commonMethods.dropDownMenu(this, v, null, Constants.QUICK_MENU_TITLE, listener);
                break;
            case R.id.tv_profile_edit_icon:
                if (myProfileModel != null && !TextUtils.isEmpty(myProfileModel.getName())) {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    intent.putExtra("userName", myProfileModel.getName());
                    startActivity(intent);
                }
                break;
            case R.id.rtl_arrow_down:
                onBackPressed();
                break;
            case R.id.rlt_unlike_lay:
                sessionManager.setSwipeType("UnLike");
                onBackPressed();
                break;
            case R.id.rlt_like_lay:
                sessionManager.setSwipeType("Like");
                onBackPressed();
                break;
            case R.id.rlt_superlike_lay:
                sessionManager.setSwipeType("SuperLike");
                onBackPressed();
                break;
            case R.id.llt_recommend:
                String message = "";
                String title = getResources().getString(R.string.recommend_friend) + " " + myProfileModel.getName() + ", " + getResources().getString(R.string.recommend_to_friends);
                if (myProfileModel.getName().equals("women")) {
                    message = getResources().getString(R.string.share_girl) + " \n\n" + getResources().getString(R.string.share_url);
                } else {
                    message = getResources().getString(R.string.share_guy) + " \n\n" + getResources().getString(R.string.share_url);
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, title));
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_OTHER_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetOtherProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetOtherProfile(JsonResponse jsonResp) {
        myProfileModel = gson.fromJson(jsonResp.getStrResponse(), MyProfileModel.class);
        if (myProfileModel != null) {
            updateView();
        }
    }

    private void updateView() {

        if (myProfileModel == null) return;

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(myProfileModel.getName())) {
            sb.append(myProfileModel.getName());
            if (myProfileModel.getAge() > 0) {
                sb.append(", ");
                sb.append(myProfileModel.getAge());
            }
            tvUserNameAge.setText(sb.toString());
            tvRecommendFriend.setVisibility(View.VISIBLE);
            tvToFriend.setVisibility(View.VISIBLE);
            tvReportUser.setText("REPORT "+myProfileModel.getName());
            tvRecommendFriend.setText(getResources().getString(R.string.recommend_friend) + " " + myProfileModel.getName());
        }

        if (!TextUtils.isEmpty(myProfileModel.getCollege())) {
            tvProfession.setText(myProfileModel.getCollege());
            lltProfession.setVisibility(View.VISIBLE);
        } else {
            lltProfession.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getWork())) {
            if (myProfileModel.getJobTitle().equals("")) {
                tvDesignation.setText(myProfileModel.getWork());
            } else {
                tvDesignation.setText(myProfileModel.getJobTitle() + " at " + myProfileModel.getWork());
            }
            lltDesignation.setVisibility(View.VISIBLE);
        } else {
            lltDesignation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && !TextUtils.isEmpty(myProfileModel.getDistanceType())) {
            if (myProfileModel.getDistanceType().contains("km")) {
                if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && Float.valueOf(myProfileModel.getKilometer()) < 1)
                    tvLocation.setText(getResources().getString(R.string.less_km_away));
                else
                    tvLocation.setText(myProfileModel.getKilometer() + " " + getResources().getString(R.string.km_away));
            } else {
                if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && Float.valueOf(myProfileModel.getKilometer()) < 1)
                    tvLocation.setText(getResources().getString(R.string.less_mi_away));
                else
                    tvLocation.setText(myProfileModel.getKilometer() + " " + getResources().getString(R.string.miles_away));
            }

            lltLocation.setVisibility(View.VISIBLE);
        } else {
            lltLocation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getAbout())) {
            tvAbout.setText(myProfileModel.getAbout());
            lltAbout.setVisibility(View.VISIBLE);
            viewCenter.setVisibility(View.VISIBLE);
        } else {
            lltAbout.setVisibility(View.GONE);
        }
        if (myProfileModel.getImages() != null && myProfileModel.getImages().size() > 0) {
            viewPager.setAdapter(new EnlargeSliderAdapter(this, myProfileModel.getImages()));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
    }
}
