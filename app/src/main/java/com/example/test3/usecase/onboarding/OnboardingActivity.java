package com.example.test3.usecase.onboarding;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.test3.R;
import com.example.test3.databinding.ActivityOnboardingBinding;
import com.example.test3.usecase.home.HomeRouter;
import com.example.test3.usecase.onboarding.Page.OnboardingPageAdapter;
import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private LinearLayout layoutonboardingindicators;
    private MaterialButton materialButtonactiononboarding;
    private OnboardingPageAdapter onboardingAdapter;
    private OnboardingViewModel onboardingViewModel;

    private HomeRouter homeRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        homeRouter = new HomeRouter();
        setContentView(binding.getRoot());

        setup();
        Data();

    }

    private void Data() {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firtstart = sharedPreferences.getBoolean( "firstStart", true);

        if (firtstart){
            getLocalizacion();
            ItemsOnboarding();
        }else {
            homeRouter.launch(this);
            finish();
        }
    }

    private void setup() {
        onboardingViewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);


    }

    private void ItemsOnboarding() {
        layoutonboardingindicators = findViewById(R.id.layoutonboardingIndicador);
        materialButtonactiononboarding = findViewById(R.id.btnOnboarding);
        onboardingAdapter = onboardingViewModel.getAdapter().getValue();

        ViewPager2 onboardingviewpager = findViewById(R.id.OnboardingViewPager);

        onboardingviewpager.setAdapter(onboardingAdapter);
        setuponboardingindicators(onboardingAdapter);
        setcurrentonboardingindicator(0);

        onboardingviewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setcurrentonboardingindicator(position);
            }
        });

        materialButtonactiononboarding.setOnClickListener(view -> {
            if (onboardingviewpager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()){
                onboardingviewpager.setCurrentItem(onboardingviewpager.getCurrentItem()+1);
            }else {
                homeRouter.launch(this);
                finish();
                SharedPreferences pref = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("firstStart", false);
                editor.apply();
            }

        });
    }


    public void setuponboardingindicators(OnboardingPageAdapter onboardingAdapter){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0 ; i < indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutonboardingindicators.addView(indicators[i]);
        }
    }

    public void setcurrentonboardingindicator(int index){
        int childcount = layoutonboardingindicators.getChildCount();
        for(int i = 0; i < childcount;i++){
            ImageView imageView = (ImageView) layoutonboardingindicators.getChildAt(i);

            if (i == index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicador_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onboardingAdapter.getItemCount()-1){
            materialButtonactiononboarding.setText("Comenzar");
        }else {
            materialButtonactiononboarding.setText("Siguiente");
        }
    }

    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}