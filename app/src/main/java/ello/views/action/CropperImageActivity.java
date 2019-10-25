package ello.views.action;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import ello.R;
import ello.helpers.StaticData;
import ello.views.cropper.CropImageView;

public class CropperImageActivity extends AppCompatActivity {
    private CropImageView cropImageView;
    private TextView cancelButton;
    private TextView doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cropper_image);
        cancelButton=findViewById(R.id.cancel_button);
        doneButton=findViewById(R.id.done_button);
        cropImageView=(CropImageView)findViewById(R.id.CropImageView);
        cropImageView.setAspectRatio(3,4);
        cropImageView.setFixedAspectRatio(true);
        int wi=StaticData.image.getWidth();
        int he=StaticData.image.getHeight();
        cropImageView.setImageBitmap(StaticData.image);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.image=null;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","NO");
                setResult(Activity.RESULT_CANCELED,returnIntent);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.croppedImage=cropImageView.getCroppedImage();
                int width=StaticData.croppedImage.getWidth();
                int height=StaticData.croppedImage.getHeight();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","YES");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
