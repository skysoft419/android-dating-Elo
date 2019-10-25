package ello.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category ProfileFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;
import com.obs.image_cropping.CropImage;
import com.squareup.picasso.Picasso;
import com.subinkrishna.widget.CircularImageView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.adapters.profile.IgniterSliderAdapter;
import ello.backgroundtask.ImageCompressAsyncTask;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.MyProfileModel;
import ello.helpers.StaticData;
import ello.interfaces.ActivityListener;
import ello.interfaces.ApiService;
import ello.interfaces.ImageListener;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;
import ello.utils.RequestCallback;
import ello.views.action.CropperImageActivity;
import ello.views.customize.CirclePageIndicator;
import ello.views.customize.CustomDialog;
import ello.views.customize.IgniterViewPager;
import ello.views.main.HomeActivity;
import ello.views.main.IgniterPlusDialogActivity;
import okhttp3.RequestBody;

import static ello.utils.Enums.REQ_GET_MY_PROFILE;
import static ello.utils.Enums.REQ_UPDATE_PROFILE;
import static ello.utils.Enums.REQ_UPLOAD_PROFILE_IMG;

/*****************************************************************
 User home profile page (Contain settings and editprofile)
 ****************************************************************/
public class ProfileFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, ServiceListener, ImageListener {
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
    RunTimePermission runTimePermission;
    @Inject
    ImageUtils imageUtils;
    private View view;
    private ActivityListener listener;
    private Resources res;
    private HomeActivity mActivity;
    private CircularImageView civUserImage;
    private CustomTextView tvUserNameAge, tvUserJob, tvUserSchool, tvUserSearchLocation, tvSuperLikesCount, tvBoostCount;
    private CustomButton btnIgniterPlus;
    private LinearLayout lltSettings, lltEditProfile, lltRemainingCount, lltSuperLike, lltBoost, lltUpgrade;
    private RelativeLayout rltViewPager;
    private IgniterViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    private boolean isSwiping = false;
    private AlertDialog dialog;
    private Timer swipeTimer = new Timer();
    private MyProfileModel myProfileModel;
    private boolean isFirst = true;
    private ImageView plusBtn;
    private File imageFile = null;
    private String imagePath = "";
    private Uri imageUri;

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
        setupAutoSwiping();
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
            view = inflater.inflate(R.layout.profile_fragment, container, false);
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        lltSettings = (LinearLayout) view.findViewById(R.id.llt_settings);
        lltEditProfile = (LinearLayout) view.findViewById(R.id.llt_edit_profile);
        lltRemainingCount = (LinearLayout) view.findViewById(R.id.llt_remaining_count);
        lltSuperLike = (LinearLayout) view.findViewById(R.id.llt_super_like);
        lltBoost = (LinearLayout) view.findViewById(R.id.llt_boost);
        lltUpgrade = (LinearLayout) view.findViewById(R.id.llt_upgrade);
        plusBtn=view.findViewById(R.id.plusBtn);
        rltViewPager = (RelativeLayout) view.findViewById(R.id.rlt_view_pager);

        tvSuperLikesCount = (CustomTextView) view.findViewById(R.id.tv_super_likes_count);
        tvBoostCount = (CustomTextView) view.findViewById(R.id.tv_boost_count);
        tvUserSearchLocation = (CustomTextView) view.findViewById(R.id.tv_user_search_location);
        tvUserNameAge = (CustomTextView) view.findViewById(R.id.tv_user_name_age);
        tvUserJob = (CustomTextView) view.findViewById(R.id.tv_user_job);
        tvUserSchool = (CustomTextView) view.findViewById(R.id.tv_user_school);

        civUserImage =view.findViewById(R.id.civ_profile_image);
        btnIgniterPlus = (CustomButton) view.findViewById(R.id.btn_igniter_plus);

        dialog = commonMethods.getAlertDialog(mActivity);

        viewPager = (IgniterViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new IgniterSliderAdapter(mActivity));

        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager, 0);


        initListeners();
        initPageIndicator();
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllPermission(Constants.PERMISSIONS_PHOTO);
            }
        });

        imageFile = commonMethods.getDefaultFileName(mActivity);

    }
    private void startCropImage() {
        if (imageFile == null) return;
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        UCrop.of(imageUri, Uri.fromFile(new File(getActivity().getCacheDir(), "sample.jpg")))
                .withAspectRatio(4, 6)
                .withOptions(options)
                .start(mActivity);

    }
    public void getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        imagePath=getRealPathFromURI(Uri.parse(path));
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private void getProfileDetails() {
        apiService.getMyProfileDetail(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_MY_PROFILE, this));
    }
    private void copyStream(InputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
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

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivity.getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }
    private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(mActivity, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(mActivity, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(mActivity, permission, 300);
            }
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);;

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 300:
                    checkAllPermission(Constants.PERMISSIONS_PHOTO);
                    break;
                case 1:
                    startCropImage();
                    break;
                case Constants.REQUEST_CODE_GALLERY:
                    try {
                        imageUri = data.getData();
                        InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        if (inputStream != null) inputStream.close();
                        startCropImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 576:
                    int sizeH=StaticData.croppedImage.getHeight();
                    int sizeW=StaticData.croppedImage.getWidth();
                    getImageUri(StaticData.croppedImage);
                    new ImageCompressAsyncTask(mActivity, imagePath, this, "").execute();
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        imagePath = result.getUri().getPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                            int width=bitmap.getWidth();
                            int height=bitmap.getHeight();
                            new ImageCompressAsyncTask(mActivity, imagePath, this, "").execute();

                        }
                    } catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
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

                            commonMethods.showProgressDialog(mActivity, customDialog);
                            new ImageCompressAsyncTask(mActivity, imagePath, this, "").execute();

                        }
                    }catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initListeners() {

        viewPager.addOnPageChangeListener(this);
        lltSettings.setOnClickListener(this);
        lltEditProfile.setOnClickListener(this);
        btnIgniterPlus.setOnClickListener(this);
        civUserImage.setOnClickListener(this);

        lltUpgrade.setOnClickListener(this);
        lltSuperLike.setOnClickListener(this);
        lltBoost.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getProfileDetails();
    }

    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(3 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(mActivity, R.color.gray_text_color));
        pageIndicator.setStrokeColor(ContextCompat.getColor(mActivity, R.color.gray_text_color));
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing((float) (1.5 * density));
        updatePageIndicator(0);
    }

    private void setupAutoSwiping() {
        final Handler handler = new Handler();
        final Runnable updateSlider = new Runnable() {
            public void run() {
                if (myProfileModel != null) {
                    if (isFirst && (myProfileModel.getIsOrder() != null || myProfileModel.getIsOrder().equalsIgnoreCase("yes"))) {
                        isFirst = false;
                        if (myProfileModel.getPlanType().equalsIgnoreCase("Gold"))
                            viewPager.setCurrentItem(0);
                        else
                            viewPager.setCurrentItem(1);
                    } else {
                        int currentPosition = viewPager.getCurrentItem() + 1;
                        if (currentPosition == 6) {
                            viewPager.setAdapter(new IgniterSliderAdapter(mActivity));
                            updatePageIndicator(0);
                        } else {
                            viewPager.setCurrentItem(currentPosition);
                        }
                    }
                } else {
                    int currentPosition = viewPager.getCurrentItem() + 1;
                    if (currentPosition == 6) {
                        viewPager.setAdapter(new IgniterSliderAdapter(mActivity));
                        updatePageIndicator(0);
                    } else {
                        viewPager.setCurrentItem(currentPosition);
                    }
                }

            }
        };

        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isSwiping) {
                    handler.post(updateSlider);
                }
            }
        }, 3000, 3000);

    }

    /**
     * Method called for make circle page indicator setup.
     */
    private void updatePageIndicator(int position) {
        if (myProfileModel != null) {
            if (myProfileModel.getIsOrder() != null && myProfileModel.getIsOrder().equalsIgnoreCase("yes")) {
                if (myProfileModel.getPlanType().equalsIgnoreCase("Gold")) {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_gold));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
                } else {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                }
            } else {
                if (position == 0) {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.get_gold));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
                } else {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                }
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
            }
        } else {
            if (position == 0) {
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.get_gold));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
            } else {
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
            }
            btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
            btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
        }

        switch (position) {
            /*case 0:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_yellow));
            break;*/
            case 0:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_violet));
                break;
            case 1:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_blue));
                break;
            case 2:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.login_fb_bg));
                break;
            case 3:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.choose_gradient_end));
                break;
            case 4:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_yellow));
                break;
            case 5:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_green));
                break;
            default:
                break;
        }
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
        swipeTimer.cancel();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updatePageIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.civ_profile_image:
                intent = new Intent(mActivity, EnlargeProfileActivity.class);
                intent.putExtra("navType", 0);
                intent.putExtra("userId", myProfileModel.getUserId());
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);

                break;
            case R.id.llt_settings:
                intent = new Intent(mActivity, SettingsActivity.class);
                if (myProfileModel != null)
                    intent.putExtra("matching_profile", myProfileModel.getMatchingProfile());
                else
                    intent.putExtra("matching_profile", "");
                startActivity(intent);
                break;
            case R.id.llt_upgrade:
                callIntent("gold");
                break;
            case R.id.llt_super_like:
                callIntent("super_like");
                break;
            case R.id.llt_boost:
                callIntent("boost");
                break;
            case R.id.llt_edit_profile:
                if (myProfileModel != null && !TextUtils.isEmpty(myProfileModel.getName())) {
                    intent = new Intent(mActivity, EditProfileActivity.class);
                    intent.putExtra("userName", myProfileModel.getName());
                    startActivity(intent);
                }
                break;
            case R.id.btn_igniter_plus:
                if (btnIgniterPlus.getText().toString().equals(mActivity.getResources().getString(R.string.get_gold))) {
                    callIntent("gold");
                }
                if (btnIgniterPlus.getText().toString().equals(mActivity.getResources().getString(R.string.my_igniter_plus))) {
                    //callIntent("plus");
                    intent = new Intent(mActivity, GetIgniterPlusActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(mActivity, GetIgniterPlusActivity.class);
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_UPDATE_PROFILE:
                break;
            case REQ_UPLOAD_PROFILE_IMG:
                EditProfileModel editProfileModel = gson.fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
                StaticData.editProfileModel.getImageList().clear();
                StaticData.editProfileModel.setImageList(editProfileModel.getImageList());
                break;
            case REQ_GET_MY_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetMyProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    /*if(jsonResp.getStatusMsg().equalsIgnoreCase("Token Expired")){
                        getProfileDetails();
                    }else {*/
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                    //}
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        //if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessGetMyProfile(JsonResponse jsonResp) {
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
            sb.append(", ");
            sb.append(myProfileModel.getAge());
            tvUserNameAge.setText(sb.toString());
            sessionManager.setUserName(myProfileModel.getName());
        }

        if (!TextUtils.isEmpty(myProfileModel.getSearchLocation()) && !myProfileModel.getSearchLocation().equalsIgnoreCase(mActivity.getResources().getString(R.string.current_location))) {
            tvUserSearchLocation.setText(myProfileModel.getSearchLocation());
            tvUserSearchLocation.setVisibility(View.VISIBLE);
        } else {
            tvUserSearchLocation.setText(myProfileModel.getSearchLocation());
            tvUserSearchLocation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getJobTitle()) && !TextUtils.isEmpty(myProfileModel.getWork())) {

            tvUserJob.setText(myProfileModel.getJobTitle() + " at " + myProfileModel.getWork());
        } else if (TextUtils.isEmpty(myProfileModel.getJobTitle())) {
            tvUserJob.setText(myProfileModel.getWork());
        } else if (TextUtils.isEmpty(myProfileModel.getWork())) {
            tvUserJob.setText(myProfileModel.getJobTitle());
        }
        if (!TextUtils.isEmpty(myProfileModel.getCollege())) {
            tvUserSchool.setText(myProfileModel.getCollege());
        } else {
            tvUserSchool.setText("");
        }
        if (myProfileModel.getImages() != null && myProfileModel.getImages().size() > 0 && !TextUtils.isEmpty(myProfileModel.getImages().get(0))) {
            //imageUtils.loadCircleImage(mActivity, civUserImage, myProfileModel.getImages().get(0));
            Picasso.get().load(myProfileModel.getImages().get(0)).fit().centerCrop()
                .noPlaceholder().into(civUserImage);
            sessionManager.setProfileImg(myProfileModel.getImages().get(0));
        }

        if (myProfileModel.getIsOrder() != null && myProfileModel.getIsOrder().equalsIgnoreCase("yes")) {
            rltViewPager.setVisibility(View.GONE);
            lltRemainingCount.setVisibility(View.VISIBLE);
            if (myProfileModel.getPlanType().equalsIgnoreCase("Gold")) {
                lltUpgrade.setVisibility(View.GONE);
            }
        } else {
            lltRemainingCount.setVisibility(View.GONE);
            rltViewPager.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(myProfileModel.getRemainingLikesCount())) {
            sessionManager.setRemainingSuperLikes(Integer.parseInt(myProfileModel.getRemainingLikesCount()));
            tvSuperLikesCount.setText(myProfileModel.getRemainingLikesCount());
        }

        if (!TextUtils.isEmpty(myProfileModel.getRemainingBoostCount())) {
            tvBoostCount.setText(myProfileModel.getRemainingBoostCount());
            sessionManager.setRemainingBoost(Integer.parseInt(myProfileModel.getRemainingBoostCount()));
        }

       /* if(!TextUtils.isEmpty(myProfileModel.getRemainingLike())) {
            sessionManager.setRemainingLikes(Integer.parseInt(myProfileModel.getRemainingLike()));
        }

        if(!TextUtils.isEmpty(myProfileModel.getIsLikesLimited())) {
            sessionManager.setIsRemainingLikeLimited(myProfileModel.getIsLikesLimited());
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null) {
            ((HomeActivity)getActivity()).setViewPagerFront(false);
            ((HomeActivity)getActivity()).changeDirection("all");
            getProfileDetails();
            hideKeyboard(getContext());

                if (StaticData.SETTING == 1){
                    Intent intent = new Intent(mActivity, SettingsActivity.class);
                    if (myProfileModel != null)
                        intent.putExtra("matching_profile", myProfileModel.getMatchingProfile());
                    else
                        intent.putExtra("matching_profile", "");
                    startActivity(intent);
                    StaticData.SETTING=0;
                }

        }

       /* if (isVisibleToUser && view != null) {
            // getMatchedProfile();
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/
    }

    private void callIntent(String type) {
        Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
        intent.putExtra("startwith", "");
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {

            apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));



    }
}
