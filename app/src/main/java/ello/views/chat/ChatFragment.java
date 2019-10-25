package ello.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category ChatFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import ello.R;
import ello.adapters.chat.AdapaterClick;
import ello.adapters.chat.MessageUserListAdapter;
import ello.adapters.chat.NewMatchcallback;
import ello.adapters.chat.NewMatchesListAdapter;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.chat.MatchedProfileModel;
import ello.datamodels.chat.MessageModel;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.chat.NotificationData;
import ello.datamodels.chat.ReceiveDateModel;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ActivityListener;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.pushnotification.Config;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.utils.SpacesItemDecoration;
import ello.views.customize.CustomDialog;
import ello.views.customize.CustomLayoutManager;
import ello.views.customize.CustomRecyclerView;
import ello.views.main.HomeActivity;

/*****************************************************************
 User chat list page (Home pages)
 ****************************************************************/
public class ChatFragment extends Fragment implements ServiceListener, AdapaterClick, NewMatchcallback {
    public int conv_no;
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
    private View view;
    private ActivityListener listener;
    private Resources res;
    private BroadcastReceiver mBroadcastReceiver;
    private HomeActivity mActivity;
    private CustomTextView tvNewMessageCount, tvNewMatchTitle, tvMessageTitle;
    private CustomRecyclerView rvNewMatchesList, rvMessagesList;
    private CustomEditText edtSearch;
    private RelativeLayout rlt_empty_message;
    private LinearLayout lltEmptyNewList, lltEmptySearchList;
    private NewMatchesListAdapter newMatchesListAdapter;
    private MessageUserListAdapter messageUserListAdapter;
    private ImageView ivEmptyMessageImage, ivEmptySearchImage;
    private TextView noMatch;
    private AlertDialog dialog;
    private MatchedProfileModel matchedProfileModel = new MatchedProfileModel();
    private ArrayList<NewMatchProfileModel> newMatchProfileModels = new ArrayList<>();
    private ArrayList<NewMatchProfileModel> messageModels = new ArrayList<>();
    private int width;
    private Boolean isFirsTime=true;
    private Boolean isFirsTimeViewCreated=true;

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
                    view = inflater.inflate(R.layout.chat_fragment, container, false);
                    edtSearch = (CustomEditText) view.findViewById(R.id.et_search);

                    tvNewMessageCount = (CustomTextView) view.findViewById(R.id.tv_new_match_count);
                    tvNewMatchTitle = (CustomTextView) view.findViewById(R.id.tv_new_match_title);
                    tvMessageTitle = (CustomTextView) view.findViewById(R.id.tv_messages_title);
                    rvNewMatchesList = (CustomRecyclerView) view.findViewById(R.id.rv_new_matches_list);
                    rvMessagesList = (CustomRecyclerView) view.findViewById(R.id.rv_message_list);
                    rlt_empty_message = (RelativeLayout) view.findViewById(R.id.rlt_empty_message);
                    ivEmptyMessageImage = (ImageView) view.findViewById(R.id.iv_empty_message_image);
                    ivEmptySearchImage = (ImageView) view.findViewById(R.id.iv_empty_search_image);//noMatch
                    noMatch = (TextView) view.findViewById(R.id.tv_no_match);
                    lltEmptyNewList = (LinearLayout) view.findViewById(R.id.llt_empty_new_list);
                    lltEmptySearchList = (LinearLayout) view.findViewById(R.id.llt_empty_search_list);
                    dialog = commonMethods.getAlertDialog(mActivity);
                    rvNewMatchesList.setNestedScrollingEnabled(false);
                    rvMessagesList.setNestedScrollingEnabled(false);

                    initRecyclerView();
                    receivePushNotification();
                    isFirsTimeViewCreated = false;
                    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                            new IntentFilter(Config.PUSH_NOTIFICATION));



                }

        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {




    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
        }
    }

    private void initRecyclerView() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (matchedProfileModel != null) {
                    // filter your list from your input
                    filter(s.toString());
                    //you can use runnable postDelayed like 500 ms to delay search text
                }
            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        width = (int) (metrics.widthPixels / 3.5);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvNewMatchesList.getLayoutParams();
        params.height = width;

        rvNewMatchesList.setLayoutParams(params);

        CustomLayoutManager layoutManager = new CustomLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        rvNewMatchesList.setLayoutManager(layoutManager);
        rvNewMatchesList.addItemDecoration(new SpacesItemDecoration(2));

        newMatchesListAdapter = new NewMatchesListAdapter(mActivity, newMatchProfileModels, width,this);
        rvNewMatchesList.setAdapter(newMatchesListAdapter);
        rvNewMatchesList.setHasFixedSize(true);

        CustomLayoutManager customLayoutManager = new CustomLayoutManager(mActivity);
        rvMessagesList.setLayoutManager(customLayoutManager);

        messageUserListAdapter = new MessageUserListAdapter(mActivity, messageModels,this);
        rvMessagesList.setAdapter(messageUserListAdapter);
        rvMessagesList.setHasFixedSize(true);
    }

    private void init() {
        if (listener == null) return;

        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (HomeActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        if (listener != null) listener = null;
        super.onDestroy();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser){
            loadData();
            ((HomeActivity)getActivity()).setViewPagerFront(false);
            ((HomeActivity)getActivity()).changeDirection("all");

        }
    }
    private void loadData(){
        if (StaticData.CHAT == 0) {
                matchedProfileModel=StaticData.matchedProfileModel;
                matchedProfileModel.setUnReadCount(StaticData.unReadCount);
                matchedProfileModel.setNewMatchCount(StaticData.unReadNewMatch);
                updateView();
        } else {
            getMatchedProfile();

        }
        sessionManager.setIsUnmatch(false);
        //getFragmentManager().popBackStack();

        edtSearch.clearFocus();
        //hideKeyboard(getContext());
    }
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
                    // new push notification is receivedc

                    ((HomeActivity)getActivity()).changeChatIcon(1);
                  getMatchedProfile();

                    }
                }

        };
    }
    private void getMatchedProfile() {
        Log.i("Chats",sessionManager.getToken());
        //commonMethods.showProgressDialog(mActivity, customDialog);
        StaticData.unReadNewMatch=0;
        StaticData.unReadCount=0;
        apiService.matchedDetails(sessionManager.getToken()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        if (jsonResp.isSuccess()) {
            onSuccessGetMatchedList(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(StaticData.CHAT == 1){
            getMatchedProfile();
            StaticData.CHAT=0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        edtSearch.clearFocus();
        hideKeyboard(getContext());
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        //if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessGetMatchedList(JsonResponse jsonResp) {
        StaticData.CHAT = 0;
        matchedProfileModel = gson.fromJson(jsonResp.getStrResponse(), MatchedProfileModel.class);
        if (matchedProfileModel != null) {
            StaticData.matchedProfileModel=this.matchedProfileModel;
            if(matchedProfileModel.getUnReadCount() > 0 || matchedProfileModel.getNewMatchCount() > 0){
                ((HomeActivity) getActivity()).changeChatIcon(1);
            }else{
                ((HomeActivity) getActivity()).changeChatIcon(0);
            }
            updateView();
        } else {
            rlt_empty_message.setVisibility(View.VISIBLE);
        }
    }


    private void updateView() {

        conv_no = matchedProfileModel.getNewMatchProfile().size() + matchedProfileModel.getMessage().size();
        edtSearch.setHint(getResources().getText(R.string.search) + " " + conv_no + " " + getResources().getText(R.string.matches));
        if (conv_no>0) {
            ivEmptySearchImage.setVisibility(View.GONE);
            noMatch.setVisibility(View.GONE);
        }
        if ((matchedProfileModel.getNewMatchProfile() == null || matchedProfileModel.getNewMatchProfile().size() <= 0)
                && (matchedProfileModel.getMessage() == null || matchedProfileModel.getMessage().size() <= 0)) {
            rlt_empty_message.setVisibility(View.VISIBLE);
        } else {
            rlt_empty_message.setVisibility(View.GONE);
        }



        if (matchedProfileModel.getNewMatchProfile() != null && (matchedProfileModel.getNewMatchProfile().size() > 0)) {// ||matchedProfileModel.getLikesCount()>0) {
                newMatchProfileModels.clear();
                newMatchProfileModels.addAll(matchedProfileModel.getNewMatchProfile());
                tvNewMessageCount.setVisibility(View.VISIBLE);
                tvNewMatchTitle.setVisibility(View.VISIBLE);
                rvNewMatchesList.setVisibility(View.VISIBLE);
                int n=matchedProfileModel.getNewMatchProfile().size();
                tvNewMessageCount.setText(String.valueOf(matchedProfileModel.getNewMatchProfile().size()));
                newMatchesListAdapter.setArrayData(newMatchProfileModels);
                newMatchesListAdapter.notifyDataSetChanged();

        } else {
            tvNewMatchTitle.setVisibility(View.GONE);
            tvNewMessageCount.setVisibility(View.GONE);
            rvNewMatchesList.setVisibility(View.GONE);
        }

        if (matchedProfileModel.getMessage() != null && matchedProfileModel.getMessage().size() > 0) {
            messageModels.clear();
            messageModels.addAll(matchedProfileModel.getMessage());
            tvMessageTitle.setVisibility(View.VISIBLE);
            rvMessagesList.setVisibility(View.VISIBLE);
            messageUserListAdapter.setArraysData(messageModels);
            messageUserListAdapter.notifyDataSetChanged();
        } else if(newMatchProfileModels.size() == 0) {
            tvMessageTitle.setVisibility(View.GONE);
            rvMessagesList.setVisibility(View.VISIBLE);
            messageUserListAdapter.setArraysData(messageModels);
            messageUserListAdapter.notifyDataSetChanged();

        }else{
            tvMessageTitle.setVisibility(View.GONE);
            rvMessagesList.setVisibility(View.GONE);
        }

    }


    void filter(String text) {
        ArrayList temp = new ArrayList();
        ArrayList temp1 = new ArrayList();
        for (NewMatchProfileModel d : messageModels) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUserName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        messageUserListAdapter.updateList(temp);

        for (NewMatchProfileModel d : newMatchProfileModels) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUserName().toLowerCase().contains(text.toLowerCase())) {
                temp1.add(d);
            }
        }
        //update recyclerview
        newMatchesListAdapter.updateList(temp1);

        if (temp.size() <= 0) {
            ivEmptyMessageImage.setVisibility(View.GONE);
        } else {
            ivEmptyMessageImage.setVisibility(View.GONE);
        }

        if (temp1.size() <= 0) {
            lltEmptyNewList.setVisibility(View.GONE);
        } else {
            lltEmptyNewList.setVisibility(View.VISIBLE);
        }

        if (temp.size() <= 0 && temp1.size() <= 0) {
            lltEmptySearchList.setVisibility(View.GONE);
            ivEmptySearchImage.setVisibility(View.VISIBLE);
            noMatch.setVisibility(View.VISIBLE);
        } else {
            lltEmptySearchList.setVisibility(View.VISIBLE);
            ivEmptySearchImage.setVisibility(View.GONE);
            noMatch.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(int index) {
        NewMatchProfileModel matchProfileModel = messageModels.get(index);

        if (matchProfileModel != null) {
            matchProfileModel.setReadStatus("Read");
            StaticData.CHAT=1;
            Intent intent = new Intent(getActivity(), ChatConversationActivity.class);
            intent.putExtra("matchId", matchProfileModel.getMatchId());
            intent.putExtra("userId", matchProfileModel.getUserId());
            intent.putExtra("gradientNo",matchProfileModel.getGradientNo());
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void newMatchClick(NewMatchProfileModel matchProfileModel) {
        apiService.likesToRead(sessionManager.getToken(), matchProfileModel.getUserId().toString()).enqueue(new RequestCallback(new ServiceListener() {
            @Override
            public void onSuccess(JsonResponse jsonResp, String data) {
                getMatchedProfile();
            }

            @Override
            public void onFailure(JsonResponse jsonResp, String data) {

            }
        }));
        ((HomeActivity)getActivity()).setFavouriteCard(matchProfileModel);

    }
}
