package ello.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category ProfilePickFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;
import com.obs.image_cropping.CropImage;
import com.obs.image_cropping.CropImageView;
import com.obs.image_cropping.ImageMinimumSizeCalculator;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import ello.BuildConfig;
import ello.R;
import ello.backgroundtask.ImageCompressAsyncTask;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.SettingsModel;
import ello.datamodels.main.SignUpModel;
import ello.helpers.AllUserData;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ImageListener;
import ello.interfaces.ServiceListener;
import ello.interfaces.SignUpActivityListener;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.action.CropperImageActivity;
import ello.views.customize.CustomDialog;
import ello.views.main.HomeActivity;
import ello.views.main.LoginActivity;
import ello.views.main.SplashActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.facebook.FacebookSdk.getCacheDir;
import static ello.utils.Enums.REQ_NUMBER_SIGNUP;
import static ello.utils.Enums.REQ_UPLOAD_PROFILE_IMG;

/*****************************************************************
 Get signup user profile image page
 ****************************************************************/

public class ProfilePickFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, ServiceListener, ImageListener {

    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    @Inject
    CustomDialog customDialog;
    @Inject
    Gson gson;
    @Inject
    RunTimePermission runTimePermission;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CustomTextView tvBackArrow, tvAddIcon;
    private CustomButton btnContinue;
    private RelativeLayout rltProfilePick;
    private ImageView ivProfileImage, ivProfieImage1;
    private CardView cvProfileImage;
    private File imageFile = null;
    private Uri imageUri;
    private String imagePath = "";
    private AlertDialog dialog;
    private boolean isPermissionGranted = false;
    private RequestBody profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.profile_pick_fragment, container, false);
            initView();
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void initView() {
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        btnContinue.setEnabled(false);
        btnContinue.setBackgroundResource(R.drawable.oval_btn_gray);
        btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
        cvProfileImage = (CardView) view.findViewById(R.id.cv_profile_image);
        ivProfileImage = (ImageView) view.findViewById(R.id.iv_profile_image);
        rltProfilePick = (RelativeLayout) view.findViewById(R.id.rlt_profile_pick);

        dialog = commonMethods.getAlertDialog(mActivity);

        tvBackArrow.setOnClickListener(this);
        cvProfileImage.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        rltProfilePick.setOnClickListener(this);
    }

    private void init() {
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (SignUpActivity) getActivity();
        AppController.getAppComponent().inject(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                mActivity.onBackPressed();
                break;
            case R.id.btn_continue:
                if (!TextUtils.isEmpty(imagePath)) {
                    commonMethods.showProgressDialog(mActivity, customDialog);
                    try {
                        apiService.phoneNumberSignUp(getSignUpParam(imagePath)).enqueue(new RequestCallback(REQ_NUMBER_SIGNUP, this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    commonMethods.showMessage(mActivity, dialog, res.getString(R.string.profile_pick_alert));
                }
                break;
            case R.id.cv_profile_image:
            case R.id.rlt_profile_pick:
                //pickProfileImg();
                checkAllPermission(Constants.PERMISSIONS_PHOTO);
                break;
            default:
                break;
        }
    }

    public RequestBody getSignUpParam(String imagePath) throws IOException {
        mActivity.putHashMap("verification_code", "0");
        mActivity.putHashMap("password", "6555465446");
        mActivity.putHashMap("phone_number", sessionManager.getPhoneNumber());
        mActivity.putHashMap("country_code", sessionManager.getCountryCode());
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        File file = null;
        try {
            file = new File(imagePath);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            multipartBody.addFormDataPart("image", "IMG_" + timeStamp + ".jpg",
                    RequestBody.create(MediaType.parse("image/jpeg"), file));
            for (Map.Entry<String, String> entry : mActivity.getHashMap().entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                    multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody formBody = multipartBody.build();
        return formBody;
    }

    public void pickProfileImg() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltLibrary = (LinearLayout) view.findViewById(R.id.llt_library);

        final Dialog bottomSheetDialog = new Dialog(mActivity, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        if (bottomSheetDialog.getWindow() == null) return;
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();
        lltLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                imageFile = commonMethods.getDefaultFileName(mActivity);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SignUpActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + Constants.listenerSignUpException);
        }
    }

    @Override
    public void onDetach() {
        if (listener != null) listener = null;
        super.onDetach();
    }

    /**
     * onCreateAnimation is used to perform the animation while sliding or
     * automatic Slideshow in the image gallery.
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (Constants.isDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

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
                case 576:
                    ivProfileImage.setImageBitmap(StaticData.croppedImage);
                    ivProfileImage.setVisibility(View.VISIBLE);
                    cvProfileImage.setVisibility(View.VISIBLE);
                    rltProfilePick.setVisibility(View.GONE);
                    btnContinue.setEnabled(true);
                    btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                    getImageUri(StaticData.croppedImage);
                    break;
                case Constants.REQUEST_CODE_GALLERY:
                    try {
                        imageUri=data.getData();
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        if (inputStream != null) inputStream.close();
                        startCropImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        imageUri=result.getUri();
                        imagePath = result.getUri().getPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                           /* Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
                            Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                            Canvas canvas = new Canvas(imageRounded);
                            Paint mpaint = new Paint();
                            mpaint.setAntiAlias(true);
                            mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                            canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 30, 30, mpaint);// Round Image Corner 100 100 100 100
                            //mimageView.setImageBitmap(imageRounded);
                            ivProfileImage.setImageBitmap(imageRounded);*/
                            ivProfileImage.setImageBitmap(bitmap);
                            ivProfileImage.setVisibility(View.VISIBLE);
                            cvProfileImage.setVisibility(View.VISIBLE);
                            rltProfilePick.setVisibility(View.GONE);
                            btnContinue.setEnabled(true);
                            btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                        }
                    } catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
                case UCrop.REQUEST_CROP:
                    try {
                        imagePath = UCrop.getOutput(data).getPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                           /* Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
                            Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                            Canvas canvas = new Canvas(imageRounded);
                            Paint mpaint = new Paint();
                            mpaint.setAntiAlias(true);
                            mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                            canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 30, 30, mpaint);// Round Image Corner 100 100 100 100
                            //mimageView.setImageBitmap(imageRounded);
                            ivProfileImage.setImageBitmap(imageRounded);*/
                            ivProfileImage.setImageBitmap(bitmap);
                            ivProfileImage.setVisibility(View.VISIBLE);
                            cvProfileImage.setVisibility(View.VISIBLE);
                            rltProfilePick.setVisibility(View.GONE);
                            btnContinue.setEnabled(true);
                            btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                        }
                    } catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
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
    private void copyStream(InputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private void startCropImage() {
        if (imageFile == null) return;
        /*
        CropImage.activity(Uri.fromFile(imageFile))
                .setDefaultlyCropEnabled(true)
                .setAspectRatio(4, 6)
                .setOutputCompressQuality(100)
                .setAllowRotation(false)
                .setAllowFlipping(false)
                .setFixAspectRatio(true)
                .setMultiTouchEnabled(false)
                //.setRequestedSize(minimumSquareDimen[0],minimumSquareDimen[1], CropImageView.RequestSizeOptions.RESIZE_FIT)
                //.setMinCropWindowSize(200,200)

                .start(getActivity());*/
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "sample.jpg")))
                .withAspectRatio(4, 6)
                .withOptions(options)
                .start(getActivity());

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        if (!jsonResp.isOnline()) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        if (jsonResp.getRequestCode() == REQ_NUMBER_SIGNUP && jsonResp.isSuccess()) {
            onSuccessNumberSignUp(jsonResp);
        }else if (jsonResp.getRequestCode() ==  REQ_UPLOAD_PROFILE_IMG && jsonResp.isSuccess()){
            sessionManager.setFirstTime("Yes");
            commonMethods.hideProgressDialog();
            EditProfileModel editProfileModel = gson.fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
            sessionManager.setProfileImg(editProfileModel.getImageList().get(0).getImageUrl());
            getData();
        }else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
        }
    }
    private void getData(){
        apiService.getUserData(sessionManager.getToken()).enqueue(new RequestCallback(1, new ServiceListener() {
            @Override
            public void onSuccess(JsonResponse jsonResp, String data) {
                if(jsonResp.isOnline()){
                    if (jsonResp.isSuccess()){
                        sessionManager.setUserData(jsonResp.getStrResponse());
                        AllUserData allUserData=new Gson().fromJson(jsonResp.getStrResponse(),AllUserData.class);
                        assignData(allUserData);
                    }
                }
            }

            @Override
            public void onFailure(JsonResponse jsonResp, String data) {

            }
        }));
    }
    private void assignData(AllUserData userData){
        StaticData.externalLinks=userData.getLinks();
        StaticData.unReadNewMatch=userData.getNewMatchCount();
        StaticData.unReadCount=userData.getUnReadCount();
        SettingsModel settingsModel=new SettingsModel();
        settingsModel.setAccessToken(userData.getAccessToken());
        settingsModel.setAdminDistanceType(userData.getAdminDistanceType());
        settingsModel.setCommunityUrl(userData.getCommunityUrl());
        settingsModel.setCountryCode(userData.getCountryCode());
        settingsModel.setPhoneNumber(userData.getPhoneNumber());
        settingsModel.setEmail(userData.getEmail());
        settingsModel.setPin(userData.getPin());
        settingsModel.setVerifyEmail(userData.isVerifyEmail());
        settingsModel.setDistanceType(userData.getDistanceType());
        settingsModel.setHelpUrl(userData.getHelpUrl());
        settingsModel.setIsOrder(userData.getIsOrder());
        settingsModel.setLicenseUrl(userData.getLicenseUrl());
        settingsModel.setCity(userData.getCity());
        settingsModel.setMinimumAge(userData.getMinimumAge());
        settingsModel.setMaximumAge(userData.getMaximumAge());
        settingsModel.setMinAge(userData.getMinAge());
        settingsModel.setMaxAge(userData.getMaxAge());
        settingsModel.setMaxDistance(userData.getMaxDistance());
        settingsModel.setMaximumDistance(userData.getMaximumDistance());
        settingsModel.setMinimumDistance(userData.getMinimumDistance());
        settingsModel.setShowMe(userData.getShowMe());
        settingsModel.setMatchingProfile(userData.getMatchingProfile());
        settingsModel.setUserName(userData.getUserName());
        settingsModel.setUserId(userData.getUserId());
        settingsModel.setNewMatch(userData.getNewMatch());
        settingsModel.setReceivingMessage(userData.getReceivingMessage());
        settingsModel.setMessageLikes(userData.getMessageLikes());
        settingsModel.setSuperLikes(userData.getSuperLikes());
        settingsModel.setPrivacyPolicyUrl(userData.getPrivacyPolicyUrl());
        settingsModel.setTermsOfServiceUrl(userData.getTermsOfServiceUrl());
        settingsModel.setSafetyUrl(userData.getSafetyUrl());
        settingsModel.setPlanType(userData.getPlanType());
        settingsModel.setSearchLocation(userData.getSearchLocation());
        settingsModel.setLocationModels(userData.getLocationModels());
        EditProfileModel editProfileModel=new EditProfileModel();
        editProfileModel.setAccessToken(userData.getAccessToken());
        editProfileModel.setUserId(userData.getUserId());
        editProfileModel.setImageList(userData.getImageList());
        editProfileModel.setAbout(userData.getAbout());
        editProfileModel.setJobTitle(userData.getJobTitle());
        editProfileModel.setWork(userData.getWork());
        editProfileModel.setCollege(userData.getCollege());
        editProfileModel.setInstagramId(userData.getInstagramId());
        editProfileModel.setShowMyAge(userData.getShowMyAge());
        editProfileModel.setGender(userData.getGender());
        editProfileModel.setDistanceInvisible(userData.getDistanceInvisible());
        editProfileModel.setIsOrder(userData.getIsOrder());
        editProfileModel.setImage_id(userData.getImage_id());
        editProfileModel.setPlanType(userData.getPlanType());
        editProfileModel.setRemainingLikes(userData.getRemainingLikes());

        StaticData.matchedProfileModel.setMessage(userData.getChat());
        StaticData.matchedProfileModel.setNewMatchProfile(userData.getNewMatchProfile());
        StaticData.settingsModel=settingsModel;
        StaticData.editProfileModel=editProfileModel;
        StaticData.PIN=settingsModel.getPin();
        StaticData.unmatch=userData.getUnmatchReasons();
        StaticData.delete=userData.getDeleteReasons();
        StaticData.report=userData.getReportReasons();
        Intent intent=new Intent(getContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);






    }
    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessNumberSignUp(JsonResponse jsonResp) {
        SignUpModel signUpModel = gson.fromJson(jsonResp.getStrResponse(), SignUpModel.class);
        if (signUpModel != null) {
            if (!TextUtils.isEmpty(signUpModel.getAccessToken())) {
                sessionManager.setToken(signUpModel.getAccessToken());
            }
            if (!TextUtils.isEmpty(signUpModel.getUserImageUrl())) {
                sessionManager.setProfileImg(signUpModel.getUserImageUrl());
            }
            //sessionManager.setToken(signUpModel.getAccessToken());
            sessionManager.setUserId(signUpModel.getUserId());
            String token=sessionManager.getToken();
            new ImageCompressAsyncTask(mActivity, imagePath, this, "").execute();


        }
    }

    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        profileImage=requestBody;

        if (!TextUtils.isEmpty(filePath) && requestBody != null) {

                apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));

        }
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
            pickProfileImg();
            //checkGpsEnable();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        } else {
            pickProfileImg();
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

}
