package ello.views.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.obs.CustomTextView;

import ello.R;
import ello.helpers.StaticData;

public class PhoneActivity extends AppCompatActivity {
    private EditText editText;
    private TextView send;
    private CustomTextView leftArrow;
    private CustomTextView headerTextView;
    private static int APP_REQUEST_CODE = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        editText=findViewById(R.id.emailEditText);
        headerTextView=findViewById(R.id.tv_header_title);
        leftArrow=findViewById(R.id.tv_left_arrow);
        headerTextView.setText("Phone Number");
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editText.setText("+"+StaticData.COUNTRY_CODE+StaticData.PHONE_NUMBER);
        send=findViewById(R.id.sendVerificationCodeTextView);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void phoneLogin() {
        AccountKit.logOut();
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
        final String token=result.getAccessToken().getToken();
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                StaticData.PHONE_NUMBER=account.getPhoneNumber().getPhoneNumber().toString();
                StaticData.COUNTRY_CODE=account.getPhoneNumber().getCountryCode().toString();
            }

            @Override
            public void onError(AccountKitError accountKitError) {

            }
        });
    }
}
