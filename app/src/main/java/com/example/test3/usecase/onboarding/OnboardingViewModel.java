package com.example.test3.usecase.onboarding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test3.R;
import com.example.test3.model.OnboardingModel;
import com.example.test3.usecase.onboarding.Page.OnboardingPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnboardingViewModel extends ViewModel {
    private final MutableLiveData<OnboardingPageAdapter> adapter;

    public OnboardingViewModel(){
        adapter = new MutableLiveData<>();
        adapter.setValue(setupOnboardingItems());
    }

    public LiveData<OnboardingPageAdapter> getAdapter(){
        return adapter;
    }

    private OnboardingPageAdapter setupOnboardingItems(){

        List<OnboardingModel> onboardingitems = new ArrayList<>();

        OnboardingModel itemPayOnline = new OnboardingModel();
        itemPayOnline.setTitle("Paga tus facturas Online");
        itemPayOnline.setDescription("Las Facturas de agua, luz, gas y ademas mucho mas por su telefono");
        itemPayOnline.setImage(R.drawable.agreement);

        OnboardingModel itemonthway = new OnboardingModel();
        itemonthway.setTitle("Tus recibos no son los ultimo");
        itemonthway.setDescription("No te endeudes utiliza yape");
        itemonthway.setImage(R.drawable.drinkwelcome);

        OnboardingModel itemsaludo =new OnboardingModel();
        itemsaludo.setTitle("Bienvenido a nuestra APP");
        itemsaludo.setDescription("Eres lo mas importante para funcuionar en la vida");
        itemsaludo.setImage(R.drawable.usher);

        onboardingitems.add(itemPayOnline);
        onboardingitems.add(itemonthway);
        onboardingitems.add(itemsaludo);
        return new OnboardingPageAdapter(onboardingitems);
    }
}
