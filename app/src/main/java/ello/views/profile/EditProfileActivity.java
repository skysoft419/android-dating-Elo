package ello.views.profile;

/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category EditProfileActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.massarttech.android.spannedgridlayoutmanager.SpannedGridLayoutManager;
import com.obs.CustomEditText;
import com.obs.CustomTextView;
import com.obs.image_cropping.CropImage;
import com.obs.image_cropping.CropImageView;
import com.obs.image_cropping.ImageMinimumSizeCalculator;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import ello.BuildConfig;
import ello.R;
import ello.adapters.action.AdapterCallback;
import ello.adapters.profile.EditProfileImageListAdapter;
import ello.backgroundtask.ImageCompressAsyncTask;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.main.EditProfileModel;
import ello.datamodels.main.ImageModel;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ImageListener;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;
import ello.utils.RequestCallback;
import ello.views.action.CropperImageActivity;
import ello.views.customize.CustomDialog;
import ello.views.customize.CustomRecyclerView;
import ello.views.main.IgniterPlusDialogActivity;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.RequestBody;

import static com.facebook.FacebookSdk.getCacheDir;
import static ello.utils.Enums.REQ_GET_EDIT_PROFILE;
import static ello.utils.Enums.REQ_REMOVE_IMAGE;
import static ello.utils.Enums.REQ_UPDATE_PROFILE;
import static ello.utils.Enums.REQ_UPLOAD_PROFILE_IMG;

/*import instagram.InstagramHelper;
import instagram.InstagramHelperConstants;
import instagram.model.InstagramUser;*/

/*****************************************************************
 User edit profile
 ****************************************************************/

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, ServiceListener, CompoundButton.OnCheckedChangeListener, ImageListener,AdapterCallback {

    ImageView updateImageView;
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
    private CustomTextView tvHeader, tvBackArrow, tvConnect, tvInstagram, tvAboutUserName, tvAboutCount,tvAppPlus;
    private CustomEditText edtAbout, edtJobTitle, edtCompany, edtSchool;
    private RelativeLayout rltProfileImageOne, rltProfileImageTwo, rltProfileImageThree, rltProfileImageFour, rltProfileImageFive, rltProfileImageSix;
    private RadioGroup rgGender;
    private RadioButton rbMan, rbWoman;
    private SwitchCompat swHideAge, swHideDistance, swSmartPhotos;
    private CustomRecyclerView rvEditProfileList;
    private EditProfileImageListAdapter imageListAdapter;
    private AlertDialog dialog;
    private EditProfileModel editProfileModel;
    private boolean isDelete = false;
    private String about = "", jobTitle = "", company = "", school = "", gender = "", hideMyAge = "", distanceInvisible = "", imageUrl = "", instagramUserName = "";
    private File imageFile = null;
    private Uri imageUri;
    private String imagePath = "";

    // private InstagramHelper instagramHelper;
    private int clickPos = 1;
    private String userName = "";
    private RelativeLayout hideage, hidedistance;
    private String img;
    private String img_id;
    private ArrayList<String> image_id;
    private boolean onBackPressed = false;
    private RecyclerView recyclerView;
    private String imageId="";
    private  HomeAdapter homeAdapter;
    ArrayList<ImageModel> array=new ArrayList<>();
    private Boolean isDeleting=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        AppController.getAppComponent().inject(this);
        recyclerView=findViewById(R.id.recyclerviewsss);
        image_id = new ArrayList<String>();
        for (ImageModel imgid:StaticData.editProfileModel.getImageList()
             ) {
            image_id.add(imgid.getImageId().toString());
        }
        //instagramHelper = AppController.getInstagramHelper();
        initView();
        editProfileModel= StaticData.editProfileModel;
        array.addAll(editProfileModel.getImageList());
        setAdapter();
    }
    private void setAdapter(){
        SpannedGridLayoutManager spannedGridLayoutManager=
                new SpannedGridLayoutManager(SpannedGridLayoutManager.Orientation.VERTICAL,12);
        homeAdapter=new HomeAdapter(this,array,this);
        recyclerView.setLayoutManager(spannedGridLayoutManager);
        recyclerView.setVisibility(View.VISIBLE);
        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
            //and in your imlpementaion of



            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // get the viewHolder's and target's positions in your adapter data, swap them
                onMoveObject(viewHolder,target);
                homeAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateImages();
                    }
                }, 500);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }


            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG|ItemTouchHelper.ANIMATION_TYPE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.LEFT | ItemTouchHelper.START | ItemTouchHelper.END);
            }
        };

        ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        ith.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(homeAdapter);


    }
    private void updateImages(){
        homeAdapter.notifyDataSetChanged();

    }
    private void onMoveObject(RecyclerView.ViewHolder viewHolder,RecyclerView.ViewHolder target){
        if (target.getAdapterPosition() < array.size() && viewHolder.getAdapterPosition() < array.size() && array.size() > 1) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(array, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(array, i, i - 1);
                }
            }

        }
    }
    private void initView() {
        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvBackArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);

       // swHideAge.setChecked(false);    //newly added
        //swHideDistance.setChecked(false);    //newly added

        dialog = commonMethods.getAlertDialog(this);



        tvHeader.setTextColor(getResources().getColor(R.color.black));
        tvHeader.setText(getString(R.string.header_edit_info));

        tvBackArrow.setOnClickListener(this);

    }


/*
    private void iniTextChangeListener() {
        edtAbout.setCursorVisible(false);

        edtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAbout.setCursorVisible(true);
            }
        });

        edtAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //edtAbout.setCursorVisible(false);
                tvAboutCount.setText(String.valueOf(500 - s.length()));
                tvAboutCount.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
*/




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                updateProfile();
                break;
                /*
            case R.id.hide_age:
                checkPurchase(1);
                break;
            case R.id.hide_distance:
                checkPurchase(0);
                break;
            case R.id.switch_hide_age:
                swHideAge.setChecked(!swHideAge.isChecked());
                checkPurchase(1);
                break;
            case R.id.switch_hide_distance:
                swHideDistance.setChecked(!swHideDistance.isChecked());
                checkPurchase(0);
                break;*/
            default:
                break;
        }
    }

    private HashMap<String, String> getParams() {
        distanceInvisible=StaticData.editProfileModel.getDistanceInvisible();
        hideMyAge=StaticData.editProfileModel.getShowMyAge();
        String imageOrder="";
        boolean first=true;
        StaticData.editProfileModel.getImageList().clear();
        StaticData.editProfileModel.getImageList().addAll(array);
        for (ImageModel model : array ){
            if (first) {
                imageOrder = imageOrder + model.getImageId().toString();
                first=false;
            }else{
                imageOrder = imageOrder + ","+model.getImageId().toString();
            }
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", sessionManager.getToken());
        gender=StaticData.editProfileModel.getGender();
        hashMap.put("gender", gender);
        hashMap.put("order_images",imageOrder);
        hashMap.put("job_title", StaticData.editProfileModel.getJobTitle());
        hashMap.put("distance_invisible", distanceInvisible);
        hashMap.put("show_my_age", hideMyAge);
        hashMap.put("about", StaticData.editProfileModel.getAbout());
        hashMap.put("college", StaticData.editProfileModel.getCollege());
        hashMap.put("work", StaticData.editProfileModel.getWork());
        editProfileModel=StaticData.editProfileModel;
        //hashMap.put("instagram_id", instagramUserName);

        return hashMap;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_man:
                gender = "Men";
                break;
            case R.id.rb_woman:
                gender = "Women";
                break;
            default:
                break;
        }
    }

    public void pickProfileImg(boolean isDelete) {
        this.isDelete = isDelete;
        View view = getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltLibrary = (LinearLayout) view.findViewById(R.id.llt_library);

        final Dialog bottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
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
                imageFile = commonMethods.getDefaultFileName(EditProfileActivity.this);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);
            }
        });
    }



    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_EDIT_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetEditProfile(jsonResp);
                } else {
                }
                break;
            case -1:
                if (jsonResp.isSuccess()) {

                        firstImage(jsonResp);

                } else {
                }
                break;
            case REQ_UPDATE_PROFILE:
                if (jsonResp.isSuccess()) {
                    onBackPressed = true;
                    onBackPressed();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPLOAD_PROFILE_IMG:
                if (jsonResp.isSuccess()) {
                    if (isDelete) {
                        commonMethods.showProgressDialog(this, customDialog);
                        apiService.remove_profile_image(Integer.valueOf(image_id.get(clickPos - 1)), sessionManager.getToken()).enqueue(new RequestCallback(REQ_REMOVE_IMAGE, this));
                    } else {
                        onSuucessImage(jsonResp);
                    }
                } else {
                }
                break;
            case REQ_REMOVE_IMAGE:
                isDeleting=false;
                if (jsonResp.isSuccess()) {
                    onSuucessImage(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }
    private void firstImage(JsonResponse jsonResponse){
        EditProfileModel editProfileModel = gson.fromJson(jsonResponse.getStrResponse(), EditProfileModel.class);
        array.remove(0);
        array.add(0,editProfileModel.getImageList().get(0));
        this.editProfileModel.getImageList().remove(0);
        this.editProfileModel.getImageList().add(0,editProfileModel.getImageList().get(0));
        StaticData.editProfileModel=this.editProfileModel;
        this.imageId="";
        homeAdapter.notifyDataSetChanged();
    }
    private void onSuucessImage(JsonResponse jsonResp){
        getImageId(jsonResp);
        EditProfileModel editProfileModel = gson.fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
        this.editProfileModel.getImageList().clear();
        this.array.clear();
        this.array.addAll(editProfileModel.getImageList());
        ArrayList<ImageModel> ar=array;
        this.editProfileModel.setImageList(editProfileModel.getImageList());
        StaticData.editProfileModel=this.editProfileModel;
        homeAdapter.notifyDataSetChanged();
    }
    public void getImageFromUrl(ImageView imageView) {
        Picasso.get().load(img).transform(new RoundedCornersTransformation(5,1)).fit().into(imageView);
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetEditProfile(JsonResponse jsonResp) {
        getImageId(jsonResp);
        editProfileModel = gson.fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
        StaticData.editProfileModel=editProfileModel;
        if (editProfileModel != null) {

        }
    }

    private void updateProfile() {
        if (StaticData.editProfileModel.getAbout() != null) {
            about = StaticData.editProfileModel.getAbout().trim();
        }
        if (StaticData.editProfileModel.getJobTitle() != null) {
            jobTitle = StaticData.editProfileModel.getJobTitle().trim();
        }
        if (StaticData.editProfileModel.getWork() != null) {
            company = StaticData.editProfileModel.getWork();
        }
        if (StaticData.editProfileModel.getCollege() != null) {
            school = StaticData.editProfileModel.getCollege();
        }

        commonMethods.showProgressDialog(this, customDialog);
        apiService.updateProfile(getParams()).enqueue(new RequestCallback(REQ_UPDATE_PROFILE, this));
    }

    @Override
    public void onBackPressed() {
        if (onBackPressed)
            super.onBackPressed();
        else {
            updateProfile();
        }
    }


    public void getImageId(JsonResponse jsonResp) {
        image_id.clear();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResp.getStrResponse());
            JSONArray array = jsonObject.getJSONArray("image_url");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                img_id = object.getString("image_id");
                image_id.add(img_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_hide_age:
                hideMyAge = isChecked ? "No" : "Yes";
                System.out.println("Hide My Age " + hideMyAge);
                break;
            case R.id.switch_hide_distance:
                distanceInvisible = isChecked ? "No" : "Yes";
                System.out.println("Hide My distance " + distanceInvisible);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    startCropImage();
                    break;
                case Constants.REQUEST_CODE_GALLERY:
                    try {
                        imageUri = data.getData();
                        InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
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
                    getImageUri(StaticData.croppedImage);
                    if (!TextUtils.isEmpty(imagePath)) {
                        Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
                        int width=mbitmap.getWidth();
                        int height=mbitmap.getHeight();
                        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                        Canvas canvas = new Canvas(imageRounded);
                        Paint mpaint = new Paint();
                        mpaint.setAntiAlias(true);
                        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 25, 25, mpaint);// Round Image Corner 100 100 100 100

                        commonMethods.showProgressDialog(this, customDialog);
                        if (imageId.equals("")) {
                            new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, "").execute();
                        }else{
                            new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, imageId).execute();
                        }
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        imagePath = result.getUri().getPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
                            int width=mbitmap.getWidth();
                            int height=mbitmap.getHeight();
                            Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                            Canvas canvas = new Canvas(imageRounded);
                            Paint mpaint = new Paint();
                            mpaint.setAntiAlias(true);
                            mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                            canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 25, 25, mpaint);// Round Image Corner 100 100 100 100

                            commonMethods.showProgressDialog(this, customDialog);
                            if (imageId.equals("")) {
                                new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, "").execute();
                            }else{
                                new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, imageId).execute();
                            }
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

                            commonMethods.showProgressDialog(this, customDialog);
                            if (imageId.equals("")) {
                                new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, "").execute();
                            } else {
                                int img = Integer.parseInt(imageId);
                                if (img > 0) {
                                    new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, imageId).execute();
                                } else {
                                    new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, "").execute();
                                }
                            }
                        }
                    }catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
               /* case InstagramHelperConstants.INSTA_LOGIN:
                    InstagramUser user = instagramHelper.getInstagramUser(this);
                    if (user != null && user.getData() != null && !TextUtils.isEmpty(user.getData().getUsername())) {
                        tvInstagram.setText(user.getData().getUsername());
                        tvConnect.setText(getString(R.string.disconnect));
                        instagramUserName = user.getData().getUsername();
                    }
                    break;*/
                default:
                    break;
            }
        }
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
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);
        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "sample.jpg")))
                .withAspectRatio(4, 6)
                .withOptions(options)
                .start(this);

    }
    public void getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        imagePath=getRealPathFromURI(Uri.parse(path));
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(filePath) && requestBody != null) {
            commonMethods.showProgressDialog(this, customDialog);
            if (imageId.equals("")) {
                apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));
            } else {
                int imgId = Integer.parseInt(imageId);
                if (imgId > 0) {
                    apiService.updateImage(requestBody).enqueue(new RequestCallback(-1, this));
                } else {
                    apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));
                }
            }
        }
    }

    public void checkPurchase(int type) {
        if (sessionManager.getIsOrder()) {
            if (type == 0) {
                swHideDistance.setChecked(!swHideDistance.isChecked());
            } else {
                swHideAge.setChecked(!swHideAge.isChecked());
            }
        } else {
            if (type == 0) {
                swHideDistance.setChecked(false);
            } else {
                swHideAge.setChecked(false);
            }

            Intent intent = new Intent(EditProfileActivity.this, IgniterPlusDialogActivity.class);
            intent.putExtra("startwith", "");
            intent.putExtra("type", "plus");
            startActivity(intent);
        }
    }

    protected void onResume() {
        super.onResume();
        /*
        if (sessionManager.getIsOrder()) {
            swHideAge.setClickable(true);
            swHideDistance.setClickable(true);
        } else {
            swHideAge.setClickable(false);
            swHideDistance.setClickable(false);
        }*/
    }

    private void checkAllPermission(String[] permission, boolean isDelete) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(this, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, permission, 300);
            }
        } else {
            pickProfileImg(isDelete);
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
            checkAllPermission(dsf, isDelete);
        } else {
            pickProfileImg(isDelete);
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
            customDialog.show(getSupportFragmentManager(), "");
        }
    }

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }

    @Override
    public void onClick(int position) {
        if (isDeleting == false) {
            if (position == 0) {
                isDelete = false;
                this.imageId = editProfileModel.getImageList().get(0).getImageId().toString();
                checkAllPermission(Constants.PERMISSIONS_PHOTO, isDelete);
            } else if (position == -1) {
                isDelete = false;
                checkAllPermission(Constants.PERMISSIONS_PHOTO, isDelete);
            } else {
                showDeleteDialog(position);
            }
        }
    }
     private void showDeleteDialog(final int index){
        customDialog= new CustomDialog(index, "", "Are you sure to delete image?", "Delete", "Cancel", new CustomDialog.btnAllowClick() {
            @Override
            public void clicked() {
                deleteImage(index);
            }
        }, new CustomDialog.btnDenyClick() {
            @Override
            public void clicked() {

            }
        });
        customDialog.show(getSupportFragmentManager(),"");
    }
    private void deleteImage(int index){
        if (isDeleting==false) {
            isDeleting=true;
            apiService.remove_profile_image(editProfileModel.getImageList().get(index).getImageId(), sessionManager.getToken()).enqueue(new RequestCallback(REQ_REMOVE_IMAGE, this));
        }
    }
}
