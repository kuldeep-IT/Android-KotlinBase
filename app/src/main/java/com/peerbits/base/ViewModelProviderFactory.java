package com.peerbits.base;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelProviderFactory<T> implements ViewModelProvider.Factory {

    private T viewModel;

    public ViewModelProviderFactory(T viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(viewModel.getClass())) {
            return modelClass.cast(viewModel);
        }
        throw new IllegalArgumentException("Unknown class name");
    }

}