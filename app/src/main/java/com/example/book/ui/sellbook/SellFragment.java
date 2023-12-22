package com.example.book.ui.sellbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.book.AppController;
import com.example.book.R;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.ui.Model.Post;
import com.example.book.ui.extra.Enums;
import com.example.book.ui.signin.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SellFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int GALLERY_REQUEST_CODE = 1000;
    private Uri imageUri;
    private ImageButton imgGallery;
    private Button upload_Data, f_post;
    private StorageReference mStorageReference;
    private DatabaseReference mDataBaseReference;

    private EditText bookNameEditText;
    private EditText bookPriceEditText;
    private EditText bookAuthor;
    private EditText bookDescription;

    private RadioGroup radioGroup;
    private RadioButton radioButtonNew;
    private RadioButton radioButtonUsed;

    String condition;
    private Spinner spinner;

    private Enums.BookCategory selectedBookCategory;

    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        ContentResolver contentResolver = requireContext().getContentResolver();

        imgGallery = view.findViewById(R.id.imageButton);
        upload_Data = view.findViewById(R.id.uploadData);

        bookNameEditText = view.findViewById(R.id.bookNameEditText);
        bookPriceEditText = view.findViewById(R.id.price);
        bookAuthor = view.findViewById(R.id.bookAuthorEditText);
        bookDescription = view.findViewById(R.id.DescriptionEditText);


        radioGroup = view.findViewById(R.id.radioGroup);
        radioButtonNew = view.findViewById(R.id.radioButtonNew);
        radioButtonUsed = view.findViewById(R.id.radioButtonUsed);
        f_post = view.findViewById(R.id.featurePost);

        spinner = view.findViewById(R.id.category_book);
        if (getActivity() != null) {

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.book_category, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(this);
        }

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDataBaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonNew) {
                condition = "New";
            } else if (checkedId == R.id.radioButtonUsed) {
                condition = "Used";
            }
        });

        imgGallery.setOnClickListener(v -> pickImageFromGallery());
        upload_Data.setOnClickListener(v -> checkAndUploadPost());
        f_post.setOnClickListener(v -> uploadPost(true));


        return view;
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgGallery.setImageURI(imageUri);
        }
    }

    private void checkAndUploadPost() {
        // Check if the user is logged in
        if (firebaseAuth.getCurrentUser() != null) {
            // User is logged in, proceed to upload post
            uploadPost(false);
        } else {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(getActivity(), loginActivity.class));
            Toast.makeText(getActivity(), "Please log in to add a post.", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadPost(boolean isFeatured) {
        if (imageUri != null) {
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            String bookName = bookNameEditText.getText().toString();
                            String bookPrice = bookPriceEditText.getText().toString();
                            String author = bookAuthor.getText().toString();
                            String description = bookDescription.getText().toString();
                            String old_new_condition = condition;

                            // Get the current date and time
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                            String uploadDate = sdf.format(new Date());

                            // Verify if any field is empty
                            if (!bookName.isEmpty() && !bookPrice.isEmpty() && !author.isEmpty() && !description.isEmpty() && !uploadDate.isEmpty()) {
//                                 If it's a featured post, show the confirmation dialog
                                if (isFeatured) {
                                    showFeaturedDialog();
                                } else {
                                    // If not a featured post, proceed with normal post
                                    postNormal(uploadDate, bookName, bookPrice, author, old_new_condition, description, downloadUrl);
                                }
                            } else {
                                if (getActivity() != null) {

                                    Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        if (getActivity() != null) {

                            Toast.makeText(getActivity(), "Fail to Upload Image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (getActivity() != null) {

                Toast.makeText(getActivity(), "Please fill in all the Fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postNormal(String uploadDate, String bookName, String bookPrice, String author,
                            String old_new_condition, String description, String downloadUrl) {
        Post upload = new Post(bookName, bookPrice, downloadUrl, author, description, old_new_condition, uploadDate, selectedBookCategory);
        String uploadId = mDataBaseReference.push().getKey();
        mDataBaseReference.child(uploadId).setValue(upload);
        if (getActivity() != null) {

            Toast.makeText(getActivity(), "Post Added Successfully", Toast.LENGTH_SHORT).show();
        }
        // Clear fields after posting
        clearFields();
    }

    private void clearFields() {
        // Clear all the input fields
        bookNameEditText.setText("");
        bookPriceEditText.setText("");
        bookAuthor.setText("");
        bookDescription.setText("");
        // Clear other fields as needed
    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //    AdapterView.OnitemSelected class Methods
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString();

        // Set the selectedBookCategory based on the spinner item
        if (selectedItem.equals("Academic")) {
            Toast.makeText(getActivity(), "Academic is selected", Toast.LENGTH_SHORT).show();
            selectedBookCategory = Enums.BookCategory.ACADEMIC;
        } else if (selectedItem.equals("General")) {
            Toast.makeText(getActivity(), "General is selected", Toast.LENGTH_SHORT).show();
            selectedBookCategory = Enums.BookCategory.GENERAL;
        }
    }

//    Featured Post:


    private AlertDialog alertDialog;

    private void showFeaturedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false); // Set the dialog to be non-cancelable

        // Inflate your custom layout for the dialog
        View view = getLayoutInflater().inflate(R.layout.activity_dialogue_confirm_feature, null);

        // Find views by ID
        TextView dialogTxt = view.findViewById(R.id.dialogtxt);
        Button btnConfirm = view.findViewById(R.id.btnconfirm);
        Button btnReject = view.findViewById(R.id.btnreject);

        dialogTxt.setText("Do you want to spend 5 coins to feature your post?");
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        // Set actions for the positive button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);
                coinManager.getTotalCoins(new CoinFetchCallback() {
                    @Override
                    public void onCoinsFetched(int totalCoins) {
                        if (totalCoins >= 5) {
                            // Deduct coins in Firebase
                            deductCoinsFromFirebase(5);

                            // Proceed with normal post upload
                            alertDialog.dismiss();
                            uploadPost(false);
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Not Enough Coins", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Set actions for the negative button
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Create the dialog
        builder.setView(view);
        alertDialog = builder.create();

        // Show the dialog
        alertDialog.show();
    }

    private void deductCoinsFromFirebase(int coinsToDeduct) {
        // Get the user's ID from Firebase Auth
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Reference to the user's coin data in Firebase
        DatabaseReference userCoinsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("coin");

        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the current coin balance
                    int currentCoins = dataSnapshot.getValue(Integer.class);

                    // Log the retrieved data
                    Log.d("CoinDeduction", "Current Coins: " + currentCoins);

                    // Deduct the coins
                    int newCoinsBalance = currentCoins - coinsToDeduct;

                    // Update the user's coin balance in Firebase
                    userCoinsRef.setValue(newCoinsBalance);
                } else {
                    Log.d("CoinDeduction", "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
                Log.e("CoinDeduction", "Database Error: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dismiss the dialog if it is showing
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
