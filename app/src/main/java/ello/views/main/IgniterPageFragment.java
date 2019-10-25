package ello.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category IgniterPageFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.autofill.FieldClassification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.obs.CustomTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.adapters.action.AdapterCallback;
import ello.adapters.matches.MatchesSwipeAdapter;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.chat.MatchedProfileModel;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.MatchProfilesModel;
import ello.datamodels.main.UserDetailModel;
import ello.datamodels.matches.MatchesResponse;
import ello.datamodels.matches.MatchingProfile;
import ello.helpers.StaticData;
import ello.iaputils.IabBroadcastReceiver;
import ello.iaputils.IabHelper;
import ello.iaputils.IabResult;
import ello.iaputils.Inventory;
import ello.iaputils.Purchase;
import ello.interfaces.ActivityListener;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.swipedeck.SwipeDeck;
import ello.swipedeck.layouts.SwipeRelativeLayout;
import ello.utils.CircleTransformation;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;
import ello.utils.MyLocation;
import ello.utils.RequestCallback;
import ello.views.chat.ChatConversationActivity;
import ello.views.chat.MatchUsersActivity;
import ello.views.customize.CustomDialog;
import ello.views.customize.CustomDialogBox;
import ello.views.customize.RippleBackground;

import static ello.utils.Enums.MATCH_LIKE;
import static ello.utils.Enums.MATCH_NOPE;
import static ello.utils.Enums.MATCH_REWIND;
import static ello.utils.Enums.MATCH_SUPER_LIKE;
import static ello.utils.Enums.REQ_GET_HOME;
import static ello.utils.Enums.REQ_SHOW_ALL_MATCHES;
import static ello.utils.Enums.REQ_SWIPE_MATCH;
import static ello.utils.Enums.REQ_UPDATE_BOOST_USER;
import static ello.utils.Enums.REQ_UPDATE_DEVICE_ID;
import static ello.utils.Enums.REQ_UPDATE_LOCATION;

/*****************************************************************
 Application home page (Swipe profile)
 ****************************************************************/
public class IgniterPageFragment extends Fragment implements View.OnClickListener, ServiceListener, IabBroadcastReceiver.IabBroadcastListener {
    // Debug tag, for logging
    static final String TAG = "Boost In App Purchase";
    // SKU for our subscription (infinite Boost)
    static String SKU_INFINITE_ONE_BOOST = "";
    static String SKU_INFINITE_FIVE_BOOST = "";
    static String SKU_INFINITE_TEN_BOOST = "";
    static String SKU_INFINITE_5_SL = "";
    static String SKU_INFINITE_25_SL = "";
    static String SKU_INFINITE_60_SL = "";
    static String SKU_INFINITE_1_IP = "";
    static String SKU_INFINITE_6_IP = "";
    static String SKU_INFINITE_12_IP = "";
    CountDownTimer countDownTimer = null;
    CustomDialogBox custom;
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
    @Inject
    RunTimePermission runTimePermission;
    int totalSuperLikeCount = 0, remainingSuperLikeCount = 0, remainingLikeCount = 0, totalLikeCount = 0, subscriptionId = 0;
    String isLikeLimited;
    String remainingSuperLikeHours;
    int totalBoostCount = 0, remainingBoostCount = 0;
    String remainingBoostHours, remainingBoostDay;
    boolean rewind = false;
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    String payload = "";
    // Does the user have an active subscription to the infinite Boost plan?
    boolean mSubscribedToInfiniteBoost = false, mSubscribedToInfiniteSuperLike = false;
    // Does the user have an active subscription to the infinite Igniter Plus plan?
    boolean mSubscribedToPlus = false;
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    // Tracks the currently owned infinite Boost SKU, and the options in the Manage dialog
    String mInfiniteBoostSku = "", mInfiniteSuperLikeSku = "", mInfinitePlusSku = "";
    Purchase oneBoost, fiveBoost, tenBoost;
    Purchase SL5, SL25, SL60;
    Purchase IP1, IP6, IP12;
    Inventory inventory;
    private int positions=0;
    private AdapterCallback adapterCallback;
    private int totalNumberOfCards=0;
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventorys) {
            Log.e(TAG, "Query inventory finished.");
            inventory = inventorys;
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.e(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // First find out which subscription is auto renewing
            oneBoost = inventory.getPurchase(SKU_INFINITE_ONE_BOOST);
            fiveBoost = inventory.getPurchase(SKU_INFINITE_FIVE_BOOST);
            tenBoost = inventory.getPurchase(SKU_INFINITE_TEN_BOOST);

            if (oneBoost != null && oneBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_ONE_BOOST;
                mAutoRenewEnabled = true;
            } else if (fiveBoost != null && fiveBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_FIVE_BOOST;
                mAutoRenewEnabled = true;
            } else if (tenBoost != null && tenBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_TEN_BOOST;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteBoostSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteBoost = (oneBoost != null && verifyDeveloperPayload(oneBoost))
                    || (fiveBoost != null && verifyDeveloperPayload(fiveBoost)
                    || (tenBoost != null && verifyDeveloperPayload(tenBoost)));
            Log.e(TAG, "Original JSON 123 " + mSubscribedToInfiniteBoost);
            Log.e(TAG, "User Boost" + (mSubscribedToInfiniteBoost ? "HAS" : "DOES NOT HAVE")
                    + " infinite Boost subscription.");


            SL5 = inventory.getPurchase(SKU_INFINITE_5_SL);
            SL25 = inventory.getPurchase(SKU_INFINITE_25_SL);
            SL60 = inventory.getPurchase(SKU_INFINITE_60_SL);


            if (SL5 != null && SL5.isAutoRenewing()) {
                mInfiniteSuperLikeSku = SKU_INFINITE_5_SL;
                mAutoRenewEnabled = true;
            } else if (SL25 != null && SL25.isAutoRenewing()) {
                mInfiniteSuperLikeSku = SKU_INFINITE_25_SL;
                mAutoRenewEnabled = true;
            } else if (SL60 != null && SL60.isAutoRenewing()) {
                mInfiniteSuperLikeSku = SKU_INFINITE_60_SL;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteSuperLikeSku = "";
                mAutoRenewEnabled = false;
            }


            IP1 = inventory.getPurchase(SKU_INFINITE_1_IP);
            IP6 = inventory.getPurchase(SKU_INFINITE_6_IP);
            IP12 = inventory.getPurchase(SKU_INFINITE_12_IP);

            if (IP1 != null && IP1.isAutoRenewing()) {
                mInfinitePlusSku = SKU_INFINITE_1_IP;
                mAutoRenewEnabled = true;
            } else if (IP6 != null && IP6.isAutoRenewing()) {
                mInfinitePlusSku = SKU_INFINITE_6_IP;
                mAutoRenewEnabled = true;
            } else if (IP12 != null && IP12.isAutoRenewing()) {
                mInfinitePlusSku = SKU_INFINITE_12_IP;
                mAutoRenewEnabled = true;
            } else {
                mInfinitePlusSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteSuperLike = (SL5 != null && verifyDeveloperPayload(SL5))
                    || (SL25 != null && verifyDeveloperPayload(SL25)
                    || (SL60 != null && verifyDeveloperPayload(SL60)));

            mSubscribedToPlus = (IP1 != null && verifyDeveloperPayload(IP1))
                    || (IP6 != null && verifyDeveloperPayload(IP6)
                    || (IP12 != null && verifyDeveloperPayload(IP12)));
        }
    };
    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.e(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.e(TAG, "Consumption successful. Provisioning.");
                mSubscribedToInfiniteBoost = false;
                mSubscribedToInfiniteSuperLike = false;
                mSubscribedToPlus = false;

                //alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            } else {
                complain("Error while consuming: " + result);
            }

            Log.e(TAG, "End consumption flow.");
        }
    };
    private View view;
    private ActivityListener listener;
    private Resources res;
    private HomeActivity mActivity;
    private ImageView ivReload, ivBoost, ivLike, ivSuperLike, ivUnLike;
    private RelativeLayout rltReload, rltBoost, rltLike, rltSuperLike, rltUnLike;
    private CustomTextView tvClose, tvMatch;
    private ImageView civProfileImg;
    private RippleBackground rippleBackground;
    private UserDetailModel userDetailModel;
    private AlertDialog dialog;
    private ArrayList<MatchingProfile> matchingProfilesList = new ArrayList<>();
    private ArrayList<MatchingProfile> originMatchingProfilesList = new ArrayList<>();
    private MatchProfilesModel matchProfilesModel;
    private MatchesSwipeAdapter matchesSwipeAdapter;
    private SwipeDeck cardStack;
    private SwipeRelativeLayout swipeLayout;
    private LinearLayout textLayout;
    private double latitude = 0, longitude = 0;
    private TextView searchTextView;
    private LinearLayout enableDiscoveryLayout,enableLocationLayout;
    private TextView enableDiscoveryBtn,enableLocationBtn;
    private boolean isGps=false;
    private MatchingProfile lastProfile;
    /**
     * Get user current location
     */
    MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            System.out.println("Check4");
            if (location == null) return;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            System.out.println("Check5");
//            latitude = 39.3;
//            longitude = -110.5;
            apiService.insertLocation(sessionManager.getToken(), latitude, longitude, "update").enqueue(new RequestCallback(REQ_UPDATE_LOCATION, IgniterPageFragment.this));
        }
    };
    private boolean sawLike = true, sawUnLike = true, sawSuperLike = true;
    private String swipeMethod;
    private boolean isPermissionGranted = false;
    private Tovuti tovuti;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapterCallback=(AdapterCallback)context;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        Tovuti.from(getActivity()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                if (isConnected){
                    if (matchingProfilesList.size() == 0){
                        showMatchProfile();
                    }
                }else{
                    if (matchingProfilesList.size() == 0){
                        noInternet();
                    }
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Tovuti.from(getActivity()).stop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            listener = (ActivityListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Profile must implement ActivityListener");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        AppController.getAppComponent().inject(this);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.igniter_fragment, container, false);
        }

        SKU_INFINITE_ONE_BOOST = getResources().getString(R.string.iap_boost_1);
        SKU_INFINITE_FIVE_BOOST = getResources().getString(R.string.iap_boost_5);
        SKU_INFINITE_TEN_BOOST = getResources().getString(R.string.iap_boost_10);

        SKU_INFINITE_5_SL = getResources().getString(R.string.iap_superlike_5);
        SKU_INFINITE_25_SL = getResources().getString(R.string.iap_superlike_25);
        SKU_INFINITE_60_SL = getResources().getString(R.string.iap_superlike_60);

        SKU_INFINITE_1_IP = getResources().getString(R.string.iap_plus_1);
        SKU_INFINITE_6_IP = getResources().getString(R.string.iap_plus_6);
        SKU_INFINITE_12_IP = getResources().getString(R.string.iap_plus_12);
        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        setupHelper();
        sessionManager.setSettingUpdate(false);
        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        swipeLayout = (SwipeRelativeLayout) view.findViewById(R.id.swipeLayout);
        ivReload = (ImageView) view.findViewById(R.id.iv_reload);
        ivLike = (ImageView) view.findViewById(R.id.iv_like);
        ivSuperLike = (ImageView) view.findViewById(R.id.iv_superlike);
        ivUnLike = (ImageView) view.findViewById(R.id.iv_unlike);
        ivBoost = (ImageView) view.findViewById(R.id.iv_boost);

        searchTextView=view.findViewById(R.id.searchTV);
        rltReload = (RelativeLayout) view.findViewById(R.id.rlt_reload_lay);
        rltLike = (RelativeLayout) view.findViewById(R.id.rlt_like_lay);
        rltSuperLike = (RelativeLayout) view.findViewById(R.id.rlt_superlike_lay);
        rltUnLike = (RelativeLayout) view.findViewById(R.id.rlt_unlike_lay);
        rltBoost = (RelativeLayout) view.findViewById(R.id.rlt_boost_lay);
        dialog = commonMethods.getAlertDialog(mActivity);
        civProfileImg =  view.findViewById(R.id.civ_profile_image);
        textLayout=view.findViewById(R.id.textLayout);
        enableDiscoveryBtn=view.findViewById(R.id.tv_enablediscovery);
        enableDiscoveryLayout=view.findViewById(R.id.llenablediscovery);
        enableLocationBtn=view.findViewById(R.id.tv_enablelocation);
        enableLocationLayout=view.findViewById(R.id.llenablelocation);

        tvMatch = (CustomTextView) view.findViewById(R.id.tv_match);
        if (!TextUtils.isEmpty(sessionManager.getProfileImg())) {
            if (StaticData.editProfileModel != null) {
                Picasso.get()
                        .load(StaticData.editProfileModel.getImageList().get(0).getImageUrl())
                        .transform(new CircleTransformation())
                        .fit().centerCrop()
                        .into(civProfileImg);
            }

            // imageUtils.loadCircleImage(mActivity, civProfileImg, sessionManager.getProfileImg());
        }
        textLayout.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        rltSuperLike.setEnabled(false);
        rltUnLike.setEnabled(false);
        rltLike.setEnabled(false);
        rltBoost.setEnabled(true);
        rltReload.setEnabled(false);
        showDiscoveryScreen();
        textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSetting();
            }
        });
        enableDiscoveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToSetting();
            }
        });
        enableLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGps){
                    enableGpsDailog();
                }else {
                    showLocationPermissionDailog(Constants.PERMISSIONS_LOCATION);
                }
            }
        });
        ivSuperLike.setEnabled(false);
        ivUnLike.setEnabled(false);
        ivLike.setEnabled(false);
        ivBoost.setEnabled(true);
        ivReload.setEnabled(false);

        rltReload.setOnClickListener(this);
        rltBoost.setOnClickListener(this);
        rltLike.setOnClickListener(this);
        rltUnLike.setOnClickListener(this);
        rltSuperLike.setOnClickListener(this);

        cardStack.setClickable(true);
        cardStack.setVisibility(View.INVISIBLE);
        cardStack.setLeftImage(R.id.like_tv);
        cardStack.setRightImage(R.id.nope_tv);
        cardStack.setBottomImage(R.id.super_like_tv);
        cardStack.setUpImage(R.id.reported_tv);
        swipeLayout.bringToFront();
        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long itemId) {
                rewind(true, 1);
                //card swiped left
                int userId = 0;
                if (sawUnLike && sessionManager.getIsSwipeUnLike()) {
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        sawUnLike = false;
                        sessionManager.setIsSwipeUnLike(false);
                        String msg = "", btnText = "", title = "";
                        String name = getUserName((int) itemId);
                        msg = String.format("Swipe left to Pass"+getEmojiByUnicode(0x2639), name);
                        title = "PASS?";
                        btnText = "PASS";
                        showDialog(userId, MATCH_NOPE, getJson((int) itemId), "swipe", title, msg, btnText, 0);
                    }
                } else {
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        swipeProfile(userId, MATCH_NOPE, getJson((int) itemId));
                        sawUnLike = true;
                    }
                }
            }

            @Override
            public void cardSwipedRight(long itemId) {
                /*rewind(true,1);
                //card swiped right
                int userId=0;
                if(sawLike&&sessionManager.getIsSwipeLike()) {
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        sawLike=false;
                        sessionManager.setIsSwipeLike(false);
                        String msg = "", btnText = "", title = "";
                        String name=getUserName((int)itemId);
                        msg = String.format(res.getString(R.string.alert_like_msg),name);
                        title = res.getString(R.string.alert_like_title);
                        btnText = res.getString(R.string.like);
                        showDialog(userId,MATCH_LIKE,getJson((int) itemId),"swipe",title, msg, btnText, 1);
                    }
                }else{
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        swipeProfile(userId, MATCH_LIKE, getJson((int) itemId));
                        sawLike = true;
                    }
                }*/
                rewind(true, 1);
                //card swiped up
                int userId = 0;
                if (sawLike && sessionManager.getIsSwipeLike()) {
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        sawLike = false;
                        sessionManager.setIsSwipeLike(false);
                        String msg = "", btnText = "", title = "";
                        String name = getUserName((int) itemId);
                        msg = String.format("Swipe right to Keep!", name);
                        title = "KEEP?";
                        btnText = "KEEP";
                        showDialog(userId, MATCH_LIKE, getJson((int) itemId), "swipe", title, msg, btnText, 1);
                    }
                } else {
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        like(userId, itemId, "");
                        sawLike = true;
                    }
                }
            }

            @Override
            public void cardSwipedUp(long itemId) {
                if (StaticData.FROMPROFILE&&StaticData.REPORTED){
                    int userId = 0;
                    userId = getUserId((int) itemId);
                    if (userId > 0) {
                        swipeProfile(userId, MATCH_NOPE, getJson((int) itemId));
                        sawUnLike = true;
                        StaticData.FROMPROFILE=false;
                        StaticData.REPORTED=false;
                    }
                }else {
                    int value = StaticData.editProfileModel.getRemainingLikes();
                    if (StaticData.editProfileModel.getRemainingLikes() > 0) {
                        StaticData.editProfileModel.setRemainingLikes(StaticData.editProfileModel.getRemainingLikes() - 1);
                        int remain=StaticData.editProfileModel.getRemainingLikes();
                        rewind(true, 1);
                        int userId = 0;
                        System.out.println("sawSuperLike " + sawSuperLike);
                        System.out.println("sawSuperLike Check  " + sessionManager.getIsSwipeSuperLike());
                        if (sawSuperLike && sessionManager.getIsSwipeSuperLike()) {
                            userId = getUserId((int) itemId);
                            if (userId > 0) {
                                sawSuperLike = false;
                                sessionManager.setIsSwipeSuperLike(false);
                                String msg = "", btnText = "", title = "";
                                String name = getUserName((int) itemId);
                                int i = 0x1F609;
                                msg = String.format("Swipe up to Favourite" + getEmojiByUnicode(i), name);
                                title = String.format(res.getString(R.string.alert_super_like_title), name);
                                btnText = res.getString(R.string.super_like_btn);
                                showDialog(userId, MATCH_SUPER_LIKE, getJson((int) itemId), "swipe", title, msg, btnText, 2);
                            }
                        } else {
                            userId = getUserId((int) itemId);
                            if (userId > 0) {
                                superLike(userId, itemId, "");
                                sawSuperLike = true;
                            }
                        }
                    } else {
                        custom = new CustomDialogBox("Oh, ello there...", "You get two favourites a day sparky. Use them wisely, meanwhile swipe that looker to the right!", "Ok", "Cancel", new CustomDialogBox.btnAllowClick() {
                            @Override
                            public void clicked() {
                                if(totalNumberOfCards != 1) {
                                    cardStack.unSwipeCard();
                                }else{
                                    undoLastCard();
                                }
                                //swipeProfile(-1, MATCH_SUPER_LIKE, "");

                            }
                        }, new CustomDialogBox.btnDenyClick() {
                            @Override
                            public void clicked() {
                                if(totalNumberOfCards != 1) {
                                    cardStack.unSwipeCard();
                                }else{
                                   undoLastCard();
                                }
                                //swipeProfile(-1, MATCH_SUPER_LIKE, "");
                            }
                        });
                        custom.show(getActivity().getSupportFragmentManager(), "");
                    }
                }
            }
            @Override
            public void cardSwipedDown(long itemId) {

            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }
        });

        //checkAllPermission(Constants.PERMISSIONS_STORAGE);

        rippleBackground = (RippleBackground) view.findViewById(R.id.rb_background);
        rippleBackground.startRippleAnimation();

        //getHomePage();
        updateDeviceId();

        showMatchProfile();
    }
    private void undoLastCard(){
        MatchingProfile matchingProfile=matchingProfilesList.get(matchingProfilesList.size()-1);
        matchingProfilesList.clear();
        cardStack.clear();
        matchingProfilesList.add(matchingProfile);
        showCards();
    }
    private void moveToSetting(){
        StaticData.SETTING=1;
        adapterCallback.onClick(1);
    }
    /**
     * Show discovery screen
     */
    public void showDiscoveryScreen(){
        if (!StaticData.settingsModel.getShowMe().equals("yes")){
            enableDiscoveryLayout.setVisibility(View.VISIBLE);
            //swipeLayout.setVisibility(View.GONE);
            enableDiscoveryLayout.bringToFront();
        }else{
            enableDiscoveryLayout.setVisibility(View.GONE);
        }
    }
    /**
     * Emoji Hex to String
     */
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    /**
     *
     * @param newMatchProfileModel
     */
    public void setFavouriteCard(NewMatchProfileModel newMatchProfileModel){
        MatchingProfile matchingProfile=new MatchingProfile();
        matchingProfile.setAge(newMatchProfileModel.getAge());
        matchingProfile.setAllImages(newMatchProfileModel.getAllImages());
        matchingProfile.setCollege(newMatchProfileModel.getCollege());
        matchingProfile.setWork(newMatchProfileModel.getWork());
        matchingProfile.setUserId(newMatchProfileModel.getUserId());
        matchingProfile.setName(newMatchProfileModel.getUserName());
        matchingProfile.setKilometer(newMatchProfileModel.getKilometer());
        for (int i=0;i<originMatchingProfilesList.size();i++){
            if (originMatchingProfilesList.get(i).getUserId().equals(matchingProfile.getUserId())){
                originMatchingProfilesList.remove(i);
            }
        }
        if (matchingProfilesList.size() != 0) {
                ArrayList<MatchingProfile> temList = new ArrayList<>();
                temList.addAll(originMatchingProfilesList);
                temList.add(0,matchingProfile);
                originMatchingProfilesList.clear();
                matchingProfilesList.clear();
                cardStack.clear();
                matchingProfilesList.addAll(temList);
                originMatchingProfilesList.addAll(temList);
                showCards();

        }else{
            ArrayList<MatchingProfile> temList = new ArrayList<>();
            temList.addAll(matchingProfilesList);
            temList.add(matchingProfile);
            matchingProfilesList.clear();
            cardStack.clear();
            matchingProfilesList.addAll(temList);
            showCards();
        }



    }
    /**
     * Enable disable rewind view
     */
    public void rewind(boolean rewindc, int type) {

        // type 0 for rewind click action
        // type 1 for rewind disable action
        if (type == 0) {
            cardStack.unSwipeCard();
            swipeProfile(getUserId((int) cardStack.getTopCardItemId()), MATCH_REWIND, "");
        } else if (type == 2) {
            cardStack.unSwipeCard();
            reloadSwipe();
        }
        if (sessionManager.getIsOrder() && rewindc) {
            rltReload.setEnabled(true);
            ivReload.setEnabled(true);
            rewind = true;
        } else {
            //rltReload.setEnabled(false);
            if (!sessionManager.getIsOrder()) {
                ivReload.setEnabled(true);
                rltReload.setEnabled(true);
            } else {
                ivReload.setEnabled(false);
                rltReload.setEnabled(false);
            }
            rewind = false;
        }
    }

    /**
     * Declare variable for layout views
     */
    private void init() {
        if (listener == null) return;

        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (HomeActivity) getActivity();
    }

    /**
     * Get swipe user name using position
     */
    private String getUserName(int pos) {

        String Name = "";
        if (pos < matchingProfilesList.size() && matchingProfilesList.get(pos) != null) {
            Name = matchingProfilesList.get(pos).getName();
        }
        if (pos == matchingProfilesList.size() - 1) {
            noPeople();
        }
        return Name;
    }

    /**
     * Get swipe user id using position
     */
    private int getUserId(int pos) {
        int userId = 0;

        if (pos < matchingProfilesList.size() && matchingProfilesList.get(pos) != null) {
            userId = matchingProfilesList.get(pos).getUserId();
        }
        if (pos == matchingProfilesList.size() - 1) {
            noPeople();
        }
        return userId;
    }

    /**
     * Get json value
     */
    private String getJson(int pos) {
        String json = "";
        if (pos < matchingProfilesList.size() && matchingProfilesList.get(pos) != null) {
            json = gson.toJson(matchingProfilesList.get(pos), MatchingProfile.class);
        }
        return json;
    }

    /**
     * Call home page API
     */
    private void getHomePage() {
        apiService.homePage(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_HOME, this));
    }

    /**
     * Update device id using API
     */
    private void updateDeviceId() {
        if (!sessionManager.getToken().equals("")) {
            if (sessionManager.getDeviceId().equals("")) {
                sessionManager.setDeviceId(FirebaseInstanceId.getInstance().getToken());
            }
            apiService.updateDeviceId(sessionManager.getToken(), "2", sessionManager.getDeviceId()).enqueue(new RequestCallback(REQ_UPDATE_DEVICE_ID, this));
        }
    }

    /**
     * Default on click function
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        String msg = "", btnText = "", title = "";

        if(custom != null && custom.isAdded())
            return;

        if(cardStack.getDeck().getFront() != null && cardStack.getDeck().getFront().isCardDragging ||
                cardStack.getDeck().getSecond() != null && cardStack.getDeck().getSecond().isCardDragging)
            return;

        switch (v.getId()) {
            case R.id.rlt_reload_lay:
                if (sessionManager.getIsOrder() && rewind) {
                    rewind(false, 0);
                } else if (!sessionManager.getIsOrder()) {
                    /*
                    intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "plus");
                    startActivity(intent);*/
                } else {

                }
                break;
            case R.id.rlt_boost_lay:
                Log.e(TAG, "Original JSON " + mSubscribedToInfiniteBoost);
                Log.e(TAG, "Original JSON " + String.valueOf(mSubscribedToInfiniteBoost));
                Log.e(TAG, "Original JSON " + String.valueOf(sessionManager.getRemainingBoost()));
                boostUser();

                break;
            case R.id.rlt_superlike_lay:
               /* if (sessionManager.getRemainingSuperLikes() <= 0) {
                    if (sessionManager.getRemainingSuperLikes() == 0 && mSubscribedToInfiniteSuperLike) {
                        mSubscribedToInfiniteSuperLike = false;
                        //complain("Super Like already applied...");
                        if (SL5 != null && verifyDeveloperPayload(SL5))
                            consumed(SKU_INFINITE_5_SL, inventory);
                        else if (SL25 != null && verifyDeveloperPayload(SL25))
                            consumed(SKU_INFINITE_25_SL, inventory);
                        else if (SL60 != null && verifyDeveloperPayload(SL60))
                            consumed(SKU_INFINITE_60_SL, inventory);
                    }

                    intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "super_like");
                    startActivity(intent);
                } else {*/

                   if (sessionManager.getIsSawSuperLike()) {
                       String name = getUserName((int) cardStack.getTopCardItemId());
                       int i=0x1F609;
                       msg = String.format("Swipe up to Favourite"+getEmojiByUnicode(i));
                       title = String.format(res.getString(R.string.alert_super_like_title), name);
                       btnText = res.getString(R.string.super_like_btn);
                       showDialog(0, "", "", "click", title, msg, btnText, 2);
                       sessionManager.setIsSawSuperLike(false);
                       sawSuperLike = false;
                   } else {
                       cardStack.swipeTopCardTop(SwipeDeck.ANIMATION_DURATION);
                       sawSuperLike = false;
                   }


                break;
            case R.id.rlt_unlike_lay:
                if (sessionManager.getIsSawUnLike()) {
                    String name = getUserName((int) cardStack.getTopCardItemId());
                    msg = String.format(res.getString(R.string.alert_not_interest_msg), name);
                    title = res.getString(R.string.alert_not_interest_title);
                    msg = String.format("Swipe left to Pass"+getEmojiByUnicode(0x2639), name);
                    btnText = res.getString(R.string.not_interest);
                    showDialog(0, "", "", "click", title, msg, btnText, 0);
                    sessionManager.setIsSawUnLike(false);
                    sawUnLike = false;
                } else {
                    sawUnLike = false;
                    cardStack.swipeTopCardLeft(SwipeDeck.ANIMATION_DURATION);
                }
                break;

            case R.id.rlt_like_lay:
                //cardStack.swipeReported(R.id.reported_tv);
                //if (!"yes".equalsIgnoreCase(sessionManager.getIsRemainingLikeLimited())) {
                    likeSwipeCall();
               // } else if (sessionManager.getRemainingLikes() > 0) {
                //    sessionManager.setRemainingLikes(sessionManager.getRemainingLikes() - 1);
                  //  likeSwipeCall();
                /*} else {
                    Log.e(TAG, "Original JSON " + mSubscribedToInfiniteSuperLike);
                    Log.e(TAG, "Original JSON " + String.valueOf(mSubscribedToInfiniteSuperLike));
                    // if (!mSubscribedToPlus) {
                    //rewind(false, 2);
                    intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "plus");
                    startActivity(intent);
                    return;
                    // }

                }*/
                /*if ("no".equalsIgnoreCase(sessionManager.getIsRemainingLikeLimited()) && sessionManager.getRemainingLikes() <= 0) {
                    intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "plus");
                    startActivity(intent);
                } else {

                    if (sessionManager.getIsSawLike()) {
                        String name = getUserName((int) cardStack.getTopCardItemId());
                        msg = String.format(res.getString(R.string.alert_like_msg), name);
                        title = res.getString(R.string.alert_like_title);
                        btnText = res.getString(R.string.like);
                        showDialog(0, "", "", "click", title, msg, btnText, 1);
                        sessionManager.setIsSawLike(false);
                        sawLike = false;
                    } else {
                        sawLike = false;
                        cardStack.swipeTopCardRight(SwipeDeck.ANIMATION_DURATION);
                    }
                }*/
                break;
            default:
                break;
        }
    }
    private void updateButtons(){

    }
    public void likeSwipeCall() {
        Intent intent = null;
        String msg = "", btnText = "", title = "";
        if (sessionManager.getIsSawLike()) {
            String name = getUserName((int) cardStack.getTopCardItemId());
            msg = msg = String.format("Swipe right to Keep!");
            title =res.getString(R.string.alert_like_title);
            btnText = res.getString(R.string.like);

            showDialog(0, "", "", "click", title, msg, btnText, 1);
            sessionManager.setIsSawLike(false);
            sawLike = false;
        } else {
            sawLike = false;
            cardStack.swipeTopCardRight(SwipeDeck.ANIMATION_DURATION);
        }
    }

    /**
     * Super like function to call In APP purchase and other functions
     */
    private void superLike(int userId, long itemId, String json) {
        System.out.println("Remaining Super Likes " + sessionManager.getRemainingSuperLikes());
        if (sessionManager.getRemainingSuperLikes() == 0 && mSubscribedToInfiniteSuperLike) {
            mSubscribedToInfiniteSuperLike = false;
            //complain("Super Like already applied...");
            if (SL5 != null && verifyDeveloperPayload(SL5))
                consumed(SKU_INFINITE_5_SL, inventory);
            else if (SL25 != null && verifyDeveloperPayload(SL25))
                consumed(SKU_INFINITE_25_SL, inventory);
            else if (SL60 != null && verifyDeveloperPayload(SL60))
                consumed(SKU_INFINITE_60_SL, inventory);
        }

        if (sessionManager.getRemainingSuperLikes() > 0) {
            sessionManager.setRemainingSuperLikes(sessionManager.getRemainingSuperLikes() - 1);
            if (itemId == -100)
                swipeProfile(userId, MATCH_SUPER_LIKE, json);
            else
                swipeProfile(userId, MATCH_SUPER_LIKE, getJson((int) itemId));
        } else {
            /*
            Log.e(TAG, "Original JSON " + mSubscribedToInfiniteSuperLike);
            Log.e(TAG, "Original JSON " + String.valueOf(mSubscribedToInfiniteSuperLike));
            //if (!mSubscribedToInfiniteSuperLike) {
            rewind(false, 2);
            Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
            intent.putExtra("startwith", "");
            intent.putExtra("type", "super_like");
            startActivity(intent);
            return;
            */
            //}
        }
    }

    /**
     * Super like function to call In APP purchase and other functions
     */
    private void like(int userId, long itemId, String json) {
        System.out.println("Remaining Likes " + sessionManager.getRemainingLikes());
        System.out.println("Remaining Likes is " + sessionManager.getIsRemainingLikeLimited());
/*
        if (!"yes".equalsIgnoreCase(sessionManager.getIsRemainingLikeLimited())) {
            System.out.println("Remaining getIsRemainingLikeLimited " + sessionManager.getIsRemainingLikeLimited());
            if (itemId == -100)
                swipeProfile(userId, MATCH_LIKE, json);
            else
                swipeProfile(userId, MATCH_LIKE, getJson((int) itemId));
        } else if (sessionManager.getRemainingLikes() > 0) {
            sessionManager.setRemainingLikes(sessionManager.getRemainingLikes() - 1);
            System.out.println("Remaining Likes check" + sessionManager.getRemainingLikes());
            if (itemId == -100)
                swipeProfile(userId, MATCH_LIKE, json);
            else
                swipeProfile(userId, MATCH_LIKE, getJson((int) itemId));
        } else {
            Log.e(TAG, "Original JSON " + mSubscribedToPlus);
            Log.e(TAG, "Original JSON " + String.valueOf(mSubscribedToPlus));
            //if (!mSubscribedToPlus) {
            rewind(false, 2);
            Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
            intent.putExtra("startwith", "");
            intent.putExtra("type", "plus");
            startActivity(intent);
            return;
            //}
        }*/
        if (itemId == -100)
            swipeProfile(userId, MATCH_LIKE, json);
        else
            swipeProfile(userId, MATCH_LIKE, getJson((int) itemId));
    }

    /**
     * Show dialog while first swipe or click the bottom images
     */
    private void showDialog(final Integer userId, final String matchType, final String json, final String type, String title, String msg, String buttonText, final int index) {
        customDialog = new CustomDialog(index, title, msg, buttonText, res.getString(R.string.cancel), new CustomDialog.btnAllowClick() {
            @Override
            public void clicked() {
                switch (index) {
                    case 0:
                        if (!type.equals("swipe"))
                            cardStack.swipeTopCardLeft(SwipeDeck.ANIMATION_DURATION);
                        //cardStack.unSwipeCard();
                        break;
                    case 1:
                        if (!type.equals("swipe"))
                            cardStack.swipeTopCardRight(SwipeDeck.ANIMATION_DURATION);
                        //cardStack.unSwipeCard();
                        break;
                    case 2:
                        if (!type.equals("swipe"))
                            cardStack.swipeTopCardTop(SwipeDeck.ANIMATION_DURATION);
                        //cardStack.unSwipeCard();
                        break;
                    default:
                        break;
                }

                if (type.equals("swipe")) {
                    if (matchType.equals(MATCH_SUPER_LIKE)) {
                        superLike(userId, -100, json);
                    } else if (matchType.equals(MATCH_LIKE)) {
                        like(userId, -100, json);
                    } else {
                        swipeProfile(userId, matchType, json);
                    }
                }
            }
        }, new CustomDialog.btnDenyClick() {
            @Override
            public void clicked() {
                switch (index) {
                    case 0:
                        if (type.equals("swipe")) {
                            cardStack.unSwipeCard();
                            swipeProfile(-1, MATCH_SUPER_LIKE, json);
                        }
                        break;
                    case 1:
                        if (type.equals("swipe")) {
                            cardStack.unSwipeCard();
                            swipeProfile(-1, MATCH_SUPER_LIKE, json);
                        }
                        break;
                    case 2:
                        if (type.equals("swipe")) {
                            cardStack.unSwipeCard();
                            swipeProfile(-1, MATCH_SUPER_LIKE, json);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        customDialog.show(mActivity.getSupportFragmentManager(), "");
    }

    /**
     * Call swipe profile API
     */
    private void swipeProfile(Integer userId, String matchType, String json) {
        if(userId > 0) {
            positions = positions + 1;
        }else{
////            matchesSwipeAdapter.left.remove(matchesSwipeAdapter.left.size() -1 );
////            matchesSwipeAdapter.right.remove(matchesSwipeAdapter.right.size() -1 );
//            matchesSwipeAdapter.setVisibilty(positions - 1);
        }

        if (positions < matchingProfilesList.size()) {
            matchesSwipeAdapter.setVisibilty(positions);
            matchesSwipeAdapter.indexOfInfo = positions;
        }
        matchesSwipeAdapter.isFirst=true;
        if (MATCH_SUPER_LIKE == matchType) {

        }

        if(userId > 0){
            originMatchingProfilesList.remove(0);
            swipeMethod = matchType;
            totalNumberOfCards=totalNumberOfCards-1;
            apiService.swipeProfile(sessionManager.getToken(), userId, matchType, subscriptionId).enqueue(new RequestCallback(REQ_SWIPE_MATCH, this));
        }
    }

    /**
     * Call boost user API
     */
    private void boostUser() {
        apiService.boostUser(sessionManager.getToken()).enqueue(new RequestCallback(REQ_UPDATE_BOOST_USER, this));
    }

    /**
     * Get result for other activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) return;
        if (requestCode == 100 && data != null) {
            boolean isKeepSwipe = data.getBooleanExtra("isKeepSwipe", true);
            //if (!isKeepSwipe) mActivity.getViewPager().setCurrentItem(2);
            if (!isKeepSwipe) {
                Intent intent = new Intent(mActivity, ChatConversationActivity.class);
                intent.putExtra("matchId", data.getIntExtra("matchId", 0));
                intent.putExtra("userId", data.getIntExtra("userId", 0));
                startActivity(intent);
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 300) {
            checkAllPermission(Constants.PERMISSIONS_LOCATION);
        } else if (requestCode == 101) {
            checkGpsEnable();
        }
    }

    /**
     * Call API for get other user profiles
     */
    private void showMatchProfile() {
        if (StaticData.isFromFavourite==false) {
            if (!rippleBackground.isRippleAnimationRunning()) {
                rippleBackground.setVisibility(View.VISIBLE);

                rippleBackground.startRippleAnimation();
                tvMatch.setPaintFlags(tvMatch.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ((HomeActivity) getActivity()).changeDirection("all");

            }

//            latitude = 39.3;
//            longitude = -110.5;
            if (latitude != 0 && longitude != 0) {
                tvMatch.setPaintFlags(tvMatch.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                apiService.showMatchingProfile(sessionManager.getToken(), latitude, longitude).enqueue(new RequestCallback(REQ_SHOW_ALL_MATCHES, this));
            }
        }
    }

    /**1
     * After API call get success response
     */
    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }

        switch (jsonResp.getRequestCode()) {
            case REQ_GET_HOME:
                if (jsonResp.isSuccess()) {
                    onSuccessGetMyHome(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_SWIPE_MATCH:

                if (jsonResp.isSuccess()) {
                    onSuccessSwipeProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    if (swipeMethod.equalsIgnoreCase(MATCH_LIKE)) {
                        totalLikeCount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_LIKE, Integer.class);
                        sessionManager.setRemainingLikes(totalLikeCount);
                        cardStack.unSwipeCard();

                        Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                        intent.putExtra("startwith", "");
                        intent.putExtra("type", "plus");
                        startActivity(intent);
                    } else {
                        commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                    }
                }
                break;
            case REQ_UPDATE_BOOST_USER:
                if (jsonResp.isSuccess()) {
                    onSuccessBoostUser(jsonResp);
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    if (jsonResp.getStatusMsg().equals("No Boost Count")) {
                        /*
                        if (oneBoost != null && verifyDeveloperPayload(oneBoost))
                            consumed(SKU_INFINITE_ONE_BOOST, inventory);
                        else if (fiveBoost != null && verifyDeveloperPayload(fiveBoost))
                            consumed(SKU_INFINITE_FIVE_BOOST, inventory);
                        else if (tenBoost != null && verifyDeveloperPayload(tenBoost))
                            consumed(SKU_INFINITE_TEN_BOOST, inventory);

                        Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                        intent.putExtra("startwith", "");
                        intent.putExtra("type", "boost");
                        startActivity(intent);*/
                    } else {
                        commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                    }

                }
                break;
            case REQ_UPDATE_LOCATION:
                showMatchProfile();
                if (jsonResp.isSuccess()) {
                    onSuccessUpdateLocation(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_SHOW_ALL_MATCHES:
                if (jsonResp.isSuccess()) {
                    matchProfilesModel = gson.fromJson(jsonResp.getStrResponse(), MatchProfilesModel.class);
                    if (!TextUtils.isEmpty(matchProfilesModel.getIsOrder()) && matchProfilesModel.getIsOrder().equalsIgnoreCase("Yes")) {
                        sessionManager.setIsOrder(true);
                        sessionManager.setPlanType(matchProfilesModel.getPlanType());
                    } else {
                        sessionManager.setIsOrder(false);
                        sessionManager.setPlanType(matchProfilesModel.getPlanType());
                    }

                    totalSuperLikeCount = matchProfilesModel.getTotalSuperLikes();
                    remainingSuperLikeCount = matchProfilesModel.getRemainingSuperLikes();
                    remainingLikeCount = matchProfilesModel.getRemainingLike();
                    isLikeLimited = matchProfilesModel.getIsLikeLimited();
                    remainingSuperLikeHours = matchProfilesModel.getRemainingSuperLikeHours();

                    sessionManager.setRemainingSuperLikes(remainingSuperLikeCount);
                    sessionManager.setRemainingLikes(remainingLikeCount);
                    sessionManager.setIsRemainingLikeLimited(isLikeLimited);

                    totalBoostCount = matchProfilesModel.getTotalBoost();
                    remainingBoostCount = matchProfilesModel.getRemainingBoost();
                    remainingBoostHours = matchProfilesModel.getRemainingBoostHours();
                    remainingBoostDay = matchProfilesModel.getRemainingBoostDay();
                    sessionManager.setRemainingBoost(remainingBoostCount);

                    onSuccessShowAllMatches(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                    noPeople();
                    rewind(false, 1);
                }
                break;
            default:
                break;
        }
    }

    /**
     * After API call get user swipe profile details
     */
    private void onSuccessShowAllMatches(JsonResponse jsonResp) {
        Log.e("Check ", String.valueOf(matchingProfilesList.size()));
        MatchesResponse matchesResponse = gson.fromJson(jsonResp.getStrResponse(), MatchesResponse.class);
        StaticData.settingsModel.setCity(matchesResponse.getCity());
        if (matchesResponse != null && matchesResponse.getMatchingProfile() != null && !matchesResponse.getMatchingProfile().isEmpty()) {
            matchingProfilesList.clear();
            originMatchingProfilesList.clear();
            cardStack.clear();
            totalNumberOfCards=0;
            Log.e("Check1 ", String.valueOf(matchingProfilesList.size()));
            MatchesResponse matchesResponse1=removeFavouriteCards(matchesResponse);
            matchingProfilesList.addAll(matchesResponse1.getMatchingProfile());
            originMatchingProfilesList.addAll(matchesResponse1.getMatchingProfile());
            totalNumberOfCards=matchingProfilesList.size();
            if (matchingProfilesList.size() > 0) {
                if (StaticData.settingsModel.getShowMe().equals("yes")) {
                    showCards();
                    ((HomeActivity) getActivity()).setViewPagerFront(true);
                }else{
                    showDiscoveryScreen();
                }
            } else {

                noPeople();
                rewind(false, 1);
            }
        } else {

            noPeople();
            rewind(false, 1);
        }
    }
    private MatchesResponse removeFavouriteCards(MatchesResponse matchesResponse){
        for (NewMatchProfileModel newMatchProfileModel:StaticData.matchedProfileModel.getNewMatchProfile()
             ) {
            for (int i=0;i<matchesResponse.getMatchingProfile().size();i++){
                if (newMatchProfileModel.getUserId()==matchesResponse.getMatchingProfile().get(i).getUserId()){
                    matchesResponse.getMatchingProfile().remove(i);
                }
            }
        }
        return matchesResponse;
    }
    private void showCards(){
        StaticData.isCardsAvailable=true;
        ((HomeActivity) getActivity()).changeDirection("none");

        Log.e("Check2 ", String.valueOf(matchingProfilesList.size()));
        rippleBackground.stopRippleAnimation();
        rippleBackground.setVisibility(View.GONE);
        cardStack.setVisibility(View.VISIBLE);
        rltSuperLike.setEnabled(true);
        rltUnLike.setEnabled(true);
        rltLike.setEnabled(true);
        rltBoost.setEnabled(true);
        if (!sessionManager.getIsOrder())
            rltReload.setEnabled(true);
        else
            rltReload.setEnabled(false);

        ivSuperLike.setEnabled(true);

        ivUnLike.setEnabled(true);
        ivLike.setEnabled(true);
        ivBoost.setEnabled(true);
        if (!sessionManager.getIsOrder())
            ivReload.setEnabled(true);
        else
            ivReload.setEnabled(false);
        textLayout.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        updateMatchingProfiles();
    }
    /**
     * After API call get failure response
     */
    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        //if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
        noInternet();
    }

    /**
     * After API call get swipe profile response
     */
    private void onSuccessSwipeProfile(JsonResponse jsonResp) {
        if (jsonResp.isSuccess()) {
            if (StaticData.isFavourite){
                StaticData.CHAT=1;
            }
            StaticData.isFavourite=false;
            String json = jsonResp.getStrResponse();

            remainingSuperLikeCount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_SUPER_LIKE, Integer.class);
            remainingLikeCount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_LIKE, Integer.class);
            isLikeLimited = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ISLIKELIMITED, String.class);
            if (swipeMethod.equalsIgnoreCase(MATCH_REWIND)) {
                sessionManager.setRemainingSuperLikes(remainingSuperLikeCount);
                sessionManager.setRemainingLikes(remainingLikeCount);
                sessionManager.setIsRemainingLikeLimited(isLikeLimited);
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonResp.getStrResponse());
                if (jsonObject.has(Constants.SUBSCRIPTION_ID)) {
                    String subscriptionIds = commonMethods.getJsonValueString(jsonResp.getStrResponse(), Constants.SUBSCRIPTION_ID, String.class);
                    if (!TextUtils.isEmpty(subscriptionIds))
                        subscriptionId = Integer.parseInt(subscriptionIds);
                    else
                        subscriptionId = 0;
                } else {
                    subscriptionId = 0;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (swipeMethod.equalsIgnoreCase(MATCH_REWIND)) {
                reloadSwipe();
            }

            String matchStatus = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.MATCH_STATUS, String.class);
            System.out.println("JSON " + json);
            System.out.println("JSONs " + jsonResp.getRequestData());
            System.out.println("Match Status " + matchStatus);

            if (!TextUtils.isEmpty(json) && !TextUtils.isEmpty(matchStatus) && matchStatus.equalsIgnoreCase("Yes")) {
                StaticData.CHAT=1;
                ((HomeActivity) getActivity()).changeChatIcon(1);
                Intent intent = new Intent(mActivity, MatchUsersActivity.class);
                intent.putExtra("json", json);
                startActivityForResult(intent, 100);
            }
        }
    }

    /**
     * After API call get boost response
     */
    private void onSuccessBoostUser(JsonResponse jsonResp) {
        if (jsonResp.isSuccess()) {
            String json = jsonResp.getRequestData();

            remainingBoostCount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_BOOST, Integer.class);
            if (remainingBoostCount == 0)
                mSubscribedToInfiniteBoost = true;

            sessionManager.setRemainingBoost(remainingBoostCount);
            Log.e(TAG, "After Boost Applied " + String.valueOf(sessionManager.getRemainingBoost()));

        }
    }

    /**
     * After swipe profile reset bottom icons
     */
    public void reloadSwipe() {
        if (cardStack != null && cardStack.getTopCardItemId() >= 0) {
            ((HomeActivity) getActivity()).changeDirection("none");
        } else {
            ((HomeActivity) getActivity()).changeDirection("none");
        }
        rippleBackground.setVisibility(View.GONE);
        cardStack.setVisibility(View.VISIBLE);

        rltSuperLike.setEnabled(true);
        rltUnLike.setEnabled(true);
        rltLike.setEnabled(true);
        rltBoost.setEnabled(true);
        // rltReload.setEnabled(true);

        if (StaticData.editProfileModel.getRemainingLikes() > 0){
            ivSuperLike.setEnabled(true);
        }else{
            ivSuperLike.setEnabled(false);
        }
        ivUnLike.setEnabled(true);
        ivLike.setEnabled(true);
        ivBoost.setEnabled(true);
    }

    /**
     * After API call get location update status
     */
    private void onSuccessUpdateLocation(JsonResponse jsonResp) {
//        IgniterPageModel igniterPageModel = gson.fromJson(jsonResp.getStrResponse(), IgniterPageModel.class);
//        if (igniterPageModel != null && igniterPageModel.getUserDetails() != null) {
//            userDetailModel = igniterPageModel.getUserDetails();
//            updateView();
//        }
    }

    /**
     * After API call get user details
     */
    private void onSuccessGetMyHome(JsonResponse jsonResp) {
       /* IgniterPageModel igniterPageModel = gson.fromJson(jsonResp.getStrResponse(), IgniterPageModel.class);
        if (igniterPageModel != null && igniterPageModel.getUserDetails() != null) {
            userDetailModel = igniterPageModel.getUserDetails();
            updateView();
        }*/
    }
    public void noInternet() {
        //rippleBackground.stopRippleAnimation();
        ((HomeActivity) getActivity()).setViewPagerFront(false);
        StaticData.isCardsAvailable=false;
        rippleBackground.startRippleAnimation();
        textLayout.setVisibility(View.GONE);
        searchTextView.setText("No internet connection");
        searchTextView.setVisibility(View.VISIBLE);
        tvMatch.setPaintFlags(tvMatch.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        ((HomeActivity) getActivity()).changeDirection("all");

        rippleBackground.setVisibility(View.VISIBLE);
        cardStack.setVisibility(View.GONE);

        rltSuperLike.setEnabled(false);
        rltUnLike.setEnabled(false);
        rltLike.setEnabled(false);
        //rltBoost.setEnabled(false);
        //rltReload.setEnabled(false);

        ivSuperLike.setEnabled(false);
        ivUnLike.setEnabled(false);
        ivLike.setEnabled(false);
        ((HomeActivity)getActivity()).tabLayout.bringToFront();

        //ivBoost.setEnabled(false);
        //ivReload.setEnabled(false);

        if (cardStack != null && cardStack.getTopCardItemId() < 0) {
            // showMatchProfile();
        }
    }
    /**
     * Set disable and hide view if no profile available
     */
    public void noPeople() {
        //rippleBackground.stopRippleAnimation();
        ((HomeActivity) getActivity()).setViewPagerFront(false);
        StaticData.isCardsAvailable=false;
        rippleBackground.startRippleAnimation();
        textLayout.setVisibility(View.VISIBLE);
        searchTextView.setText("Searching....");
        searchTextView.setVisibility(View.VISIBLE);
        tvMatch.setPaintFlags(tvMatch.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        ((HomeActivity) getActivity()).changeDirection("all");

        rippleBackground.setVisibility(View.VISIBLE);
        cardStack.setVisibility(View.GONE);

        rltSuperLike.setEnabled(false);
        rltUnLike.setEnabled(false);
        rltLike.setEnabled(false);
        //rltBoost.setEnabled(false);
        //rltReload.setEnabled(false);

        ivSuperLike.setEnabled(false);
        ivUnLike.setEnabled(false);
        ivLike.setEnabled(false);
        ((HomeActivity)getActivity()).tabLayout.bringToFront();

        //ivBoost.setEnabled(false);
        //ivReload.setEnabled(false);

        if (cardStack != null && cardStack.getTopCardItemId() < 0) {
            // showMatchProfile();
        }
    }

    /**
     * After API call to update view
     */
    private void updateView() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(userDetailModel.getFirstName())) {
            sb.append(userDetailModel.getFirstName());
            sessionManager.setUserName(userDetailModel.getFirstName());
        }

        if (!TextUtils.isEmpty(userDetailModel.getLastName())) {
            sb.append(" ");
            sb.append(userDetailModel.getLastName());
        }

        sb.append(", ");
        sb.append(userDetailModel.getAge());

    }
    private void checkAllPermissionFirstTime(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(mActivity, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {

                showLocationPermissionDailog(Constants.PERMISSIONS_LOCATION);

        } else {
            checkGpsEnableFirstTime();
        }
    }
    /**
     * Check user allow all permission and ask permission to allow
     */
    private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(mActivity, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            isGps=false;
           showLocationEnableScreen();
        } else {
            checkGpsEnable();
        }
    }
    private void showLocationEnableScreen(){
        enableLocationLayout.setVisibility(View.VISIBLE);
    }
    private void dismissLocationEnableScreen(){
        enableLocationLayout.setVisibility(View.GONE);
    }
    private void showLocationPermissionDailog(String[] permission){
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(mActivity, permission);
        boolean isBlocked = runTimePermission.isPermissionBlocked(mActivity, blockedPermission.toArray(new String[blockedPermission.size()]));
        if (isBlocked) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    //callPermissionSettings();
                    showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                }
            });
        } else {
            ActivityCompat.requestPermissions(mActivity, permission, 150);
        }
    }

    /**
     * Get permission result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        } else {
            checkGpsEnable();
        }
    }

    /**
     * If any one or more permission is deny or block show the enable permission dialog
     */
    private void showEnablePermissionDailog(final int type, String message) {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog(message, getString(R.string.ok), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    if (type == 0)
                        callPermissionSettings();
                    else
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
                }
            });
            customDialog.show(mActivity.getSupportFragmentManager(), "");
        }
    }

    /**
     * Open permission dialog
     */
    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }
    private void enableGpsDailog(){
        showEnablePermissionDailog(1, getString(R.string.please_enable_location));
    }
    private void checkGpsEnableFirstTime() {
        boolean isGpsEnabled = MyLocation.defaultHandler().isLocationAvailable(mActivity);
        if (!isGpsEnabled) {
            enableGpsDailog();

        } else {
            dismissLocationEnableScreen();
            isPermissionGranted = true;
            System.out.println("Check1");
            MyLocation.defaultHandler().getLocation(mActivity, locationResult);
        }
    }
    /**
     * Check GPS enable or not
     */
    private void checkGpsEnable() {
        boolean isGpsEnabled = MyLocation.defaultHandler().isLocationAvailable(mActivity);
        if (!isGpsEnabled) {
            isGps=true;
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
            showLocationEnableScreen();
        } else {
            dismissLocationEnableScreen();
            isPermissionGranted = true;
            System.out.println("Check1");
            MyLocation.defaultHandler().getLocation(mActivity, locationResult);
        }
    }

    /**
     * show other user profile in swipedeck
     */
    private void updateMatchingProfiles() {
        positions=0;
        cardStack.setVisibility(View.VISIBLE);
         ArrayList<MatchingProfile> match=matchingProfilesList;
         int count=matchingProfilesList.size();
        matchesSwipeAdapter = new MatchesSwipeAdapter(matchingProfilesList, getContext());
        if (cardStack != null) {
            Log.e("Check ", String.valueOf(matchingProfilesList.size()));
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    cardStack.setAdapter(matchesSwipeAdapter);
                    matchesSwipeAdapter.notifyDataSetChanged();
                    StaticData.isFromFavourite=false;
                }
            });

            //cardStack.unSwipeCard();
        }
    }



    /**
     * Function call the view is visible to user that time we reload the other user is swipe deck
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (textLayout != null) {
                textLayout.setVisibility(View.GONE);
                searchTextView.setVisibility(View.GONE);
                if (cardStack != null && cardStack.getTopCardItemId() >= 0){
                    bringViewPagerToFront();

                }else{
                   bringViewPagerToFront();
                }
            }
        }
        if (cardStack != null)
            //if (isVisibleToUser && view != null&&cardStack!=null&&cardStack.getTopCardItemId()<0&&sessionManager.getSettingUpdate()) {
            if (isVisibleToUser && view != null && cardStack != null && cardStack.getTopCardItemId() < 0) {
                if (sessionManager.getSettingUpdate()) {
                    showDiscoveryScreen();
                    noPeople();
                    sessionManager.setSettingUpdate(false);
                    showMatchProfile();
                } else {
                    showDiscoveryScreen();
                    sessionManager.setSettingUpdate(false);
                    showMatchProfile();
                }
            } else {
                if (sessionManager.getSettingUpdate()) {
                    showDiscoveryScreen();
                    cardStack.clear();
                    sessionManager.setSettingUpdate(false);
                    showMatchProfile();
                }
            }


        if (cardStack != null && cardStack.getTopCardItemId() >= 0) {
            ((HomeActivity) getActivity()).changeDirection("none");
        } else {
//           ((HomeActivity)getActivity()).changeDirection("all");
        }

        if (isVisibleToUser && view != null) {
            hideKeyboard(getContext());
        }
    }
    private void bringViewPagerToFront(){
        if (StaticData.settingsModel.getShowMe().equals("yes")){
            ((HomeActivity)getActivity()).setViewPagerFront(true);
        }else{
            ((HomeActivity)getActivity()).setViewPagerFront(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (StaticData.REPORTED&&StaticData.FROMPROFILE){
            cardStack.swipeReported(R.id.reported_tv);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cardStack.swipeReportedCard(SwipeDeck.ANIMATION_DURATION);
                }
            }, 3000);

        }
        hideKeyboard(getContext());

        if (cardStack != null && cardStack.getTopCardItemId() < 0 && !isPermissionGranted)
            if (StaticData.isFirstRequest) {
                checkAllPermissionFirstTime(Constants.PERMISSIONS_LOCATION);
            }else {
                checkAllPermission(Constants.PERMISSIONS_LOCATION);
            }

        if (sessionManager.getSwipeType() != null && cardStack != null) {
            if (sessionManager.getSwipeType().equals("UnLike"))
                cardStack.swipeTopCardLeft(SwipeDeck.ANIMATION_DURATION);
            else if (sessionManager.getSwipeType().equals("Like"))
                cardStack.swipeTopCardRight(SwipeDeck.ANIMATION_DURATION);
            else if (sessionManager.getSwipeType().equals("SuperLike"))
                cardStack.swipeTopCardTop(SwipeDeck.ANIMATION_DURATION);

            sessionManager.setSwipeType("");
        }

        if (sessionManager.getIsOrder()) {
            rewind(rewind, 1);
        }

        if (sessionManager.getRemainingBoost() == 0)
            mSubscribedToInfiniteBoost = true;
    }

    /**
     * Setup In App Purchase
     */
    void setupHelper() {
        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = getResources().getString(R.string.google_play_publish_key);

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }


        // Create the helper, passing it our context and the public key to verify signatures with
        Log.e(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.e(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.e(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(IgniterPageFragment.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                mActivity.registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.e(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    /**
     * Receive data while purchase or call In App products
     */
    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d("Boost Dialog", "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        payload = p.getDeveloperPayload();
        /*Log.e(TAG, "Original JSON " +  p.getDeveloperPayload());
        Log.e(TAG, "Original JSON " +  p.getOriginalJson());
        Log.e(TAG, "Original JSON " +  p.getItemType());
        Log.e(TAG, "Original JSON " +  p.getPurchaseState());
        Log.e(TAG, "Original JSON " +  p.getPurchaseTime());
        Log.e(TAG, "Original JSON " +  p.getToken());
        Log.e(TAG, "Original JSON " +  p.getSku());
        Log.e(TAG, "Original JSON " +  p.getSignature());*/

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    /**
     * Show dialog while In App Purchase messages
     */
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        //alert("Error: " + message);
        alert("" + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.e(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }

        // very important:
        if (mBroadcastReceiver != null) {
            mActivity.unregisterReceiver(mBroadcastReceiver);
        }
    }

    /**
     * After user used the plan call consumed function
     */
    void consumed(String plan, Inventory inventory) {
        // Check for Boost delivery -- if we own Boost, we should fill up the tank immediately
        Purchase BoostPurchase = inventory.getPurchase(plan);
        if (BoostPurchase != null && verifyDeveloperPayload(BoostPurchase)) {
            Log.e(TAG, "We have Boost. Consuming it.");
            try {
                mHelper.consumeAsync(BoostPurchase, mConsumeFinishedListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error consuming Boost. Another async operation in progress.");
            }
            return;
        }
        Log.e(TAG, "Initial inventory query finished; enabling main UI.");

    }
}