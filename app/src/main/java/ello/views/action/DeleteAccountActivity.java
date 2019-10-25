package ello.views.action;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.adapters.action.ActionAdapter;
import ello.adapters.action.AdapterCallback;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.chat.MatchedProfileModel;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.customize.CustomDialog;
import ello.views.main.LoginActivity;

public class DeleteAccountActivity extends AppCompatActivity implements AdapterCallback,ServiceListener {
    private RecyclerView recyclerView;
    private ActionAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView deleteButton;
    private CustomTextView leftArrow;
    private CustomTextView headerTextView;
    private int index;
    @Inject
    CustomDialog customDialog;
    @Inject
    ApiService apiService;
    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        AppController.getAppComponent().inject(this);
        recyclerView=findViewById(R.id.recyclerview);
        deleteButton=findViewById(R.id.deleteButton);
        headerTextView=findViewById(R.id.tv_header_title);
        leftArrow=findViewById(R.id.tv_left_arrow);
        headerTextView.setText("Delete Account");
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        deleteButton.setEnabled(false);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.showProgressDialog(DeleteAccountActivity.this,customDialog);
                deleteAccount();
            }
        });
        setAdapter();
    }
    private void deleteAccount(){
        apiService.deleteAccount(sessionManager.getToken(),StaticData.delete.get(index).getReasonId()).enqueue(new RequestCallback(1,this));
    }
    private void setAdapter(){
        layoutManager=new LinearLayoutManager(this);
        adapter=new ActionAdapter(this, StaticData.delete,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(int position) {
        index=position;
        deleteButton.setEnabled(true);
        deleteButton.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isOnline()){
            if (jsonResp.isSuccess()){
                sessionManager.clearToken();
                StaticData.matchedProfileModel=new MatchedProfileModel();
                StaticData.editProfileModel=null;
                StaticData.settingsModel=null;
                Intent intent=new Intent(DeleteAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }
}
