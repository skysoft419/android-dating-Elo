package ello.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category SignUpActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.RunTimePermission;
import ello.configs.SessionManager;
import ello.datamodels.main.JsonResponse;
import ello.datamodels.main.SignUpModel;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.interfaces.SignUpActivityListener;
import ello.utils.CommonMethods;
import ello.utils.Enums;
import ello.utils.RequestCallback;
import ello.views.customize.CustomDialog;
import ello.views.main.HomeActivity;

import static ello.utils.Enums.BIRTHDAY;
import static ello.utils.Enums.EMAIL;
import static ello.utils.Enums.FIRST_NAME;
import static ello.utils.Enums.GENDER;
import static ello.utils.Enums.LAST_NAME;
import static ello.utils.Enums.MY_PHONE_NUMBER;
import static ello.utils.Enums.ONE_TIME_PWD;
import static ello.utils.Enums.PASSWORD;
import static ello.utils.Enums.PROFILE_PICK;
import static ello.utils.Enums.RESEND_CODE;

/*****************************************************************
 Signup home page contain all signup fragment page
 ****************************************************************/
public class SignUpActivity extends AppCompatActivity implements SignUpActivityListener {

    @Enums.Frag
    String currentFrag = EMAIL;
    @Inject
    RunTimePermission runTimePermission;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    int min, max;
    HashMap<String, String> hashMap;
    private ProfilePickFragment profilePickFragment = null;
    private PhoneNumberFragment phoneNumberFragment = null;
    private OneTimePwdFragment oneTimePwdFragment = null;
    private HashMap<String, String> signUp = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        AppController.getAppComponent().inject(this);

        Intent intent = getIntent();
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
        if (hashMap != null)
            Log.v("HashMapTest", hashMap.get("fb_id"));

        min = Integer.parseInt(sessionManager.getMinAge());
        max = Integer.parseInt(sessionManager.getMaxAge());
        changeFragment(EMAIL, null, false);

    }
    /**
     * changeFragment method is used to change the fragment.
     *
     * @param currentFrag  which is represent calling fragment.
     * @param bundle       which contains arguments.
     * @param isReplaceAll which represent need to refresh or not.
     */
    public void changeFragment(@Enums.Frag final String currentFrag, Bundle bundle, boolean isReplaceAll) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (currentFrag) {
            case MY_PHONE_NUMBER:
                phoneNumberFragment = new PhoneNumberFragment();
                phoneNumberFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, MY_PHONE_NUMBER, phoneNumberFragment);
                break;
            case ONE_TIME_PWD:
                oneTimePwdFragment = new OneTimePwdFragment();
                if (hashMap != null) {
                    if (bundle != null) {
                        bundle.putSerializable("map", hashMap);
                    } else {
                        Bundle bundles = new Bundle();
                        bundles.putSerializable("map", hashMap);
                        bundle = bundles;
                    }
                }
                oneTimePwdFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, ONE_TIME_PWD, oneTimePwdFragment);
                break;
            case RESEND_CODE:
                ResendCodeFragment resendCodeFragment = new ResendCodeFragment();
                resendCodeFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, RESEND_CODE, resendCodeFragment);
                break;
            case FIRST_NAME:
                FirstNameFragment firstNameFragment = new FirstNameFragment();
                firstNameFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, FIRST_NAME, firstNameFragment);
                break;
            case LAST_NAME:
                LastNameFragment lastNameFragment = new LastNameFragment();
                lastNameFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, LAST_NAME, lastNameFragment);
                break;
            case GENDER:
                GenderFragment genderFragment = new GenderFragment();
                genderFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, GENDER, genderFragment);
                break;
            case BIRTHDAY:
                BirthdayFragment birthdayFragment = new BirthdayFragment();
                if (bundle != null) {
                    bundle.putInt("min", min);
                    bundle.putInt("max", max);
                } else {
                    Bundle bundles = new Bundle();
                    bundles.putInt("min", min);
                    bundles.putInt("max", max);
                    bundle = bundles;
                }
                birthdayFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, BIRTHDAY, birthdayFragment);
                break;
            case EMAIL:
                EmailFragment emailFragment = new EmailFragment();
                emailFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, EMAIL, emailFragment);
                break;
            case PROFILE_PICK:
                profilePickFragment = new ProfilePickFragment();
                profilePickFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, PROFILE_PICK, profilePickFragment);
                break;
            case PASSWORD:
                PasswordFragment passwordFragment = new PasswordFragment();
                passwordFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, PASSWORD, passwordFragment);
                break;
            default:
                break;
        }
    }

    /**
     * addBackStack method is used to maintain the back stack.
     *
     * @param isReplaceAll    true if need replace previous else false.
     * @param ft              fragment to transmit
     * @param backStackName   back stack name
     * @param callingFragment calling fragment name.
     */
    private void addBackStack(boolean isReplaceAll, final FragmentTransaction ft, final String backStackName, final Fragment callingFragment) {
        setCurrentFrag(backStackName);

        if (isReplaceAll) {
            replaceFragment(backStackName);
        }
        if (MY_PHONE_NUMBER.equals(backStackName)) {
            ft.replace(R.id.main_content, callingFragment, backStackName);
            ft.commitAllowingStateLoss();
        } else {
            ft.replace(R.id.main_content, callingFragment, backStackName);
            ft.addToBackStack(backStackName);
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * Method called for replacing the fragment.
     */
    private void replaceFragment(final String backStackName) {

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        List<Fragment> listOfFragments = manager.getFragments();
        for (int i = (listOfFragments.size() - 1); i > 0; i--) {
            Constants.isDisableFragmentAnimations = true;
            if (listOfFragments.get(i) != null && listOfFragments.get(i).getFragmentManager() != null && !backStackName.equals(listOfFragments.get(i).getTag())) {
                listOfFragments.get(i).getFragmentManager().popBackStackImmediate();
            }
            Constants.isDisableFragmentAnimations = false;
        }
    }

    @Override
    public Resources getRes() {
        return this.getResources();
    }

    @Override
    public SignUpActivity getInstance() {
        return SignUpActivity.this;
    }

    public void setCurrentFrag(String currentFrag) {
        this.currentFrag = currentFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkAllPermission(Constants.PERMISSIONS_STORAGE);
    }

    private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(SignUpActivity.this, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(SignUpActivity.this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        //showEnablePermissionDialog();
                    }
                });
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, permission, 150);
            }
        }
    }

    private void showEnablePermissionDialog() {
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

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", SignUpActivity.this.getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        }*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (profilePickFragment != null)
            profilePickFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void putHashMap(String key, String value) {
        if (signUp.containsKey(key)) signUp.remove(key);

        signUp.put(key, value);
    }

    public void removeHashMap(String key) {
        if (signUp.containsKey(key)) signUp.remove(key);
    }

    public HashMap<String, String> getHashMap() {
        return signUp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) return;
        if (resultCode == Activity.RESULT_OK && requestCode == 300) {
            checkAllPermission(Constants.PERMISSIONS_STORAGE);
        } else if (requestCode == 200 && currentFrag.equalsIgnoreCase(MY_PHONE_NUMBER) && phoneNumberFragment != null) {
            phoneNumberFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == 200 && currentFrag.equalsIgnoreCase(ONE_TIME_PWD) && oneTimePwdFragment != null) {
            oneTimePwdFragment.onActivityResult(requestCode, resultCode, data);
        } else if (currentFrag.equalsIgnoreCase(PROFILE_PICK) && profilePickFragment != null) {
            profilePickFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
