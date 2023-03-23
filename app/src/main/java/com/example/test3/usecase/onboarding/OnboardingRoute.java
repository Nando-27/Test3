package com.example.test3.usecase.onboarding;

import android.content.Context;
import android.content.Intent;

import com.example.test3.usecase.base.BaseActivityRoute;

public class OnboardingRoute implements BaseActivityRoute {
    @Override
    public Intent intent(Context activity) {
        return new Intent(activity,OnboardingActivity.class);
    }
}
