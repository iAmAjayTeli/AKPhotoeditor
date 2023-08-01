package com.example.akphotoeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.akphotoeditor.databinding.ActivityMainBinding;
import android.Manifest;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 1002;
    private static final int GALLERY_REQUEST_CODE = 1003;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());


        binding.imageView3.setImageResource(R.drawable.photoediting);

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {

                // Camera permission denied, handle this situation
                showCameraPermissionDeniedError();
            }
        }
    }

    private void showCameraPermissionDeniedError() {
        Toast.makeText(this, "Camera permission denied. Please grant the camera permission in the app settings.", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data !=null) {
            // The image is captured from the camera
            Uri imageUri = Uri.parse(MediaStore.EXTRA_OUTPUT);
//            Uri imageUri = getCapturedImageUri(data);
            // Start the new activity with the captured image
            startEditingActivity(imageUri);
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            // The image is picked from the gallery
            Uri selectedImage = data.getData();
            // Start the new activity with the selected image
            startEditingActivity(selectedImage);
        }
    }

    private Uri getCapturedImageUri(Intent data) {
        // Check if the EXTRA_OUTPUT extra is set in the camera intent
        if (data != null && data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
            return Uri.parse(data.getParcelableExtra(MediaStore.EXTRA_OUTPUT).toString());
        } else {
            // Handle the case where the captured image URI is not available
            return null;
        }
    }

    private void startEditingActivity(Uri imageUri) {
        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }
}


