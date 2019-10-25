package ello.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category ChatConversationActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.matches.MatchedUserProfile;
import ello.helpers.StaticData;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;

/*****************************************************************
 Matched profile show while swipe
 ****************************************************************/
public class MatchUsersActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    ImageUtils imageUtils;
    @Inject
    CommonMethods commonMethods;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    private CustomTextView tvMatchTitle, tvMatchDescription;
    private CircleImageView civMatchOne, civMatchTwo;
    private CustomButton btnSendMessage, btnKeepSwipe;
    private LinearLayout lltShareFriends;
    private String json = "";
    private ImageView matchedUserImageView;
    private MatchedUserProfile matchingProfile;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);

        setContentView(R.layout.match_dialog);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawableResource(R.color.transparent);

        initView();
        getIntentValues();
    }

    private void initView() {
        matchedUserImageView=findViewById(R.id.mvMatchedUser);
        animationView=findViewById(R.id.animationview);
        tvMatchTitle = (CustomTextView) findViewById(R.id.tv_matches);
        tvMatchDescription = (CustomTextView) findViewById(R.id.tv_matches);
        btnSendMessage = (CustomButton) findViewById(R.id.btn_send_message);
        btnKeepSwipe = (CustomButton) findViewById(R.id.btn_keep_swiping);

        civMatchOne = (CircleImageView) findViewById(R.id.civ_match_one);
        civMatchTwo = (CircleImageView) findViewById(R.id.civ_match_two);

        lltShareFriends = (LinearLayout) findViewById(R.id.llt_share_friends);

        btnKeepSwipe.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);
        lltShareFriends.setOnClickListener(this);
        animationView.setAnimation("matched.json");
        animationView.playAnimation();
        //Picasso.get().load(StaticData.editProfileModel.getImageList().get(0).getImageUrl()).fit().centerCrop().noPlaceholder().into(matchedUserImageView);


    }

    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !TextUtils.isEmpty(bundle.getString("json"))) {
            json = bundle.getString("json");
            updateView();
        }else{
        updateView();
        }
    }

    private void updateView() {
        matchingProfile = gson.fromJson(json, MatchedUserProfile.class);
        if (matchingProfile != null) {
            if (!TextUtils.isEmpty(matchingProfile.getName())) {
                tvMatchDescription.setText(String.format(getString(R.string.match_description), matchingProfile.getName()));
            }
            if (!TextUtils.isEmpty(matchingProfile.getImages())) {
                imageUtils.loadCircleImage(this, civMatchTwo, matchingProfile.getImages());
            }
            if (!TextUtils.isEmpty(sessionManager.getProfileImg())) {
                imageUtils.loadCircleImage(this, civMatchOne, sessionManager.getProfileImg());
            }
            Picasso.get().load(matchingProfile.getImages()).fit().centerCrop().noPlaceholder().into(matchedUserImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("isKeepSwipe", false);
                returnIntent.putExtra("matchId", matchingProfile.getMatchId());
                returnIntent.putExtra("userId", Integer.valueOf(matchingProfile.getMatchedUserId()));
                //returnIntent.putExtra("userId", matchingProfile.getUserId());
                setResult(RESULT_OK, returnIntent);
                if (!this.isFinishing()) finish();
                break;
            case R.id.btn_keep_swiping:
                Intent intent = new Intent();
                intent.putExtra("isKeepSwipe", true);
                intent.putExtra("matchId", matchingProfile.getMatchId());
                intent.putExtra("userId", Integer.valueOf(matchingProfile.getMatchedUserId()));
                setResult(RESULT_OK, intent);
                if (!this.isFinishing()) finish();
                break;
            case R.id.llt_share_friends:
                break;
            default:
                break;
        }
    }
}
