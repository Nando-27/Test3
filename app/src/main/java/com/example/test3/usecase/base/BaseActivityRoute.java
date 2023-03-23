package com.example.test3.usecase.base;

import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public interface BaseActivityRoute {


    Intent intent (Context activity);

    public default void launch(@NotNull Context activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        activity.startActivity(intent(activity));
    }
}
