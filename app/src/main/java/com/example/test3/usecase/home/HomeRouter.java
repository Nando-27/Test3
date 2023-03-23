package com.example.test3.usecase.home;

import android.content.Context;
import android.content.Intent;

import com.example.test3.usecase.base.BaseActivityRoute;

public class HomeRouter implements BaseActivityRoute {
    @Override
    public Intent intent(Context activity) {
        return new Intent(activity, HomeActivity.class);
    }
}
