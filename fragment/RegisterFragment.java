import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by *** on 23/11/14.
 * Modified by isak.prytz on 24/02/15 Week: 9.
 */
public class RegisterFragment extends RegisterBaseFragment {

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    protected LoginFragmentsListener mFragmentCallback;
    private final String LOG_TAG = RegisterFragment.class.getSimpleName();
    private TextView mAcceptTermsAndCondLinkText;
    protected Button mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //sets custom theme only on this fragment
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_appcustomer_Black);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = infalteView(localInflater);

        setUpCommonViewComponents(view);

        mLinearLayout = (LinearLayout) view.findViewById(R.id.login_register_view);
        mRegisterButton = (Button) view.findViewById(R.id.register_register_button);
        mAcceptTermsAndCond = (SwitchCompat) view.findViewById(R.id.register_switch_accept_terms_and_cond);
        mAcceptTermsAndCondLinkText = (TextView) view.findViewById(R.id.register_accept_terms_and_cond_linktext);

        UserRegistrationData model = mFragmentCallback.getTempUserData();

        if (model != null) {
            mZipCodeValue = model.getZipCode();
            mStateValue = model.getState();
            mRegionValue = model.getRegion();
            mGenderValue = model.getGender();
            mBirthDay = model.getBirthDate();
            mJobTypeValue = model.getJob();
            mChildNumberValue = model.getChildNumber();
            mChildBirthDay = model.getChildBirthDate();
            mCouponNewsValue = model.getCouponNews();
            mSpecialGiftNewsValue = model.getGiftNews();
            mProductNewsValue = model.getProductNews();
        } else {
            mCouponNewsValue = true;
            mSpecialGiftNewsValue = true;
        }

        setupHideKeyboardOnUI(view.findViewById(R.id.login_register_view));
        setUpEditText();
        setUpRadioGroup();
        setUpDayPickers();
        setUpDataSpinners();
        setUpJobAndChildNrSpinner();
        setUpSwitchCompats();


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    saveRegisterData();
                    mFragmentCallback.goToSendRegisterEmailFragment();
                }
            }
        });

        mAcceptTermsAndCondLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appcustomerConfiguration.Url internalLinkScheme = ((appcustomerDataProvider) getActivity().getApplicationContext()).getConfiguration().getUrl("APP_URL_INTERNAL_LINK_SCHEME");
                NavPoint navPoint = ModuleManager.getManager().mapUrl(internalLinkScheme + "webapp?webAppUrl="+ getString(R.string.login_accept_terms_and_cond_link_address));
                startActivity(navPoint.getIntent());
            }
        });

        return view;
    }

    protected View infalteView(LayoutInflater inflater){
        return inflater.inflate(R.layout.login_register_layout, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (LoginFragmentsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegisterOrLoginListener");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        saveRegisterData();
    }


    protected void saveRegisterData() {
        //((appcustomerActionbarActivity)getActivity()).showLoader(true);

        String zipCode = mZipCode.getText().toString();
        String state = mState.getSelectedItem().toString();
        String region = mRegion.getSelectedItem().toString();
        int gender = mGenderValue;
        Date birthDate = mBirthDay;
        int job = mJobType.getSelectedItemPosition();
        int childNumber = mChildNumber.getSelectedItemPosition();
        Date childBirthDate = mChildBirthDay;
        boolean couponNews = mCouponNews.isChecked();
        boolean giftNews = mSpecialGiftNews.isChecked();
        boolean productNews = mProductNews.isChecked();


        UserRegistrationData userRegistrationData = new UserRegistrationData(zipCode, state, region, gender, birthDate, job, childNumber, childBirthDate, couponNews, giftNews, productNews);

        mFragmentCallback.saveTempUserData(userRegistrationData);


    }





}
