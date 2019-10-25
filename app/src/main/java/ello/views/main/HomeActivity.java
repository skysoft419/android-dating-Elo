package ello.views.main;




import android.app.Activity;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.obs.CustomTextView;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ello.R;
import ello.adapters.action.AdapterCallback;
import ello.backgroundtask.ImageCompressAsyncTask;
import ello.configs.AppController;
import ello.configs.AppLifecycleListener;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.chat.MatchedProfileModel;
import ello.datamodels.chat.NewMatchProfileModel;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.helpers.FixedSpeedScroller;
import ello.helpers.StaticData;
import ello.interfaces.ActivityListener;
import ello.interfaces.ApiService;
import ello.interfaces.ImageListener;
import ello.interfaces.ServiceListener;
import ello.pushnotification.Config;
import ello.pushnotification.NotificationUtils;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.chat.ChatFragment;
import ello.views.customize.CustomDialog;
import ello.views.customize.IgniterViewPager;
import ello.views.customize.SwipeDirection;
import ello.views.profile.ProfileFragment;
import okhttp3.RequestBody;

import static ello.utils.Enums.REQ_UPLOAD_PROFILE_IMG;

/*****************************************************************
 Application home page contain Home page fragment (Profile,home,chat)
 ****************************************************************/
public class HomeActivity extends AppCompatActivity implements ServiceListener,ActivityListener, ViewPager.OnPageChangeListener,AdapterCallback, ImageListener {

  public IgniterViewPager viewPager;
  @Inject
  ApiService apiService;
  @Inject
  SessionManager sessionManager;
  @Inject
  CommonMethods commonMethods;
  @Inject
  RunTimePermission runTimePermission;
  @Inject
  CustomDialog customDialog;
  public  TabLayout tabLayout;
  private IgniterPageFragment igniterPageFragment;
  private int backPressed = 0;    // used by onBackPressed()
  private boolean isPermissionGranted = false;
  private BroadcastReceiver mBroadcastReceiver;
  private MyAdapter myAdapter;
  private String imagePath = "";
  private ChatFragment chat;

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
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home_tab_layout);

    AppController.getAppComponent().inject(this);
    initViews();
    ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleListener());
    if (getIntent().getStringExtra("super") != null){
      StaticData.CHAT=0;
      final Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          viewPager.setCurrentItem(2);
        }
      }, 2000);

    }
  }

  /**
   * Declare variable for layout views
   */
  private void initViews() {
    tabLayout = (TabLayout) findViewById(R.id.tabs);
    viewPager = (IgniterViewPager) findViewById(R.id.viewpager);
    myAdapter=new MyAdapter(getSupportFragmentManager());
    viewPager.setAdapter(myAdapter);
    viewPager.setAllowedSwipeDirection(SwipeDirection.all);

    viewPager.setCurrentItem(1);
    try {
      Field mScroller;
      mScroller = ViewPager.class.getDeclaredField("mScroller");
      mScroller.setAccessible(true);
      FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),  new DecelerateInterpolator());
      // scroller.setFixedDuration(5000);
      mScroller.set(viewPager, scroller);
    } catch (NoSuchFieldException e) {
    } catch (IllegalArgumentException e) {
    } catch (IllegalAccessException e) {
    }
    tabLayout.post(new Runnable() {
      @Override
      public void run() {
        tabLayout.setupWithViewPager(viewPager);
        setupIcons();
        if (StaticData.unReadCount > 0 || StaticData.unReadNewMatch > 0){
          changeChatIcon(1);
        }else{
          changeChatIcon(0);
        }
      }
    });
    receivePushNotification();
  }
   public void setFavouriteCard(NewMatchProfileModel matchProfileModel){
      StaticData.isFromFavourite=true;
      viewPager.setCurrentItem(1);
      igniterPageFragment.setFavouriteCard(matchProfileModel);
  }

  /**
   * setup tab icons using font
   */
  public void setupIcons() {
    try {
      tabLayout.getTabAt(0).setCustomView(getTabView(R.string.ic_user_black_bg, 0));
      tabLayout.getTabAt(1).setCustomView(getTabView(R.string.ic_hot_or_burn, 1));
      tabLayout.getTabAt(2).setCustomView(getTabView(R.string.ic_chat_1, 0));

      viewPager.addOnPageChangeListener(this);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void setViewPagerFront(boolean isCradAvailable){
    if(viewPager.getCurrentItem() == 1) {
      if (isCradAvailable) {
        viewPager.bringToFront();
      } else {
        tabLayout.bringToFront();
      }
    }else{
      tabLayout.bringToFront();
    }
  }
  /**
   * Get tabs using custom text
   */
  private View getTabView(int title, int type) {
    RelativeLayout tabView = (RelativeLayout) LayoutInflater.from(HomeActivity.this).inflate(R.layout.igniter_tab_view, null);
    CustomTextView tabtext = (CustomTextView) tabView.findViewById(R.id.tv_igniter_tab_view);
    ImageView tabImage = (ImageView) tabView.findViewById(R.id.iv_igniter_tab_indicator);
    RelativeLayout tablay = (RelativeLayout) tabView.findViewById(R.id.rlt_igniter_tab_indicator);
    RelativeLayout rltLogo = (RelativeLayout) tabView.findViewById(R.id.rlt_logo);
    ImageView ivLogo = (ImageView) tabView.findViewById(R.id.iv_logo);
    tabtext.setText(title);
    tabtext.setTextColor(getResources().getColorStateList(R.color.selected_tab));

    if (type == 1) {
      tabtext.setVisibility(View.INVISIBLE);
      ivLogo.setVisibility(View.VISIBLE);
      rltLogo.setVisibility(View.INVISIBLE);
    } else {
      tabtext.setVisibility(View.VISIBLE);
      ivLogo.setVisibility(View.INVISIBLE);
      rltLogo.setVisibility(View.VISIBLE);
    }
    //tabtext.setTextSize(35);
    tabImage.setVisibility(View.GONE);
    tablay.setVisibility(View.GONE);

    return tabView;
  }
  @Override
  public Resources getRes() {
    return HomeActivity.this.getResources();
  }

  @Override
  public HomeActivity getInstance() {
    return HomeActivity.this;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override
  public void onPageSelected(int position) {


  }

  @Override
  public void onPageScrollStateChanged(int state) {
    Log.d("", "");
  }

  /**
   * Check all permission
   */
  private void checkAllPermission(String[] permission) {
    ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(HomeActivity.this, permission);
    if (blockedPermission != null && !blockedPermission.isEmpty()) {
      boolean isBlocked = runTimePermission.isPermissionBlocked(HomeActivity.this, blockedPermission.toArray(new String[blockedPermission.size()]));
      if (isBlocked) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
          public void run() {
            showEnablePermissionDailog();
          }
        });
      } else {
        ActivityCompat.requestPermissions(HomeActivity.this, permission, 150);
      }
    } else {
      isPermissionGranted = true;
    }
  }

  /**
   * Request permission result
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        }*/
        /*super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(igniterPageFragment!=null)
            igniterPageFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);*/
  }


  /**
   * Show enable permission dialog
   */
  private void showEnablePermissionDailog() {
    if (!customDialog.isVisible()) {
      customDialog = new CustomDialog(getString(R.string.please_enable_permissions), getString(R.string.ok), new CustomDialog.btnAllowClick() {
        @Override
        public void clicked() {
          callPermissionSettings();
        }
      });
      customDialog.show(getSupportFragmentManager(), "");
    }
  }

  /**
   * While block (Deny) call settings page
   */
  private void callPermissionSettings() {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", HomeActivity.this.getApplicationContext().getPackageName(), null);
    intent.setData(uri);
    startActivityForResult(intent, 300);
  }

  /**
   * Get view pager from other fragment
   */
  public IgniterViewPager getViewPager() {
    return viewPager;
  }


  public void setViewPager(IgniterViewPager viewPager) {
    this.viewPager = viewPager;
  }

  /**
   * Home on Back press function
   */
  @Override
  public void onBackPressed() {
    if (backPressed >= 1) {
      finishAffinity();
      super.onBackPressed();       // bye


    } else {


      // clean up
      backPressed = backPressed + 1;
      Toast.makeText(this, "Press back again to exit.",
              Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * View pager direction change
   */
  public void changeDirection(String type) {
    if (viewPager.getCurrentItem() == 1) {
      if (type.equals("all"))
        viewPager.setAllowedSwipeDirection(SwipeDirection.all);
      else
        viewPager.setAllowedSwipeDirection(SwipeDirection.none);
    }else{
      viewPager.setAllowedSwipeDirection(SwipeDirection.all);
    }
  }

  /**
   * Change chat tab layout icon
   */
  public void changeChatIcon(int type) {
    TabLayout.Tab tab = tabLayout.getTabAt(2);
    View view = tab.getCustomView();
    RelativeLayout tablay = (RelativeLayout)
            view.findViewById(R.id.rlt_igniter_tab_indicator);
    ImageView tabImage = (ImageView)
            view.findViewById(R.id.iv_igniter_tab_indicator);
    TextView tabText = (TextView)
            view.findViewById(R.id.tv_igniter_tab_view);
    if (type == 1) {
      tabText.setText("b");
      tabImage.setVisibility(View.VISIBLE);
      tablay.setVisibility(View.VISIBLE);
    } else {
      tabText.setText("b");
      tabImage.setVisibility(View.GONE);
      tablay.setVisibility(View.GONE);
    }
    viewPager.addOnPageChangeListener(this);
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
          changeChatIcon(1);
          if (viewPager.getCurrentItem() != 2) {
            apiService.matchedDetails(sessionManager.getToken()).enqueue(new RequestCallback(new ServiceListener() {
              @Override
              public void onSuccess(JsonResponse jsonResp, String data) {
                StaticData.matchedProfileModel = new Gson().fromJson(jsonResp.getStrResponse(), MatchedProfileModel.class);

              }

              @Override
              public void onFailure(JsonResponse jsonResp, String data) {

              }
            }));

          }
        }
      }
    };
  }

  @Override
  public void onPause() {
    super.onPause();
    //LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    hideKeyboard(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    hideKeyboard(this);
    if (StaticData.CHAT == 1){
      getMatchedProfile();
    }
    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    //check for runtime permission
    if (!isPermissionGranted) {
      //checkAllPermission(Constants.PERMISSIONS_STORAGE);
    }

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
  private void getMatchedProfile() {
    Log.i("Chats",sessionManager.getToken());
    //commonMethods.showProgressDialog(mActivity, customDialog);

    apiService.matchedDetails(sessionManager.getToken()).enqueue(new RequestCallback(1,this));
  }
  @Override
  public void onSuccess(JsonResponse jsonResp, String data) {
    commonMethods.hideProgressDialog();
    if (!jsonResp.isOnline()) {

      return;
    }
    switch (jsonResp.getRequestCode()){
      case REQ_UPLOAD_PROFILE_IMG:
        EditProfileModel editProfileModel = new Gson().fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
        StaticData.editProfileModel.getImageList().clear();
        StaticData.editProfileModel.setImageList(editProfileModel.getImageList());
        break;
      case 1:
        onSuccessGetMatchedList(jsonResp);
        break;
    }

  }
  @Override
  public void onFailure(JsonResponse jsonResp, String data) {

  }

  private void onSuccessGetMatchedList(JsonResponse jsonResp) {
    MatchedProfileModel matchedProfileModel = new Gson().fromJson(jsonResp.getStrResponse(), MatchedProfileModel.class);
    if (matchedProfileModel != null) {
      StaticData.matchedProfileModel=matchedProfileModel;

    } else {

    }
  }
  @Override
  public void onClick(int position) {
    if(StaticData.SETTING == 1){
      viewPager.setCurrentItem(0);
    }
  }

  @Override
  public void onImageCompress(String filePath, RequestBody requestBody) {

    apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));



  }

  /**
   * Adapter for tabs
   */
  private class MyAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    private MyAdapter(FragmentManager fm) {
      super(fm);
      mFragmentManager = fm;
      mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      Object object = super.instantiateItem(container, position);
      if (object instanceof Fragment) {
        Fragment fragment = (Fragment) object;
        String tag = fragment.getTag();
        mFragmentTags.put(position, tag);
      }
      return object;
    }

    @Override
    public Fragment getItem(int position) {
      switch (position) {
        case 0:
          return new ProfileFragment();
        case 1:
          igniterPageFragment = new IgniterPageFragment();
          return igniterPageFragment;
        case 2:
           chat=new ChatFragment();
           return chat;
      }
      return null;
    }

    @Override
    public int getCount() {
      return 3;
    }

    public Fragment getFragment(int position) {
      Fragment fragment = null;
      String tag = mFragmentTags.get(position);
      if (tag != null) {
        fragment = mFragmentManager.findFragmentByTag(tag);
      }
      return fragment;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case UCrop.REQUEST_CROP:
        try {
          imagePath = UCrop.getOutput(data).getPath();
          if (!TextUtils.isEmpty(imagePath)) {
            Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
            int width = mbitmap.getWidth();
            int height = mbitmap.getHeight();
            Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint mpaint = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 25, 25, mpaint);// Round Image Corner 100 100 100 100

            commonMethods.showProgressDialog(HomeActivity.this, customDialog);
            new ImageCompressAsyncTask(HomeActivity.this, imagePath, this, "").execute();

          }
        } catch (OutOfMemoryError | Exception e) {
          e.printStackTrace();
        }
        break;
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
  }


  /**
   * This method is To get the PutExtra value form the notificationUtils Class
   * Because In notificationutils we call HomeActivity--->ChatFragment
   * In Normal Intent We can Satisfy the Condition Because the Activity is Already Opened
   * @param intent latest Intent
   */
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (intent.hasExtra("matchStatus")){
      viewPager.setCurrentItem(2,true);
    }
  }
}
