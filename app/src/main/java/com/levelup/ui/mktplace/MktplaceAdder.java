package com.levelup.ui.mktplace;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.levelup.R;
import com.levelup.activity.MainActivity;
import com.levelup.fragment.mktplace.MktplaceFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MktplaceAdder extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText listingTitle;
    private EditText meetupLocation;
    private EditText listingDescription;
    private Button photoSelector;
    private Button createListing;
    private ProgressBar progressBar;
    private ImageView listingPhoto;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private StorageTask uploadTask;


    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marketplace_adder);
        listingTitle = findViewById(R.id.listing_title);
        meetupLocation = findViewById(R.id.meetup_location);
        listingDescription = findViewById(R.id.listing_description);
        photoSelector = findViewById(R.id.photo_selector);
        listingPhoto = findViewById(R.id.selected_image);
        progressBar = findViewById(R.id.progress_bar);
        createListing = findViewById(R.id.create_listing);

        storageRef = FirebaseStorage.getInstance().getReference("mktplace uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("mktplace uploads");


        photoSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(MktplaceAdder.this, "Upload in progress",
                        Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(listingPhoto.getContext()).load(imageUri).into(listingPhoto);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        boolean factors = imageUri != null && !meetupLocation.getText().toString().equals("")
                && !listingTitle.getText().toString().equals("");
        if (factors) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);
                            Toast.makeText(MktplaceAdder.this, "Listing successful",
                                Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String key = databaseRef.push().getKey();
                            String creatorUid = MainActivity.currUser.getId();
                            MktplaceItem title = new MktplaceItem(0, key, creatorUid,
                                listingTitle.getText().toString().trim(),
                                    downloadUrl.toString(),
                                    meetupLocation.getText().toString().trim(),
                                    listingDescription.getText().toString().trim());
                            databaseRef.child(key).setValue(title);
                            MktplaceFragment.setRefresh(true);
                            onBackPressed();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MktplaceAdder.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "Please check all fields and try again",
                Toast.LENGTH_SHORT).show();
        }
    }
}
