package ello.adapters.matches;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.matches
 * @category MatchesSwipeAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appolica.flubber.Flubber;
import com.appolica.flubber.interpolator.providers.Linear;
import com.obs.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.matches.MatchingProfile;
import ello.helpers.StaticData;
import ello.stories.StoriesProgressView;
import ello.utils.ImageUtils;
import ello.views.profile.EnlargeProfileActivity;

import static android.content.Context.VIBRATOR_SERVICE;

/*****************************************************************
 Adapter for matched swipe user
 ****************************************************************/

public class MatchesSwipeAdapter extends BaseAdapter {

    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private ArrayList<MatchingProfile> matchingProfiles = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private Vibrator myVib;
    public int indexOfInfo=0;
    public Boolean isFirst=true;
    public HashMap<Integer,LinearLayout> left=new HashMap<>();
    public HashMap<Integer,LinearLayout> right=new HashMap<>();
    private int milliSeconds=25;
    public MatchesSwipeAdapter(ArrayList<MatchingProfile> matchingProfilesList, Context context) {
        this.context = context;
        myVib=(Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        this.matchingProfiles = matchingProfilesList;
        this.inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
        left.clear();
        right.clear();
        indexOfInfo=0;
    }

    @Override
    public int getCount() {
        return matchingProfiles.size();
    }

    @Override
    public Object getItem(int position) {
        return matchingProfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.swipe_card_item, parent, false);
        }
        final CardView cardView=v.findViewById(R.id.card_view);
        final RelativeLayout uppertocuhLayout=v.findViewById(R.id.upperLayout);
        final RelativeLayout bottomTocuhlayout=v.findViewById(R.id.bottomLayout);;
        final StoriesProgressView storiesProgressView=v.findViewById(R.id.storiesView);
        final CustomViewPager viewPager =  v.findViewById(R.id.iv_user_image);
        CustomTextView userNameAge = (CustomTextView) v.findViewById(R.id.tv_user_name_age);
        CustomTextView userDesignation = (CustomTextView) v.findViewById(R.id.tv_designation);
        CustomTextView distanceTv=v.findViewById(R.id.tv_distance);
        CustomTextView userProfession = (CustomTextView) v.findViewById(R.id.tv_profession);
        ImageView superlike = (ImageView) v.findViewById(R.id.superlike);
        LinearLayout rightView=v.findViewById(R.id.rightView);
        LinearLayout leftView=v.findViewById(R.id.leftView);
        //ViewPager viewPager=v.findViewById(R.id.viewpager);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout2);
        storiesProgressView.bringToFront();

        final MatchingProfile currentUser = matchingProfiles.get(position);
        if (currentUser.getAllImages().size() > 1) {
            storiesProgressView.setStoriesCount(currentUser.getAllImages().size());
            storiesProgressView.skip();
        }
        if (currentUser != null) {
            //fill user Image
            float distance=Float.valueOf(currentUser.getKilometer());
            if (distance < 1){
                distanceTv.setText("Less than a kilometer away");
            }else if(distance==1){
                distanceTv.setText(currentUser.getKilometer()+" kilometer away");
            }else{
                distanceTv.setText(currentUser.getKilometer()+" kilometers away");
            }
            viewPager.setPagingEnabled(false);
            setUserImageAdapter(viewPager,currentUser.getAllImages());
            //imageUtils.loadSliderImage(context, userImage, currentUser.getImages());

            //fill user details
            if (!TextUtils.isEmpty(currentUser.getName()) && currentUser.getAge() > 0) {
                userNameAge.setText(new StringBuilder().append(currentUser.getName()).append(", ").append(currentUser.getAge()).toString());
            } else {
                userNameAge.setText(new StringBuilder().append(currentUser.getName()));
            }


            if (!TextUtils.isEmpty(currentUser.getSuperLike()) && currentUser.getSuperLike().equalsIgnoreCase("yes")) {
                superlike.setVisibility(View.VISIBLE);
            } else {
                superlike.setVisibility(View.GONE);
            }
        }

        /*linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnlargeProfileActivity.class);
                intent.putExtra("navType", 1);
                intent.putExtra("userId",  currentUser.getUserId());
                context.startActivity(intent);
            }
        });*/

        if(position == 0) {
            leftView.setVisibility(View.GONE);
            rightView.setVisibility(View.GONE);
            StaticData.isSwiped=true;
        }
        if (!left.containsKey(position)){
            left.put(position,leftView);
            right.put(position,rightView);
        }
        leftView.setVisibility(View.GONE);
        rightView.setVisibility(View.GONE);
        leftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLeft(currentUser,position,cardView,uppertocuhLayout,bottomTocuhlayout,viewPager,storiesProgressView);
            }
        });
        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRight(position,cardView,uppertocuhLayout,bottomTocuhlayout,viewPager,storiesProgressView);
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int j=position;
                int x = StaticData.touchX;

                int y = StaticData.touchY;
                if (x <= (v.getRight() / 2) && ((v.getBottom() / 3) * 2.5) >= y) {
                    onClickRight(position,cardView,uppertocuhLayout,bottomTocuhlayout,viewPager,storiesProgressView);

                } else if (x > (v.getRight() / 2) && ((v.getBottom() / 3) * 2.2) >= y) {
                    onClickLeft(currentUser,position,cardView,uppertocuhLayout,bottomTocuhlayout,viewPager,storiesProgressView);
                } else {
                    Intent intent = new Intent(context, EnlargeProfileActivity.class);
                    intent.putExtra("navType", 1);
                    intent.putExtra("userId", currentUser.getUserId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                }

            }
        });


        return v;
    }
    private void onClickLeft(MatchingProfile currentUser,int position,CardView cardView,RelativeLayout upperLayout,RelativeLayout bottomLayout,ViewPager viewPager,StoriesProgressView storiesProgressView){
        if (sessionManager.isFirst().equals("Yes")){
            if(position == indexOfInfo) {
                bottomLayout.setVisibility(View.VISIBLE);
                upperLayout.setVisibility(View.VISIBLE);
                sessionManager.setFirstTime("No");
            }else{
                if (viewPager.getCurrentItem() < currentUser.getAllImages().size() - 1) {
                    int i= viewPager.getCurrentItem() + 1;
                    movePhoto(false, storiesProgressView, viewPager, i);
                    myVib.vibrate(10);
                }else{
                    myVib.vibrate(10);
                    shakeView(cardView);
                    myVib.vibrate(10);
                }
            }
        }else{
            if(upperLayout.getVisibility() == View.VISIBLE) {
                upperLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                return ;
            }
            if (viewPager.getCurrentItem() < currentUser.getAllImages().size() - 1) {
                int i = viewPager.getCurrentItem() + 1;
                movePhoto(false, storiesProgressView, viewPager, i);
                myVib.vibrate(milliSeconds);
            }else{
                myVib.vibrate(milliSeconds);
                shakeView(cardView);
                myVib.vibrate(milliSeconds);
            }
        }
    }
    private void onClickRight(int position,CardView cardView,RelativeLayout upperLayout,RelativeLayout bottomLayout,ViewPager viewPager,StoriesProgressView storiesProgressView ){
        if (sessionManager.isFirst().equals("Yes")) {
            if(position == indexOfInfo) {
                upperLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                sessionManager.setFirstTime("No");
            }else{
                if (viewPager.getCurrentItem() > 0) {
                    int i=viewPager.getCurrentItem()-1;
                    movePhoto(true, storiesProgressView, viewPager, i);
                    myVib.vibrate(milliSeconds);
                }else{
                    myVib.vibrate(milliSeconds);
                    shakeView(cardView);
                    myVib.vibrate(milliSeconds);
                }
            }
        } else {
            if(upperLayout.getVisibility() == View.VISIBLE){
                upperLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                return;
            }
            if (viewPager.getCurrentItem() > 0) {
                int i = viewPager.getCurrentItem() - 1;
                movePhoto(true, storiesProgressView, viewPager, i);
                myVib.vibrate(milliSeconds);
            }else{
                myVib.vibrate(milliSeconds);
                shakeView(cardView);
                myVib.vibrate(milliSeconds);
            }
        }
    }
    public void setVisibilty(int position){
//        left.get(position).setVisibility(View.GONE);
//        right.get(position).setVisibility(View.GONE);
    }
    private void shakeView(CardView cardView){
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        //Flubber.with().animation(Flubber.AnimationPreset.SHAKE).duration(100).createFor(cardView).start();
        cardView.startAnimation(shake);
    }
    private void movePhoto(boolean isleft,StoriesProgressView progressView,ViewPager viewPager,int pageNumber){
        if (isleft){
            progressView.reverseStory(pageNumber);
        }else{
            progressView.skipStory(pageNumber);
        }
        viewPager.setCurrentItem(pageNumber);
    }
    private void setUserImageAdapter(ViewPager viewPager,ArrayList<String> userImages){
        ImageAdapter adapterView = new ImageAdapter(context,userImages);
        viewPager.setAdapter(adapterView);
    }

}
