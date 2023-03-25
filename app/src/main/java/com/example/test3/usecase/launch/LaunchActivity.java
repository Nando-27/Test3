package com.example.test3.usecase.launch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.test3.databinding.ActivityLaunchBinding;
import com.example.test3.usecase.onboarding.OnboardingRoute;

public class LaunchActivity extends AppCompatActivity {

    private ActivityLaunchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaunchBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Data();

    }
    private void Data() {
        showOnboarding();
    }

    private void showOnboarding() {
        OnboardingRoute onboardingRoute = new OnboardingRoute();
        onboardingRoute.launch(this);
        finish();
    }


}