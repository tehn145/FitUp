package com.example.fitup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditUsernameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // You need to create a layout file for this, e.g., 'fragment_edit_name.xml'
        // It should contain an EditText and a "Save" button.
        return inflater.inflate(R.layout.fragment_edit_name, container, false);
    }
    //fix sau
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find your EditText and Button here
        // Set up the save button's OnClickListener to:
        // 1. Get the text from the EditText.
        // 2. Save the new name to Firestore.
        // 3. Close the bottom sheet.
    }
}
