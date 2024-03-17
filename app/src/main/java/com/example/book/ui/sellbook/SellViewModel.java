package com.example.book.ui.sellbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.book.MainActivity;
import com.example.book.R;
import com.example.book.AppController;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.ui.Model.Post;
import com.example.book.ui.extra.Enums;
import com.example.book.ui.signin.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.book.databinding.FragmentSellBinding;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellViewModel extends ViewModel {
    private final MutableLiveData<Boolean> featuredPostConfirmation = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private static final int GALLERY_REQUEST_CODE = 1000;

    private Uri imageUri;
    private Enums.BookCategory selectedBookCategory;

    private ProgressBar progressBar;

    private Enums.PostType postType;

    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageReference;
    private DatabaseReference mDataBaseReference;

    private FragmentSellBinding binding;

    public LiveData<Boolean> getFeaturedPostConfirmation() {
        return featuredPostConfirmation;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public SellViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDataBaseReference = FirebaseDatabase.getInstance().getReference("uploads");
    }



    public void pickImageFromGallery(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public void uploadPost(Activity activity, String bookName, String bookPrice, List<String> authors, String description, String condition, Uri imageUri) {

        if (firebaseAuth.getCurrentUser() != null) {
            progressBar.setVisibility(View.VISIBLE);
            if (imageUri != null) {
                StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri, activity));

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                String oldNewCondition = condition;

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                String uploadDate = sdf.format(new Date());

                                String userId = firebaseAuth.getCurrentUser().getUid();


                                if (!bookName.isEmpty() && !bookPrice.isEmpty() && !authors.isEmpty() && !description.isEmpty() && !uploadDate.isEmpty()) {
                                    post(activity, userId, uploadDate, bookName, bookPrice, authors, oldNewCondition, description, downloadUrl);
                                } else {
                                    errorMessage.setValue("Please fill in all the fields");
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(activity, "Failed to Upload Image", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(activity, "Please fill in all the Fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(activity, loginActivity.class);
            activity.startActivity(intent);
            Toast.makeText(activity, "Please log in to add a post.", Toast.LENGTH_SHORT).show();
        }
    }

    private void post(Activity activity, String userId, String uploadDate, String bookName, String bookPrice, List<String> authors,
                      String oldNewCondition, String description, String downloadUrl) {
        // Generate a unique key for the post
        String uploadId = mDataBaseReference.push().getKey();

        if (uploadId != null) {
            // Create a reference for the specific post using the generated key
            DatabaseReference postReference = mDataBaseReference.child(uploadId);

            // Create the Post object
            Post upload = new Post(uploadId, bookName, bookPrice, downloadUrl, authors, description, oldNewCondition, uploadDate, selectedBookCategory, userId, postType);

            // Set the value of the post in the database
            postReference.setValue(upload);
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);

            Toast.makeText(activity, "Post Added Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Failed to generate a unique key for the post", Toast.LENGTH_SHORT).show();
        }
    }




    private String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void setSelectedBookCategory(Enums.BookCategory selectedBookCategory) {
        this.selectedBookCategory = selectedBookCategory;
    }

    public void setPostType(Enums.PostType postType) {
        this.postType = postType;
    }
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up resources if needed
    }
}