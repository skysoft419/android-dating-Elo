package ello.views.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.obs.CustomTextView;

import ello.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    public String url;
    public String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView=findViewById(R.id.webView);
        title=getIntent().getStringExtra("title");
        url=getIntent().getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        CustomTextView titleTextView=findViewById(R.id.tv_header_title);
        titleTextView.setText(title);
        CustomTextView leftArrow=findViewById(R.id.tv_left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
