package ello.views.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.RequestCallback;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ello.utils.Enums.REQ_FB_SIGNUP;
import static ello.utils.Enums.REQ_UPDATE_SETTINGS;

public class EmailActivity extends AppCompatActivity implements ServiceListener {
   private CustomTextView leftArrow;
   private CustomTextView headerTextView;
   private CustomTextView hintTextView;
   private CustomTextView verificationTextView;
   private LinearLayout verificationLayout;
   private LinearLayout pinLayout;
   private TextView pinTextView;
   private ImageView icon;
   private EditText emailEditText;
    @Inject
    ApiService apiService;
    @Inject
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);
        setContentView(R.layout.activity_email);
        initView();
        updateView();
    }
    private void initView(){
        headerTextView=findViewById(R.id.tv_header_title);
        leftArrow=findViewById(R.id.tv_left_arrow);
        hintTextView=findViewById(R.id.hintTextView);
        emailEditText=findViewById(R.id.emailEditText);
        pinLayout=findViewById(R.id.pinLayout);
        pinTextView=findViewById(R.id.enterPin);
        verificationTextView=findViewById(R.id.sendVerificationCodeTextView);
        verificationTextView.setText("Send verification code");
        icon=findViewById(R.id.icon);
        if (StaticData.PIN.equals("1")){
            pinLayout.setVisibility(View.VISIBLE);
        }else{
            pinLayout.setVisibility(View.GONE);
        }
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(EmailActivity.this,VerifyEmailActivity.class);
               startActivity(intent);
            }
        });
        verificationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.equals(StaticData.EMAIL)){
                        if (StaticData.VERFIED.equals("0")){
                            hintTextView.setText("Email not verified. Tap below to request a verification email.");
                            hintTextView.setTextColor(getResources().getColor(R.color.red));
                            verificationTextView.setEnabled(true);
                            verificationTextView.setTextColor(getResources().getColor(R.color.red));
                            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                        }else{
                            hintTextView.setText("Email is verified.");
                            hintTextView.setTextColor(getResources().getColor(R.color.green));
                            verificationTextView.setEnabled(false);
                            verificationTextView.setTextColor(getResources().getColor(R.color.light_gray));
                            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_checked));
                        }
                    }else{
                        if(isValidEmail(s)){
                            hintTextView.setText("Email not verified. Tap below to request a verification email.");
                            hintTextView.setTextColor(getResources().getColor(R.color.red));
                            verificationTextView.setEnabled(true);
                            verificationTextView.setTextColor(getResources().getColor(R.color.red));
                            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                        }else{
                            hintTextView.setText("Invalid email.");
                            hintTextView.setTextColor(getResources().getColor(R.color.red));
                            verificationTextView.setEnabled(false);
                            verificationTextView.setTextColor(getResources().getColor(R.color.light_gray));
                            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
                        }
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void sendCode(){
        StaticData.EMAIL=emailEditText.getText().toString();
        apiService.sendCode(sessionManager.getToken(),emailEditText.getText().toString()).enqueue(new RequestCallback(1, this));
        Intent intent=new Intent(EmailActivity.this,VerifyEmailActivity.class);
        startActivity(intent);
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void updateView(){
        emailEditText.setText(StaticData.EMAIL);
        emailEditText.setSelection(StaticData.EMAIL.length());
        headerTextView.setText("Email");
        if (StaticData.VERFIED.equals("0")){
            hintTextView.setText("Email not verified. Tap below to request a verification email.");
            hintTextView.setTextColor(getResources().getColor(R.color.red));
            verificationTextView.setEnabled(true);
            verificationTextView.setTextColor(getResources().getColor(R.color.red));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        }else{
            hintTextView.setText("Email is verified.");
            hintTextView.setTextColor(getResources().getColor(R.color.green));
            verificationTextView.setEnabled(false);
            verificationTextView.setTextColor(getResources().getColor(R.color.light_gray));
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_checked));
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        StaticData.VERFIED="1";
        StaticData.PIN="1";
        Log.d("ok",jsonResp.getStrResponse());
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }
}
