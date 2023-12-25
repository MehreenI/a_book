package com.example.book.ui.sellbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.book.AppController;
import com.example.book.MainActivity;
import com.example.book.R;
import com.example.book.databinding.FragmentSellBinding;
import com.example.book.manager.CoinFetchCallback;
import com.example.book.manager.CoinManager;
import com.example.book.ui.extra.Enums;

public class SellFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SellViewModel sellViewModel;
    private FragmentSellBinding binding;
    private static final int GALLERY_REQUEST_CODE = 1000;
    private ImageButton imgGallery;

    Uri imageUri;

    String condition;
    String bookName;
    String bookPrice;
    String author;
    String description;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSellBinding.inflate(inflater, container, false);
        setupRadioGroup();
        imgGallery = binding.imageButton; // Initialize ImageButton
        return binding.getRoot();
    }

    private void setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.radioButtonNew.getId()) {
                condition = "New";
            } else {
                condition = "Used"; // Assuming "Used" is the correct condition
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sellViewModel = new ViewModelProvider(this).get(SellViewModel.class);

        setupSpinner();

        // Set up click listeners
        binding.imageButton.setOnClickListener(v -> pickImageFromGallery());
        binding.uploadData.setOnClickListener(v -> uploadPost(false));
//        binding.featurePost.setOnClickListener(v -> uploadPost(true));
        binding.featurePost.setOnClickListener(v -> showFeaturedDialogueBox());

        // Observe the featured post confirmation LiveData
        sellViewModel.getFeaturedPostConfirmation().observe(getViewLifecycleOwner(), isConfirmed -> {
            if (isConfirmed) {
                // Handle the case where the user confirmed the featured post
                Toast.makeText(requireContext(), "Post will be featured!", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where the user rejected the featured post
                Toast.makeText(requireContext(), "Post will not be featured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AlertDialog alertDialog;

    private void showFeaturedDialogueBox() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false); // Set the dialog to be non-cancelable

        // Inflate your custom layout for the dialog
        View view = getLayoutInflater().inflate(R.layout.activity_dialogue_confirm_feature, null);

        // Find views by ID
        TextView dialogTxt = view.findViewById(R.id.dialogtxt);
        Button btnConfirm = view.findViewById(R.id.ok);
        Button btnReject = view.findViewById(R.id.btnreject);

        dialogTxt.setText("Do you want to spend 5 coins to feature your post?");

        // Set actions for the positive button
        btnConfirm.setOnClickListener(v -> {
            CoinManager coinManager = AppController.getInstance().getManager(CoinManager.class);
            coinManager.getTotalCoins(new CoinFetchCallback() {
                @Override
                public void onCoinsFetched(int totalCoins) {
                    if (totalCoins >= 5) {
                        // Deduct coins in Firebase
                        coinManager.deductCoinsFromFirebase(5);

                        // Proceed with normal post upload
                        uploadPost(true);
                    } else {
                        Toast.makeText(getActivity(), "Not Enough Coins", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        // Set actions for the negative button
        btnReject.setOnClickListener(v -> alertDialog.dismiss());

        // Create or update the dialog
        builder.setView(view);
        alertDialog = builder.create();

        // Show the dialog
        alertDialog.show();
    }


    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.book_category,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categoryBook.setAdapter(adapter);
        binding.categoryBook.setOnItemSelectedListener(this);
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

    private void uploadPost(boolean isFeatured) {
        bookName = binding.bookNameEditText.getText().toString();
        author = binding.bookAuthorEditText.getText().toString();
        bookPrice = binding.price.getText().toString();
        description = binding.DescriptionEditText.getText().toString();

        if (binding != null && imageUri != null) { // Null checks
            if (isFeatured) {
                sellViewModel.setPostType(Enums.PostType.FEATURED);
                Log.e("FEATURED","Post is Featured");
            } else {
                sellViewModel.setPostType(Enums.PostType.NORMAL);
                Log.e("FEATURED","Post is not Featured");

            }

            // Create Post object
            sellViewModel.uploadPost(requireActivity(), bookName, bookPrice, author, description, condition, imageUri);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            clearFields();
        } else {
            // Handle the case where binding or imageUri is null
            Toast.makeText(requireContext(), "Error uploading post", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();

        // Set the selectedBookCategory based on the spinner item
        if (selectedItem.equals("Academic")) {
            Toast.makeText(requireContext(), "Academic is selected", Toast.LENGTH_SHORT).show();
            sellViewModel.setSelectedBookCategory(Enums.BookCategory.ACADEMIC);
        } else if (selectedItem.equals("General")) {
            Toast.makeText(requireContext(), "General is selected", Toast.LENGTH_SHORT).show();
            sellViewModel.setSelectedBookCategory(Enums.BookCategory.GENERAL);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle the case where nothing is selected in the spinner
    }
    private void clearFields() {
        binding.bookNameEditText.setText("");
        binding.price.setText("");
        binding.bookAuthorEditText.setText("");
        binding.DescriptionEditText.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
