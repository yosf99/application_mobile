package com.example.photo_editor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PackageManagerCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

public class MainActivity extends AppCompatActivity {
    TextView imageButtonCrop;
    TextView btnPrendre;
    Uri imageAffiche;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// ouvrir la camera avec la demande de permmession
        btnPrendre = findViewById(R.id.btn_camera);

        btnPrendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
                        String[] tabPermission={Manifest.permission.CAMERA};
                        requestPermissions(tabPermission,100);
                    }
                    else
                        OpenCamera();
                }
                else
                    OpenCamera();
            }
        });


// choisir une photo a partir de gallerie  avec la demande de permmession
        imageButtonCrop = findViewById(R.id.btn_add);
        imageButtonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED ){
                        String[] tabPermission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(tabPermission,300);
                    }
                    else
                        OpenGallerie();
                }
                else
                    OpenGallerie();
            }
        });
    }

    // methode pour ouvrir le gallerie
    private void OpenGallerie() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }


    // methode pour ouvrir la camera
    private void OpenCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Titre Image");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Description Image");
        imageAffiche = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        CameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageAffiche);
        startActivityForResult(CameraIntent, 101);
    }

    // ajouer la bare d'autiles
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if (data.getData() != null){
                Uri filePath = data.getData();//image selectionner stocker en filePath
                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(filePath);
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "photoEditor");
                int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                startActivityForResult(dsPhotoEditorIntent, 200);
            }

        }

        if (requestCode == 200){

            Intent intent = new Intent(MainActivity.this, resultActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                OpenCamera();
            }
            else
                Toast.makeText(this, "Permission manquante", Toast.LENGTH_SHORT).show();

        }
        if (requestCode == 300) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                OpenGallerie();
            }
            else
                Toast.makeText(this, "Permission manquante gallerie", Toast.LENGTH_SHORT).show();

        }
    }
}