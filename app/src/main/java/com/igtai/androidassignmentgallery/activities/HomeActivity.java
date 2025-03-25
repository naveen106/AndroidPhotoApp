package com.igtai.androidassignmentgallery.activities;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.igtai.androidassignmentgallery.R;
import com.igtai.androidassignmentgallery.adapters.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fabCamera;

    private Uri photoUri;
    private File photoFile;
    private FirebaseAuth mAuth;
    private boolean isGuest;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private final int REQUEST_PERMISSION = 100;
    private final List<String> imagePaths = new ArrayList<>();
    private RecyclerView recyclerView;
    private GalleryAdapter adapter  = new GalleryAdapter(this, imagePaths, imagePath -> {
        Intent intent = new Intent(HomeActivity.this, ImageViewActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);
    });


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera(); //Open camera immediately if permission is granted
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {


                if (result.getResultCode() == RESULT_OK && photoUri != null) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photoUri));

                    String imagePath = getRealPathFromURI(photoUri);
                    if (imagePath != null) {
                        imagePaths.add(0, imagePath);
                        adapter.notifyItemInserted(0);
                        recyclerView.smoothScrollToPosition(0);
                        saveImageList();
                        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (photoFile != null) {
                        photoFile.delete(); // Clean up temporary file if camera was cancelled
                    }
                    photoFile = null;
                    Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        fabCamera = findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(view -> openCamera());
        mAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
        if (checkPermission()) {
            loadImages();
//            loadSavedImages();
        }

        isGuest = getIntent().getBooleanExtra("isGuest",false);
        if(isGuest) {
            Menu menu = navigationView.getMenu();
            MenuItem logoutItem = menu.findItem(R.id.menuLogoutButton);

            Log.d("Guest", "Value of isGuest = " + isGuest);
            logoutItem.setVisible(!isGuest);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

//       Handle Toolbar Menu Click
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));

//        checkPermissions();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menuLogoutButton) {
                logout();
                return true;
            }
            return false;
        });

    }

    private void saveImageList() {
        SharedPreferences prefs = getSharedPreferences("GalleryPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("imagePaths", new HashSet<>(imagePaths));
        editor.apply();
    }

    private void logout(){
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    private void loadImages() {

        imagePaths.clear(); // Ensure the list is empty before loading new images

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                File file = new File(imagePath);
                if (file.exists()) {
                    imagePaths.add(imagePath); // Add only valid images
                }
            }
            cursor.close();
        }

        if (adapter == null) {
            adapter = new GalleryAdapter(this, imagePaths, imagePath -> {
                Intent intent = new Intent(HomeActivity.this, ImageViewActivity.class);
                intent.putExtra("imagePath", imagePath);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(); // Refresh UI with new images
        }
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
                return false;
            }
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {

        //check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // Saves in /storage/emulated/0/Pictures/
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoUri != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
        }

    }

}
