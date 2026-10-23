package com.example.smartpantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;

/** The Settings screen: currently one saved preference, the expiry highlight toggle. */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        MaterialSwitch expirySwitch = root.findViewById(R.id.switch_expiry);

        // Show the saved value, then save every change straight away
        expirySwitch.setChecked(AppPreferences.isExpiryHighlightEnabled(requireContext()));
        expirySwitch.setOnCheckedChangeListener((button, isChecked) ->
                AppPreferences.setExpiryHighlightEnabled(requireContext(), isChecked));

        return root;
    }
}