package ello.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category ChatConversationListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.views.GPHMediaView;
import com.obs.CustomTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.chat.MessageModel;
import ello.datamodels.chat.ReceiveDateModel;
import ello.stories.MyAppGlideModule;
import ello.utils.CommonMethods;
import ello.utils.DateTimeUtility;
import ello.utils.ImageUtils;
import ello.views.profile.EnlargeProfileActivity;

/*****************************************************************
 Adapter for chat conversation page
 ****************************************************************/


public class ChatConversationListAdapter extends RecyclerView.Adapter<ChatConversationListAdapter.RecyclerViewHolder> implements View.OnClickListener {

    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    @Inject
    DateTimeUtility dateTimeUtility;
    private Context context;
    private ArrayList<MessageModel> messageModels;
    private LayoutInflater inflater;
    private String otherUserName;
    private String msgDate = "", msgDate_old = "";


    public ChatConversationListAdapter(Context context) {
        this.context = context;
        this.messageModels = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(ChatConversationListAdapter.this);
    }

    public ChatConversationListAdapter(Context context, ArrayList<MessageModel> messageModels, String otherUserName) {
        this.context = context;
        this.messageModels = messageModels;
        this.otherUserName = otherUserName;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(ChatConversationListAdapter.this);
    }

    @Override
    public ChatConversationListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_chat, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatConversationListAdapter.RecyclerViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if (messageModel == null) return;

        try {
            if (position == 0){
                String currentdate = convertCurrentDate(0);
                msgDate = convertDate(messageModel.getReceivedDate().getDate());
                holder.tvDay.setVisibility(View.VISIBLE);
                if (currentdate.equals(msgDate)) {
                    holder.tvDay.setText("Today");
                } else if (convertCurrentDate(1).equals(msgDate)) {
                    holder.tvDay.setText("Yesterday");
                } else
                    holder.tvDay.setText(msgDate);
                String date=dateTimeUtility.getChatFormatDate(messageModel.getReceivedDate().getDate());
                if (date.length() == 16){
                    if (messageModel.getSenderId().equals(sessionManager.getUserId())){
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date.substring(7, 16));
                    }else{
                        holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                        holder.tvOtherUserTime.setText(date.substring(7, 16));
                    }
                }else{
                    if (messageModel.getSenderId().equals(sessionManager.getUserId())){
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date.substring(7, 15));
                    }else{
                        holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                        holder.tvOtherUserTime.setText(date.substring(7, 15));
                    }
                }
            }else{
                MessageModel messageModel1=messageModels.get(position-1);
                showDay(messageModel,messageModel1,holder);
                showDate(messageModel,messageModel1,holder);
            }

            if (messageModel.getGifId() != null&& !messageModel.getGifId().isEmpty()&& !messageModel.getGifId().equals("null")){
                showGifMedia(holder,messageModel);
            }else {
                showMessage(messageModel,holder);

        } }catch (Exception e) {
            e.printStackTrace();
        }


    }
    //For showing message text in Message TextView
    private void showMessage(MessageModel messageModel,RecyclerViewHolder holder){
        if (messageModel.getSenderId().equals(sessionManager.getUserId())){
            holder.userlayout.setVisibility(View.VISIBLE);
            holder.tvUsermsg.setText(messageModel.getMessage());
        }else{
            holder.otherUserLayout.setVisibility(View.VISIBLE);
            holder.tvOtherUserMsg.setText(messageModel.getMessage());
        }
    }
    //For showing the Days e.g Today,yesterday....
    private void showDay(MessageModel messageModel,MessageModel messageModel1,RecyclerViewHolder holder){
        String date1=convertDate(messageModel.getReceivedDate().getDate());
        String date2=convertDate(messageModel1.getReceivedDate().getDate());
        if (date1.equals(date2)){

        }else{
            String currentdate = convertCurrentDate(0);
            msgDate = convertDate(messageModel.getReceivedDate().getDate());
                holder.tvDay.setVisibility(View.VISIBLE);
            if (currentdate.equals(msgDate)) {
                holder.tvDay.setText("Today");
            } else if (convertCurrentDate(1).equals(msgDate)) {
                holder.tvDay.setText("Yesterday");
            } else
                holder.tvDay.setText(msgDate);
        }
    }
    //For Calculating the Data difference.
    private void showDate(MessageModel messageModel,MessageModel previousMessageModel,RecyclerViewHolder holder){
        String date1=dateTimeUtility.getChatFormatDate(messageModel.getReceivedDate().getDate());
        String date2=dateTimeUtility.getChatFormatDate(previousMessageModel.getReceivedDate().getDate());
        if(messageModel.getSenderId().equals(previousMessageModel.getSenderId())){
            if (messageModel.getSenderId().equals(sessionManager.getUserId())){
                showDateOfUser(date1,date2,0,holder);
            }else{
                showDateOfUser(date1,date2,1,holder);
            }
        }else{
            if (messageModel.getSenderId().equals(sessionManager.getUserId())){
                if (date1.length() == 16) {
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date1.substring(7, 16));

                } else {
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date1.substring(7, 15));

                }
            }else{
                if (date1.length() == 16) {
                    holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                    holder.tvOtherUserTime.setText(date1.substring(7, 16));

                } else {
                    holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                    holder.tvOtherUserTime.setText(date1.substring(7, 15));

                }
            }
        }
    }
    //
    private void showDateOfUser(String date1,String date2,int userId,RecyclerViewHolder holder){
        if (date1.length()==date2.length()){
            if (date1.equals(date2)){
            }else {
                if (date1.length() == 16) {
                    if (userId == 0) {
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date1.substring(7, 16));
                    }else{
                        holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                        holder.tvOtherUserTime.setText(date1.substring(7, 16));
                    }
                } else {
                    if (userId == 0) {
                        holder.tvUserTime.setVisibility(View.VISIBLE);
                        holder.tvUserTime.setText(date1.substring(7, 15));
                    }else{
                        holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                        holder.tvOtherUserTime.setText(date1.substring(7, 15));
                    }
                }
            }
        }else{
            if (date1.length() == 16) {
                if (userId == 0) {
                    holder.tvUserTime.setVisibility(View.VISIBLE);
                    holder.tvUserTime.setText(date1.substring(7, 16));
                }else{
                    holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                    holder.tvOtherUserTime.setText(date1.substring(7, 16));
                }
            } else {
                if (userId == 0) {
                    holder.tvUserTime.setVisibility(View.VISIBLE);
                    holder.tvUserTime.setText(date1.substring(7, 15));
                }else{
                    holder.tvOtherUserTime.setVisibility(View.VISIBLE);
                    holder.tvOtherUserTime.setText(date1.substring(7, 15));
                }
            }
        }
    }
    private void showGifMedia(ChatConversationListAdapter.RecyclerViewHolder holder,MessageModel messageModel){
        if(messageModel.getSenderId().equals(sessionManager.getUserId())){
            holder.userCardView.setVisibility(View.VISIBLE);
            Float height=500*Float.valueOf(messageModel.getGifRatio());
            holder.userMedia.getLayoutParams().width=500;
            holder.userMedia.getLayoutParams().height=Math.round(height);
            holder.userMedia.requestLayout();
            Glide.with(context).asGif().load(messageModel.getGifUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).centerCrop().into(holder.userMedia);

        }else{
            holder.otherUserCardView.setVisibility(View.VISIBLE);
            Float height=500*Float.valueOf(messageModel.getGifRatio());
            holder.otherUserMedia.getLayoutParams().width=500;
            holder.otherUserMedia.getLayoutParams().height=Math.round(height);
            holder.otherUserMedia.requestLayout();
            Glide.with(context).asGif().load(messageModel.getGifUrl()).centerCrop().into(holder.otherUserMedia);

        }
    }
    public String convertCurrentDate(int type) {
        String inputPattern = "EEE mmm dd HH:mm:ss z yyyy";
        String outputPattern = "MMMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date currentdate = new Date();
        Date date = null;
        String str = null;

        //try {
        // date = inputFormat.parse(currentdate);
        if (type == 0) {
            //str = outputFormat.format(date);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 0);
            str = outputFormat.format(cal.getTime());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            str = outputFormat.format(cal.getTime());
        }

        /*} catch (ParseException e) {
            e.printStackTrace();
        }*/
        return str;
    }

    public String convertDate(String time) {

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();

        MessageModel messageModel = messageModels.get(clickedPosition);
        if (messageModel != null) {
            Intent intent = new Intent(context, EnlargeProfileActivity.class);
            intent.putExtra("navType", 3);
            intent.putExtra("userId", messageModel.getSenderId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView tvUsermsg,tvOtherUserMsg,tvUserTime,tvOtherUserTime,tvDay;
        public ImageView otherUserMedia,userMedia;
        public LinearLayout userlayout,otherUserLayout;
        public CardView userCardView,otherUserCardView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            /*user chat row*/
            otherUserLayout=itemView.findViewById(R.id.othermessagelayout);
            userlayout=itemView.findViewById(R.id.messagelayout);
            tvUsermsg=itemView.findViewById(R.id.tvUserMessage);
            tvOtherUserMsg=itemView.findViewById(R.id.tvOtherUserMessage);
            tvUserTime=itemView.findViewById(R.id.tvUserTime);
            tvOtherUserTime=itemView.findViewById(R.id.tvOtherUserTime);
            tvDay=itemView.findViewById(R.id.tvUserDay);
            userCardView=itemView.findViewById(R.id.userCardView);
            otherUserMedia=itemView.findViewById(R.id.otherGifView);
            userMedia=itemView.findViewById(R.id.userGifView);
            otherUserCardView=itemView.findViewById(R.id.otherGifCard);

        }
    }
}
