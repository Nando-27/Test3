package com.example.test3.usecase.onboarding.Page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test3.R;
import com.example.test3.model.OnboardingModel;

import java.util.ArrayList;
import java.util.List;

public class OnboardingPageAdapter extends RecyclerView.Adapter<OnboardingPageAdapter.OnboardingViewHolder>{

    private List<OnboardingModel> onboardingitems;

    public OnboardingPageAdapter(List<OnboardingModel> onboardingitems) {
        this.onboardingitems = onboardingitems;
    }



    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.onboarding_container_fragment, parent , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingitems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingitems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{
        private TextView texttitle;
        private TextView textDescription;
        private ImageView imageonboarding;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            texttitle = itemView.findViewById(R.id.textitle);
            textDescription = itemView.findViewById(R.id.textdescription);
            imageonboarding = itemView.findViewById(R.id.imagenonboarding);
        }

        void setOnboardingData(OnboardingModel onboardingitem){
            texttitle.setText(onboardingitem.getTitle());
            textDescription.setText(onboardingitem.getDescription());
            imageonboarding.setImageResource(onboardingitem.getImage());
        }
    }

}