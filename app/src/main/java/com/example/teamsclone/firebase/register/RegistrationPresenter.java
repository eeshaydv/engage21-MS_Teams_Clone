package com.example.teamsclone.firebase.register;

import android.app.Activity;

public class RegistrationPresenter implements RegistrationContract.Presenter, RegistrationContract.onRegistrationListener {

    private RegistrationContract.View mRegisterView;
    private RegistrationInteractor mRegistrationInteractor;

    public RegistrationPresenter(RegistrationContract.View registerView) {
        this.mRegisterView = registerView;
        mRegistrationInteractor = new RegistrationInteractor(this);
    }

    @Override
    public void register(Activity activity, String email, String password, String confirmPassword, String name) {
        mRegistrationInteractor.validateCredentials(activity, email, password, confirmPassword, name);
    }

    @Override
    public void onSuccess(String message) {
        mRegisterView.onRegistrationSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mRegisterView.onRegistrationFailure(message);
    }
}
