import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

/**
 * Created by isak.prytz on 23/02/15 Week: 9.
 */
public class ProfileFragment extends RegisterBaseFragment {

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    private final String LOG_TAG = ProfileFragment.class.getSimpleName();
    private TextView mEmail;

    private ProfileFragmentListener mFragmentCallback;
    private Button mUpdateProfileButton;
    private Button mUpdateEmailButton;
    private Button mSaveProfileDataButton;
    private boolean mIsEditableMode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_appcustomer_Black);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.profile_layout, null);
        setUpCommonViewComponents(view);

        mLinearLayout = (LinearLayout) view.findViewById(R.id.profile_layout_view);
        mUpdateProfileButton = (Button) view.findViewById(R.id.profile_update_profile_button);
        mUpdateEmailButton = (Button) view.findViewById(R.id.profile_update_email_button);
        mEmail = (TextView) view.findViewById(R.id.login_profile_email_user);
        mSaveProfileDataButton = (Button) view.findViewById(R.id.profile_save_data_button);

        setHasOptionsMenu(true);

        setUpButtons(view);

        mLinearLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && mIsEditableMode) {
                    showLeaveDialog();
                    return true;
                }
                return false;
            }
        });

        //Set up profile with saved data
        final AppcustomerUserConfiguration userConfig = DataProviders.get(AppcustomerUserConfiguration.class);
        setUpProfileForView(userConfig.getUserConfig());

        //Set up profile again with data from server
        AppcustomerRegisterDataProvider dataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);
        dataProvider.updateUserData(new DataProviderListener<SimpleResponse>() {
            @Override
            public void onSuccess(SimpleResponse result) {
                AppcustomerUserModel model = userConfig.getUserConfig();
                setUpFetchedData(model);
            }

            @Override
            public void onFail(DataProviderError error) {
                Log.d(LOG_TAG, "updateUserData >>> Data couldn't update from server");

            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsEditableMode) {
                    showLeaveDialog();
                } else {
                    mFragmentCallback.goBack();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (ProfileFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegisterOrLoginListener");
        }
    }

    private void setUpButtons(final View rootView) {
        mUpdateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEditableMode) {
                    updateUserProfile();
                } else {
                    setUpProfileForEdit(rootView);
                }

            }
        });

        mUpdateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.goToUpdateEmailFragment();
            }
        });

        mSaveProfileDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }


    private void updateValues(AppcustomerUserModel model) {
        mZipCodeValue = model.getZipCode();
        mStateValue = model.getState();
        mRegionValue = model.getRegion();
        mGenderValue = model.getGender();

        mBirthDay = new Date(getDateStringAsLong(model.getBirthDay()));
        //mBirthDayText.setText(getFormattedDate(mBirthDay));

        mJobTypeValue = model.getJobType();
        mChildNumberValue = model.getNumberChild();
        if (mChildNumberValue > 0) {
            mChildBirthDay = new Date(getDateStringAsLong(model.getChildBirthDay()));
        }
        mCouponNewsValue = model.getCouponNews();
        mSpecialGiftNewsValue = model.getSpecialGiftNews();
        mProductNewsValue = model.getProductNews();
    }

    private void setUpProfileForView(AppcustomerUserModel model) {

        if (model != null) {
            mEmail.setText(model.getEmail());

            updateValues(model);

        } else {
            Log.d(LOG_TAG, "setUpProfileForView model == null");
        }

        setUpTempDataSpinners();
        setUpEditText();
        setUpJobAndChildNrSpinner();
        setUpRadioGroup();
        setUpDayPickers();
        setUpSwitchCompats();

        setEnabledOnAll(false);

        mSaveProfileDataButton.setVisibility(View.GONE);

        );

    }

    private void setUpFetchedData(AppcustomerUserModel model) {

        if (model != null) {
            mEmail.setText(model.getEmail());
            mZipCode.setText(model.getZipCode());
            mStateValue = model.getState();
            mRegionValue = model.getRegion();
            ((RadioButton) mGenderGroup.getChildAt(model.getGender())).setChecked(true);
            mBirthDayText.setText(getFormattedDate(new Date(getDateStringAsLong(model.getBirthDay()))));

            mJobType.setSelection(model.getJobType());
            mChildNumber.setSelection(model.getNumberChild());

            if (model.getNumberChild() > 0) {
                mChildBirthDayText.setText(getFormattedDate(new Date(getDateStringAsLong(model.getChildBirthDay()))));
            } else {
                mChildBirthDayText.setText("");
            }
            mCouponNews.setChecked(model.getCouponNews());
            mSpecialGiftNews.setChecked(model.getSpecialGiftNews());
            mProductNews.setChecked(model.getProductNews());

        } else {

        }

        setRegionSpinner(mStateValue, false);

        mState.setSelection(mStateAdapter.getPosition(mStateValue));

    }

    private void setUpProfileForEdit(View rootView) {
        mIsEditableMode = true;
        mUpdateProfileButton.setText(R.string.profile_save_data_button);

        mUpdateEmailButton.setEnabled(false);
        mSaveProfileDataButton.setVisibility(View.VISIBLE);
        setEnabledOnAll(true);

        setupHideKeyboardOnUI(rootView.findViewById(R.id.profile_layout_view));
        setUpJobAndChildNrSpinner();
        setUpDataSpinners();

    }

    private void setEnabledOnAll(boolean setThisEnabled) {
        mZipCode.setEnabled(setThisEnabled);
        mStateAdapter.setEnabled(setThisEnabled);
        mState.setEnabled(setThisEnabled);
        mRegionAdapter.setEnabled(setThisEnabled);
        mRegion.setEnabled(setThisEnabled);
        for (int i = 0; i < mGenderGroup.getChildCount(); i++) {
            mGenderGroup.getChildAt(i).setEnabled(setThisEnabled);
        }
        mBirthDayHolder.setEnabled(setThisEnabled);
        mBirthDayText.setEnabled(setThisEnabled);
        mJobType.setEnabled(setThisEnabled);
        mJobAdapter.setEnabled(setThisEnabled);
        mChildNumber.setEnabled(setThisEnabled);
        mChildNumberAdapter.setEnabled(setThisEnabled);
        if (mChildNumberValue > 0) {
            mChildBirthDayHolder.setEnabled(setThisEnabled);
            mChildBirthDayText.setEnabled(setThisEnabled);
        } else {
            mChildBirthDayHolder.setEnabled(false);
            mChildBirthDayText.setEnabled(false);
        }
        mCouponNews.setEnabled(setThisEnabled);
        mSpecialGiftNews.setEnabled(setThisEnabled);
        mProductNews.setEnabled(setThisEnabled);

    }


    private void setUpTempDataSpinners() {
        mStateAdapter = new appcustomerSpinnerAdapter(getActivity());
        if (mStateValue != null) {
            mStateAdapter.add(mStateValue);
        }
        mStateAdapter.add("");
        mStateAdapter.add("");
        mStateAdapter.setEnabled(false);
        mState.setAdapter(mStateAdapter);
        mState.setEnabled(false);
        mState.setSelection(0);

        mRegionAdapter = new appcustomerSpinnerAdapter(getActivity());
        if (mRegionValue != null) {
            mRegionAdapter.add(mRegionValue);
        }
        mRegionAdapter.add("");
        mRegionAdapter.add("");
        mRegionAdapter.setEnabled(false);
        mRegion.setAdapter(mRegionAdapter);
        mRegion.setEnabled(false);
        mRegion.setSelection(0);
    }

    private void updateUserProfile() {

        final String email = mEmail.getText().toString();
        final String zipCode = mZipCode.getText().toString();
        final String state = mState.getSelectedItem().toString();
        final String region = mRegion.getSelectedItem().toString();
        final int gender = mGenderValue;
        final Date birthDate = mBirthDay;
        final int job = mJobType.getSelectedItemPosition();
        final int childNumber = mChildNumber.getSelectedItemPosition();
        final Date childBirthDate = mChildBirthDay;
        final boolean couponNews = mCouponNews.isChecked();
        final boolean giftNews = mSpecialGiftNews.isChecked();
        final boolean productNews = mProductNews.isChecked();


        if (validateForm()) {
            mIsEditableMode = false;
            mUpdateProfileButton.setEnabled(false);
            mSaveProfileDataButton.setEnabled(false);

            ((appcustomerActionbarActivity) getActivity()).showLoader();

            AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);

            registerDataProvider.updateProfile(email, zipCode, state, region, gender, birthDate, job, childNumber, childBirthDate, couponNews, giftNews, productNews, new DataProviderListener<SimpleResponse>() {
                @Override
                public void onSuccess(SimpleResponse result) {

                    AppcustomerUserConfiguration configuration = DataProviders.get(AppcustomerUserConfiguration.class);
                    updateValues(configuration.getUserConfig());

                    if (getActivity() != null) {
                        ((appcustomerActionbarActivity) getActivity()).hideLoader();
                    }

                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    setEnabledOnAll(false);
                    mUpdateProfileButton.setText(R.string.profile_update_profile_button);
                    mUpdateProfileButton.setEnabled(true);
                    mSaveProfileDataButton.setEnabled(true);
                    mSaveProfileDataButton.setVisibility(View.GONE);


                    mUpdateEmailButton.setEnabled(true);

                }

                @Override
                public void onFail(DataProviderError error) {
                    if (getActivity() != null) {
                        ((appcustomerActionbarActivity) getActivity()).hideLoader();
                    }

                    showErrorDialog(R.string.login_error_register_error_title, R.string.core_error_dialog_no_internet);

                    Log.d(LOG_TAG, "Profile update error: " + error.getMessage());

                    mUpdateEmailButton.setEnabled(true);
                }
            });

        }
    }

    private void showAlert(String errorTitle, String errorMessage) {
        new AlertDialog.Builder(getActivity())
                .setTitle(errorTitle)
                .setMessage(errorMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showLeaveDialog() {

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.profile_leave_page_dialog_title))
                .setMessage(getString(R.string.profile_leave_page_dialog_body))
                .setNegativeButton(getString(R.string.profile_leave_page_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.profile_leave_page_dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFragmentCallback.goBack();
                        dialog.dismiss();

                    }
                }).show();
    }

}