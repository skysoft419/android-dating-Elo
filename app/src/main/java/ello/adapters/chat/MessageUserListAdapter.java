package ello.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category MessageUserListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.configs.AppController;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.helpers.StaticData;
import ello.utils.ImageUtils;
import ello.views.action.ReportUserActivity;
import ello.views.action.UnmatchUserActivity;
import ello.views.chat.ChatConversationActivity;

/*****************************************************************
 Adapter for Chatted user list
 ****************************************************************/
public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.RecyclerViewHolder> {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NewMatchProfileModel> messageList;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private AdapaterClick callback;
    public MessageUserListAdapter(Context context) {
        this.context = context;
        messageList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
        binderHelper.setOpenOnlyOne(true);
    }

    public MessageUserListAdapter(Context context, ArrayList<NewMatchProfileModel> messageList,AdapaterClick callback) {
        this.context = context;
        this.callback=callback;
        this.messageList = messageList;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
        binderHelper.setOpenOnlyOne(true);
    }
    public void setArraysData(ArrayList<NewMatchProfileModel> messageList){
        this.messageList = messageList;
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = inflater.inflate(R.layout.row_message_user_layout, parent, false);
            return new RecyclerViewHolder(v,viewType);
        }else{
            View v = inflater.inflate(R.layout.start_chat, parent, false);
            return new RecyclerViewHolder(v,viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (messageList.size() > 0) {
            NewMatchProfileModel messageModel = messageList.get(position);
            binderHelper.bind(holder.swipeLayout, messageModel.getUserId().toString());
            if (messageModel != null) {
                if (!TextUtils.isEmpty(messageModel.getUserName())) {
                    holder.tvUserName.setText(messageModel.getUserName());
                }
                if (!TextUtils.isEmpty(messageModel.getLastMessage())) {
                    holder.tvLastMsg.setText(messageModel.getLastMessage());
                }
                if (!TextUtils.isEmpty(messageModel.getIsReply()) && messageModel.getIsReply().equalsIgnoreCase("yes")) {
                    holder.ivReply.setVisibility(View.VISIBLE);
                } else {
                    holder.ivReply.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(messageModel.getReadStatus()) && messageModel.getReadStatus().equalsIgnoreCase("Unread")) {
                    holder.civNewMessageAlert.setVisibility(View.VISIBLE);
                    holder.rlt_new_msg_alert.setVisibility(View.VISIBLE);
                } else {
                    holder.civNewMessageAlert.setVisibility(View.GONE);
                    holder.rlt_new_msg_alert.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(messageModel.getLikeStatus()) && messageModel.getLikeStatus().equalsIgnoreCase("super_like")) {
                    holder.civSuperLike.setVisibility(View.VISIBLE);
                } else {
                    holder.civSuperLike.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(messageModel.getUserImgUrl())) {
                    imageUtils.loadCircleImage(context, holder.civUserImg, messageModel.getUserImgUrl());
                } else {
                    holder.civUserImg.setImageResource(R.drawable.chat_user_bg);
                }
                holder.rltUserMessage.setTag(holder);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (messageList.size() > 0){
            return messageList.size();
        }else {
             return 1;
        }
    }



    public void updateList(ArrayList<NewMatchProfileModel> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.size() > 0){
            return  1;
        }else{
            return 0;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView civUserImg;
        private ImageView civNewMessageAlert, civSuperLike;
        private CustomTextView tvUserName, tvLastMsg;
        private RelativeLayout rltUserMessage, rlt_new_msg_alert;
        private ImageView ivReply;
        private SwipeRevealLayout swipeLayout;
        private LinearLayout unmatch;
        private LinearLayout report;
        private TextView unmatchTextView;
        private TextView reportTextView;
        private ImageView reportImageView;
        private ImageView unmatchImageView;

        public RecyclerViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType == 1) {
                swipeLayout = itemView.findViewById(R.id.swipeLayout);
                reportImageView = itemView.findViewById(R.id.reportIcon);
                unmatchImageView = itemView.findViewById(R.id.unmatchIcon);
                reportTextView = itemView.findViewById(R.id.reportTextView);
                unmatchTextView = itemView.findViewById(R.id.unmatchTextView);
                civUserImg = itemView.findViewById(R.id.civ_user_image);
                unmatch = itemView.findViewById(R.id.unmatch);
                report = itemView.findViewById(R.id.report);
                tvUserName = (CustomTextView) itemView.findViewById(R.id.tv_user_name);
                tvLastMsg = (CustomTextView) itemView.findViewById(R.id.tv_last_msg);
                civNewMessageAlert = (ImageView) itemView.findViewById(R.id.civ_new_msg_alert);
                civSuperLike = (ImageView) itemView.findViewById(R.id.civ_super_like);
                rltUserMessage = (RelativeLayout) itemView.findViewById(R.id.rlt_user_message_root);
                rlt_new_msg_alert = (RelativeLayout) itemView.findViewById(R.id.rlt_new_msg_alert);
                ivReply = (ImageView) itemView.findViewById(R.id.iv_reply);
                rltUserMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onClick(getAdapterPosition());
                    }
                });
                unmatchTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UnmatchUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                        StaticData.CHAT=1;
                    }
                });
                unmatchImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UnmatchUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                        StaticData.CHAT=1;
                    }
                });
                unmatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, UnmatchUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                        StaticData.CHAT=1;
                    }
                });
                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReportUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                        StaticData.CHAT=1;
                    }
                });
                reportTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReportUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                    }
                });
                reportImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ReportUserActivity.class);
                        intent.putExtra("id", messageList.get(getAdapterPosition()).getUserId().toString());
                        context.startActivity(intent);
                        StaticData.CHAT=1;
                    }
                });
            }
        }
    }
}