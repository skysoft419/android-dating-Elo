package ello.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category MatchUsersActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.GiphyCoreUI;
import com.giphy.sdk.ui.themes.GridType;
import com.giphy.sdk.ui.themes.LightTheme;
import com.giphy.sdk.ui.views.GPHMediaTypeView;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.adapters.chat.ChatConversationListAdapter;
import ello.adapters.chat.UnmatchReasonListAdapter;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.SessionManager;
import ello.datamodels.chat.ChatMessageModel;
import ello.datamodels.chat.MessageModel;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.chat.ReasonModel;
import ello.datamodels.chat.ReceiveDateModel;
import ello.datamodels.chat.UnMatchReasonModel;
import ello.datamodels.main.JsonResponse;
import ello.dialog.OnGradientClick;
import ello.dialog.ViewDialog;
import ello.interfaces.ApiService;
import ello.interfaces.DropDownClickListener;
import ello.interfaces.ServiceListener;
import ello.pushnotification.Config;
import ello.pushnotification.NotificationUtils;
import ello.utils.CommonMethods;
import ello.utils.DateTimeUtility;
import ello.utils.Enums;
import ello.utils.ImageUtils;
import ello.utils.RequestCallback;
import ello.views.action.UnmatchUserActivity;
import ello.views.customize.CustomDialog;
import ello.views.customize.CustomLayoutManager;
import ello.views.customize.CustomRecyclerView;
import ello.views.profile.EnlargeProfileActivity;

import static ello.utils.Enums.REQ_GET_PLAN;

/*****************************************************************
 User Chat conversation page
 ****************************************************************/
public class ChatConversationActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener, OnGradientClick {

    public static boolean isConversationActivity = false;
    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson, gson1;
    @Inject
    ImageUtils imageUtils;
    @Inject
    DateTimeUtility dateTimeUtility;
    /**
     * Dropdown click listener
     */
    DropDownClickListener listener = new DropDownClickListener() {
        @Override
        public void onDropDrownClick(String value) {
            switch (value) {
                case Constants.MUTE_NOTIFICATION:
                    break;
                case Constants.REPORT:
                    break;
                case Constants.UNMATCH:
                    getUnmatchDetails();
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver;
    private CustomTextView tvHeader, tvLeftArrow, tvName;
    private ImageView tvHeaderMenuIcon;
    private ImageView civHeaderImgOne,mvGifIcon,mvColorIcon, civHeaderImgTwo, civHeaderImgSingle, civEmptyChatImage;
    private RelativeLayout rltHeaderImg, rltEmptyChatConversation;
    private View rltEmptyChat;
    private CustomEditText edtChatMsg;
    private RecyclerView rvChatConversationList;
    private Dialog unMatchDialog;
    private ImageView ivSendMsg;
    private ChatConversationListAdapter chatConversationListAdapter;
    private UnmatchReasonListAdapter unmatchReasonListAdapter;
    private CustomLayoutManager linearLayoutManager;
    private AlertDialog dialog;
    private int matchId, userId, userIds;
    private ChatMessageModel chatMessageModel;
    private NewMatchProfileModel newMatchProfileModel;
    private UnMatchReasonModel unMatchReasonModel;
    private ArrayList<MessageModel> messageModels = new ArrayList<>();
    private ArrayList<ReasonModel> reasonModels = new ArrayList<>();
    private String otherUserName = "";
    private static String API_KEY_GIPHY="br7Cz8zHsl8lMOl9kfkuxukn1U0K0VCt";
    private GiphyDialogFragment dialogFragment;
    private final String[] topColors = {"#434343", "#3f5efb", "#7579ff", "#009fff", "#bdc3c7", "#43cea2", "#00c6fb", "#92fe9d", "#ef8e38", "#FC00FF", "#dd2476", "#ffc0cb", "#f953c6", "#8f94fb", "#0abfbc", "#f2f501"};
    private final String[] bottomColors = {"#000000", "#fc466b", "#b224ef", "#ec2f4b", "#2c3e50", "#185a9d", "#005bea", "#00c9ff", "#108dc7", "#00DBDE", "#ff512f", "#800080", "#b91d73", "#4e54c8", "#fc354c", "#de0000"};
    private int colorGlobalSelection;
    private int overallXScroll = 0;
    private int sendButtonSelectedColor;
    private boolean keyboardShown=false;
    private boolean isFisrtScroll=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_conversation_layout);

        AppController.getAppComponent().inject(this);
        GiphyCoreUI.INSTANCE.configure(this,API_KEY_GIPHY);
        GPHSettings settings=new GPHSettings();
        settings.setTheme(LightTheme.INSTANCE);
        settings.setRenditionType(RenditionType.fixedWidth);
        settings.setGridType(GridType.waterfall);
        GPHContentType array[]=new GPHContentType[1];
        array[0]=GPHContentType.gif;
        settings.setMediaTypeConfig(array);
        dialogFragment=GiphyDialogFragment.Companion.newInstance(settings);
        initView();
        msgEditorListener();
        getIntentValues();
    }

    /**
     * Declare variable for layout views
     */
    private void sendMessage(String gifUrl,String isEmoji,String gifId,String gifAspectRatio){
        MessageModel messageModel=new MessageModel();
        messageModel.setGifId(gifId);
        messageModel.setSenderId(userId);
        messageModel.setIsEmoji(isEmoji);
        messageModel.setGifUrl(gifUrl);
        messageModel.setGifRatio(gifAspectRatio);
        messageModels.add(messageModel);
        rltEmptyChat.setVisibility(View.GONE);
        apiService.sendMessage(sessionManager.getToken(),Integer.toString(sessionManager.getUserId()), matchId, gifId,"gif",isEmoji,gifUrl,gifAspectRatio).enqueue(new RequestCallback(Enums.REQ_SEND_MSG, this));
    }
    private void initView() {
        final RelativeLayout rootView=findViewById(R.id.rootView);
        mvColorIcon=findViewById(R.id.mv_color);
        tvName = (CustomTextView) findViewById(R.id.tv_name);
        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvLeftArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);
        mvGifIcon =  findViewById(R.id.tv_gif_icon);
        mvColorIcon=findViewById(R.id.mv_color);
        ivSendMsg = (ImageView) findViewById(R.id.iv_send);
        rvChatConversationList =  findViewById(R.id.rv_chat_conversation_list);
        rltEmptyChat = (View) findViewById(R.id.rlt_empty_chat);
        edtChatMsg = (CustomEditText) findViewById(R.id.edt_new_msg);
        dialog = commonMethods.getAlertDialog(this);

        civHeaderImgOne = findViewById(R.id.civ_header_image_one);
        civHeaderImgTwo =  findViewById(R.id.civ_header_image_two);
        civHeaderImgSingle =  findViewById(R.id.civ_header_image_single);
        civEmptyChatImage =  findViewById(R.id.civ_empty_chat_image);

        tvHeaderMenuIcon =findViewById(R.id.tv_menu_icon);
        rltHeaderImg = (RelativeLayout) findViewById(R.id.rlt_header_image);

        setUpHeader();
        ivSendMsg.setEnabled(false);

        ivSendMsg.setOnClickListener(this);
        mvGifIcon.setOnClickListener(this);
        tvLeftArrow.setOnClickListener(this);
        tvHeaderMenuIcon.setOnClickListener(this);
        civEmptyChatImage.setOnClickListener(this);
        civHeaderImgOne.setOnClickListener(this);
        initRecyclerView();

        receivePushNotification();
        mvColorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDailog();
            }
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > dpToPx(ChatConversationActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                    //linearLayoutManager.smoothScrollToPosition(rvChatConversationList,null,chatConversationListAdapter.getItemCount());
                    if (keyboardShown == false) {
                        keyboardShown=true;
                        scrollToBottom();
                    }
                }else{
                    keyboardShown=false;
                }
            }
        });
    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    private void showDailog(){
        ViewDialog alert=new ViewDialog();
        alert.setCallback(this);
        alert.showDialog(ChatConversationActivity.this);
    }

    /**
     * Get Intent values from previous activities
     */
    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchId = bundle.getInt("matchId");
            userIds = bundle.getInt("userId");
            int index=bundle.getInt("gradientNo");
            changeGradient(index);
            getChatConversationList();
        }
    }

    /**
     * Setup the header layout
     */
    private void setUpHeader() {
        //tvHeader.setText(getString(R.string.chat_conversation_title));
        tvHeader.setText("");
        rltHeaderImg.setVisibility(View.VISIBLE);
        civHeaderImgOne.setVisibility(View.VISIBLE);

        tvHeaderMenuIcon.setVisibility(View.VISIBLE);
    }

    /**
     * Declare recyclerview
     */
    private void initRecyclerView() {
        linearLayoutManager = new CustomLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChatConversationList.setLayoutManager(linearLayoutManager);
        chatConversationListAdapter = new ChatConversationListAdapter(this);
        rvChatConversationList.setAdapter(chatConversationListAdapter);
        setChatListAdapter();

    }

    /**
     * Textwatcher for message
     */
    private void msgEditorListener() {

        edtChatMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivSendMsg.setEnabled(true);
                    changeButtonColor(true);
                }
                else {
                    ivSendMsg.setEnabled(false);
                    changeButtonColor(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void changeButtonColor(boolean enable){
        if (enable){
            ivSendMsg.setColorFilter(sendButtonSelectedColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        }else{
            ivSendMsg.setColorFilter(this.getResources().getColor(R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }
    /**
     * Get conversation details using webservice
     */
    private void getChatConversationList() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.messageConversation(sessionManager.getToken(), matchId).enqueue(new RequestCallback(Enums.REQ_MSG_CONVERSATION, this));
    }

    /**
     * Default on click method
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left_arrow:
                onBackPressed();
                break;
            case R.id.iv_send:

                String msg = edtChatMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg) && userId > 0) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setMessage(msg);
                    messageModel.setSenderId(userId);
                    messageModel.setLikeStatus("");
                    messageModel.setMessageId(10);
                    messageModel.setGifId("");
                    messageModel.setSenderImageUrl(chatMessageModel.getUserImgUrl());

                    messageModels.add(messageModel);
                    rltEmptyChat.setVisibility(View.GONE);
                    edtChatMsg.setText("");
                    ivSendMsg.setEnabled(false);
                    changeButtonColor(false);
                    //commonMethods.showProgressDialog(this, customDialog);
                    apiService.sendMessage(sessionManager.getToken(),Integer.toString(sessionManager.getUserId()),matchId,"", msg,"no","","0.0").enqueue(new RequestCallback(Enums.REQ_SEND_MSG, this));
                }
                break;
            case R.id.tv_gif_icon:
                showGifDialog();
                break;
            case R.id.tv_menu_icon:
                commonMethods.dropDownMenu(this, view, null, Constants.QUICK_MENU_TITLE, listener);
                break;
            case R.id.civ_empty_chat_image:
                Intent intent = new Intent(this, EnlargeProfileActivity.class);
                intent.putExtra("navType", 3);
                intent.putExtra("userId", userIds);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                break;
            case R.id.civ_header_image_one:
                Intent intent1 = new Intent(this, EnlargeProfileActivity.class);
                intent1.putExtra("navType", 3);
                intent1.putExtra("userId", userIds);
                startActivity(intent1);
                overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                break;

            default:
                break;
        }
    }
    private void showGifDialog(){
        dialogFragment.setGifSelectionListener(new GiphyDialogFragment.GifSelectionListener() {
            @Override
            public void onGifSelected(Media media) {
                String url=media.getImages().getFixedWidth().getGifUrl();
                String gifId=media.getId();
                String isEmoji="no";
                if (media.isSticker() && media.getImages().getFixedWidth().getWidth()==media.getImages().getFixedWidth().getHeight()){
                    isEmoji="yes";
                }
                Double aspectRatio=(((double)media.getImages().getFixedWidth().getHeight())/((double)media.getImages().getFixedWidth().getWidth()));
                sendMessage(url,isEmoji,gifId,Double.toString(aspectRatio));

            }

            @Override
            public void onDismissed() {

            }
        });
        dialogFragment.show(getSupportFragmentManager(),"gifs_dialog");
    }
    /**
     * Get unmatch details using webservice
     */
    private void getUnmatchDetails() {
        Intent intent=new Intent(ChatConversationActivity.this, UnmatchUserActivity.class);
        intent.putExtra("id",String.valueOf(userIds));
        startActivity(intent);
    }
    /**
     * Get unmatch user using webservice
     */
    private void unMatchUser() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchUser(sessionManager.getToken(), sessionManager.getUnMatchReasonId(), chatMessageModel.getLikedId()).enqueue(new RequestCallback(Enums.REQ_UNMATCH_USER, this));
    }
    /**
     * Scrolling the RecyclerView to the Bottom
     *
     */
    private void scrollToBottom(){
        chatConversationListAdapter.notifyDataSetChanged();


        rvChatConversationList.scrollToPosition(messageModels.size()-1);
        if (isFisrtScroll){
            isFisrtScroll=false;
            linearLayoutManager.setStackFromEnd(false);
        }

    }
    /**
     * Set chat conversation list adapter
     */
    private void setChatListAdapter() {
        chatConversationListAdapter = new ChatConversationListAdapter(this, messageModels, otherUserName);
        rvChatConversationList.setAdapter(chatConversationListAdapter);
        linearLayoutManager = (CustomLayoutManager) rvChatConversationList.getLayoutManager();

    }

    /**
     * API response success
     */

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case Enums.REQ_MSG_CONVERSATION:
                if (jsonResp.isSuccess()) {
                    onSuccessGetMsgList(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_SEND_MSG:
                if (jsonResp.isSuccess()) {

                    ReceiveDateModel receiveDateModel;
                    String dateResp = "";
                    String json = jsonResp.getStrResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        dateResp = String.valueOf(jsonObject.getJSONObject("messages").getJSONObject("received_date_time"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    receiveDateModel = gson.fromJson(dateResp, ReceiveDateModel.class);
                    messageModels.get(messageModels.size() - 1).setReceivedDate(receiveDateModel);
                    rltEmptyChat.setVisibility(View.GONE);
                    scrollToBottom();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_UNMATCH_DETAILS:
                if (jsonResp.isSuccess()) {
                    onSuccessGetUnmatchDetails(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_UNMATCH_USER:
                if (jsonResp.isSuccess()) {
                    onSuccessUnmatchUser(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }

    /**
     * API response failure
     */
    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    /**
     * Get Message list after API success
     */
    private void onSuccessGetMsgList(JsonResponse jsonResp) {
        chatMessageModel = gson.fromJson(jsonResp.getStrResponse(), ChatMessageModel.class);
        newMatchProfileModel = gson1.fromJson(jsonResp.getStrResponse(), NewMatchProfileModel.class);
        if (chatMessageModel != null) {
            updateView();
        }
    }

    /**
     * Get unmatch reason list after API success
     */
    private void onSuccessGetUnmatchDetails(JsonResponse jsonResp) {
        unMatchReasonModel = gson.fromJson(jsonResp.getStrResponse(), UnMatchReasonModel.class);
        if (unMatchReasonModel != null) {
            updateUnMatchView();
        }
    }

    /**
     * Get response after un match user
     */
    private void onSuccessUnmatchUser(JsonResponse jsonResp) {
        if (unMatchDialog.isShowing())
            unMatchDialog.dismiss();
        sessionManager.setIsUnmatch(true);
        finish();
    }

    /**
     * Update views based on the response
     */
    private void updateView() {

        userId = chatMessageModel.getUserId();
        if (sessionManager.getUserId() == 0)
            sessionManager.setUserId(userId);
        if (!TextUtils.isEmpty(chatMessageModel.getLikedImageUrl())) {
            imageUtils.loadCircleImage(this, civHeaderImgOne, chatMessageModel.getLikedImageUrl());
            imageUtils.loadCircleImage(this, civEmptyChatImage, chatMessageModel.getLikedImageUrl());

        }

        /*if (!TextUtils.isEmpty(chatMessageModel.getLikedImageUrl())) {
            imageUtils.loadCircleImage(this, civHeaderImgTwo, chatMessageModel.getLikedImageUrl());
        }*/

        if (!TextUtils.isEmpty(chatMessageModel.getLikedUsername())) {
            otherUserName = chatMessageModel.getLikedUsername();
            tvHeader.setText(String.format(getString(R.string.chat_conversation_title), otherUserName));
            tvName.setText(otherUserName);
        }

        if (chatMessageModel.getMessageModels() != null && chatMessageModel.getMessageModels().size() > 0) {
            rltEmptyChat.setVisibility(View.GONE);
            messageModels.clear();
            messageModels.addAll(chatMessageModel.getMessageModels());
            if (messageModels.size() > 0){
                scrollToBottom();
            }
        } else {
            rltEmptyChat.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update unmatch reason view
     */
    private void updateUnMatchView() {
        if (unMatchReasonModel.getResonModels() != null && unMatchReasonModel.getResonModels().size() > 0) {
            reasonModels.clear();
            reasonModels.addAll(unMatchReasonModel.getResonModels());
            showUnMatchDialog(getResources().getString(R.string.areyousure), getResources().getString(R.string.tell_us_why), 0, reasonModels);
        } else {
            //rltEmptyChat.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show unmatch reason dialog
     */
    public void showUnMatchDialog(String title, String message, int type, ArrayList<ReasonModel> reasonModel) {
        unMatchDialog = new Dialog(ChatConversationActivity.this);
        unMatchDialog.setContentView(R.layout.activity_cucustomcurve_dialog); //layout for dialog
        //dialog.setTitle("Add a new friend");
        unMatchDialog.setCancelable(true); //none-dismiss when touching outside Dialog
        unMatchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the custom dialog components - texts and image
        RelativeLayout rltReportImage = (RelativeLayout) unMatchDialog.findViewById(R.id.rlt_report_image);
        RelativeLayout rltUpdate = (RelativeLayout) unMatchDialog.findViewById(R.id.rlt_update);
        CustomTextView tvDialogTitle = (CustomTextView) unMatchDialog.findViewById(R.id.tv_dialog_title);
        CustomTextView tvMessage = (CustomTextView) unMatchDialog.findViewById(R.id.tv_message);
        CustomTextView tvUpdate = (CustomTextView) unMatchDialog.findViewById(R.id.tv_update);
        CustomRecyclerView rvReasonUnmatchList = (CustomRecyclerView) unMatchDialog.findViewById(R.id.rv_reason_unmatch_list);
        if (type == 0) {
            rltReportImage.setVisibility(View.GONE);
            rltUpdate.setVisibility(View.VISIBLE);
            rvReasonUnmatchList.setVisibility(View.VISIBLE);
            tvDialogTitle.setText(title);
            tvMessage.setText(message);
            tvUpdate.setText(getResources().getString(R.string.unmatch));

            linearLayoutManager = new CustomLayoutManager(this);
            rvReasonUnmatchList.setLayoutManager(linearLayoutManager);

            if (reasonModel.size() > 0) {
                unmatchReasonListAdapter = new UnmatchReasonListAdapter(this, reasonModel);
                rvReasonUnmatchList.setAdapter(unmatchReasonListAdapter);
                linearLayoutManager = (CustomLayoutManager) rvReasonUnmatchList.getLayoutManager();
            } else {
                unmatchReasonListAdapter = new UnmatchReasonListAdapter(this);
                rvReasonUnmatchList.setAdapter(unmatchReasonListAdapter);
            }
        }
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unMatchUser();
            }
        });
        unMatchDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //unMatchDialog.dismiss();
    }

    /**
     * Get notification from Firebase broadcast
     */
    public void receivePushNotification() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    String JSON_DATA = sessionManager.getPushNotification();

                    try {
                        JSONObject jsonObject = new JSONObject(JSON_DATA);

                        if (jsonObject.getJSONObject("custom").has("chat_status") && jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("status").equals("New message")) {

                            //getChatConversationList();
                            int userId=jsonObject.getJSONObject("custom").getJSONObject("chat_status").getInt("sender_id");
                            String cumessage = jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("message");
                            ReceiveDateModel receiveDateModel;//=new ReceiveDateModel();
                            String gifUrl=jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("gif_image_url");
                            String gifId=jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("gif_id");
                            String girRatio=jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("gif_ratio");
                            int messageId=jsonObject.getJSONObject("custom").getJSONObject("chat_status").getInt("message_id");
                            String dateResp = String.valueOf(jsonObject.getJSONObject("custom").getJSONObject("chat_status").getJSONObject("received_date_time"));
                            receiveDateModel = gson.fromJson(dateResp, ReceiveDateModel.class);
                            if (userId == userIds) {
                                MessageModel messageModel = new MessageModel();
                                messageModel.setMessage(cumessage);
                                messageModel.setSenderId(userIds);
                                messageModel.setGifRatio(girRatio);
                                messageModel.setGifUrl(gifUrl);
                                messageModel.setGifId(gifId);
                                messageModel.setReceivedDate(receiveDateModel);
                                messageModel.setLikeStatus("");
                                messageModel.setMessageId(10);
                                messageModel.setSenderImageUrl(chatMessageModel.getLikedImageUrl());

                                messageModels.add(messageModel);
                                rltEmptyChat.setVisibility(View.GONE);
                                scrollToBottom();
                            }
                            updateReadCount(String.valueOf(messageId));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        isConversationActivity = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        isConversationActivity = true;
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    @Override
    public void onClick(int index) {
        updateGradient(index);
        changeGradient(index);
    }
    private void changeGradient(int index){
        colorGlobalSelection=index;
        int topColor=Color.parseColor(topColors[index]);
        int bottomColor=Color.parseColor(bottomColors[index]);
        try {
            setGradient(topColor, bottomColor);
        }catch (Exception ex){
            Log.i("Exception",ex.getLocalizedMessage());
        }
    }
    private void updateGradient(int index){
        apiService.setGradient(sessionManager.getToken(),String.valueOf(index),String.valueOf(matchId)).enqueue(new RequestCallback(Enums.REQ_SEND_GRADIENT, this));
    }
    private void setGradient(int topColor,int bottomColor) throws Exception{
        tvLeftArrow.setTextColor(topColor);
        tvHeaderMenuIcon.setColorFilter(topColor);
        sendButtonSelectedColor=topColor;
                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[] {topColor,bottomColor});
                    rvChatConversationList.setBackground(gd);
                    rvChatConversationList.invalidate();
                    if (edtChatMsg.getText().length() > 0){
                        changeButtonColor(true);
                    }

    }
    public static int getHexColor(int[] color) {
        return android.graphics.Color.argb(color[0],color[1],color[2],color[3]);
    }
    private void updateReadCount(String messageId){
        apiService.updateReadCount(sessionManager.getToken(),messageId).enqueue(new RequestCallback(1, new ServiceListener() {
            @Override
            public void onSuccess(JsonResponse jsonResp, String data) {

            }

            @Override
            public void onFailure(JsonResponse jsonResp, String data) {

            }
        }));
    }
}