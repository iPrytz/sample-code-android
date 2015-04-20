import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by isak.prytz on 24/02/15 Week: 9.
 */
public class RegisterBaseFragment extends BaseFragment {

    private final String LOG_TAG = RegisterBaseFragment.class.getSimpleName();

    protected LinearLayout mLinearLayout;
    protected EditText mZipCode;
    protected String mZipCodeValue;
    protected TextView mZipCodeInvalidText;
    protected Spinner mState;
    protected String mStateValue;
    protected Spinner mRegion;
    protected String mRegionValue;
    protected RadioGroup mGenderGroup;
    protected int mGenderValue = -1;
    protected View mBirthDayHolder;
    protected TextView mBirthDayText;
    protected Date mBirthDay;
    protected Spinner mJobType;
    protected int mJobTypeValue = -1;
    protected Spinner mChildNumber;
    protected int mChildNumberValue = -1;
    protected View mChildBirthDayHolder;
    protected TextView mChildBirthDayText;
    protected Date mChildBirthDay;
    protected SwitchCompat mCouponNews;
    protected boolean mCouponNewsValue;
    protected SwitchCompat mSpecialGiftNews;
    protected boolean mSpecialGiftNewsValue;
    protected SwitchCompat mProductNews;
    protected boolean mProductNewsValue;
    protected SwitchCompat mAcceptTermsAndCond;
    protected appcustomerSpinnerAdapter mChildNumberAdapter;
    protected appcustomerSpinnerAdapter mJobAdapter;
    protected appcustomerSpinnerAdapter mStateAdapter;
    protected appcustomerSpinnerAdapter mRegionAdapter;


    protected void setUpCommonViewComponents(View view) {

        mZipCode = (EditText) view.findViewById(R.id.register_edit_zip_code);
        mZipCodeInvalidText = (TextView) view.findViewById(R.id.register_zip_code_invalid_error);
        mState = (Spinner) view.findViewById(R.id.register_state_spinner);
        mRegion = (Spinner) view.findViewById(R.id.register_edit_region);
        mGenderGroup = (RadioGroup) view.findViewById(R.id.register_radio_group_gender);
        mBirthDayHolder = view.findViewById(R.id.register_birth_day_holder);
        mBirthDayText = (TextView) view.findViewById(R.id.register_text_birthday);
        mJobType = (Spinner) view.findViewById(R.id.register_job_type);
        mChildNumber = (Spinner) view.findViewById(R.id.register_number_of_child);
        mChildBirthDayHolder = view.findViewById(R.id.register_youngest_child_birth_day_holder);
        mChildBirthDayText = (TextView) view.findViewById(R.id.register_text_child_birthday);
        mCouponNews = (SwitchCompat) view.findViewById(R.id.register_switch_new_coupon);
        mSpecialGiftNews = (SwitchCompat) view.findViewById(R.id.register_switch_special_gift);
        mProductNews = (SwitchCompat) view.findViewById(R.id.register_switch_new_product);

    }


    protected void setUpEditText() {

        mZipCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFocus();
                }
                return false;
            }
        });

        mZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);

                    String zipCode = mZipCode.getText().toString();

                    if (!TextUtils.isEmpty(zipCode)) {
                        registerDataProvider.getStateAndRegionFromZipcode(Integer.parseInt(zipCode), new DataProviderListener<ZipCode>() {
                            @Override
                            public void onSuccess(ZipCode result) {
                                mZipCodeInvalidText.setVisibility(View.GONE);

                                mRegionValue = result.getCity().getName();
                                mStateValue = result.getPrefecture().getName();
                                setRegionSpinner(result.getPrefecture().getName(), true);
                                int pos = mStateAdapter.getPosition(result.getPrefecture().getName());
                                mState.setSelection(pos);

                                pos = mRegionAdapter.getPosition(result.getCity().getName());
                                mRegion.setSelection(pos);

                            }

                            @Override
                            public void onFail(DataProviderError error) {
                                mZipCodeInvalidText.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    hideSoftKeyboard(getActivity(), v);
                } else {
                    mZipCode.setCursorVisible(true);
                }
            }
        });

        mZipCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mZipCode.setCursorVisible(true);
                return false;
            }
        });

        if (mZipCodeValue != null) {
            mZipCode.setText(mZipCodeValue);
        }

    }


    protected void setUpRadioGroup() {
        mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton genderRadio = (RadioButton) group.findViewById(checkedId);
                mGenderValue = group.indexOfChild(genderRadio);
            }
        });

        if (mGenderValue > -1 && mGenderValue < 2) {
            ((RadioButton) mGenderGroup.getChildAt(mGenderValue)).setChecked(true);
        }
    }


    protected void setUpDayPickers() {
        if (mBirthDay != null) {
            mBirthDayText.setText(getFormattedDate(mBirthDay));
        }
        mBirthDayHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = GregorianCalendar.getInstance();
                int year = 1990;
                int month = 0;
                int day = 1;
                if (mBirthDay != null) {
                    calendar.setTime(mBirthDay);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog datePicBirthDay = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth, 12, 00);
                        mBirthDay = calendar.getTime();
                        mBirthDayText.setText(getFormattedDate(mBirthDay));
                    }
                }, year, month, day);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicBirthDay.getDatePicker().setMaxDate(new Date().getTime());
                }
                datePicBirthDay.show();
            }
        });

        if (mChildBirthDay != null) {
            mChildBirthDayText.setText(getFormattedDate(mChildBirthDay));
        }
        mChildBirthDayHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = GregorianCalendar.getInstance();
                int year = 2000;
                int month = 0;
                int day = 1;
                if (mChildBirthDay != null) {
                    calendar.setTime(mChildBirthDay);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog datePicChildBirthDay = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth, 12, 00);
                        mChildBirthDay = calendar.getTime();
                        mChildBirthDayText.setText(getFormattedDate(mChildBirthDay));
                    }
                }, year, month, day);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicChildBirthDay.getDatePicker().setMaxDate(new Date().getTime());
                    if (mBirthDay != null) {
                        datePicChildBirthDay.getDatePicker().setMinDate(mBirthDay.getTime());
                    }
                }
                datePicChildBirthDay.show();

            }
        });

        if (mChildBirthDayText.equals("") || mChildBirthDayText == null) {
            mChildBirthDayHolder.setEnabled(false);
            mChildBirthDayText.setEnabled(false);
        }
    }

    protected void setUpJobAndChildNrSpinner() {

        mJobAdapter = new appcustomerSpinnerAdapter(getActivity());
        mJobAdapter.setEnabled(true);
        String[] jobArray = getResources().getStringArray(R.array.login_employee_type);
        for (String job : jobArray) {
            mJobAdapter.add(job);
        }
        mJobAdapter.add(getString(R.string.login_hint_employment));
        mJobType.setEnabled(true);
        mJobType.setAdapter(mJobAdapter);


        if (mJobTypeValue > -1 && mJobTypeValue < mJobAdapter.getCount()) {
            mJobType.setSelection(mJobTypeValue);

        } else {
            mJobType.setSelection(mJobAdapter.getCount());
        }

        mJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mChildNumberAdapter = new appcustomerSpinnerAdapter(getActivity());
        mChildNumberAdapter.setEnabled(true);
        String[] childArray = getResources().getStringArray(R.array.login_child_number);
        for (String child : childArray) {
            mChildNumberAdapter.add(child);
        }
        mChildNumberAdapter.add(""); //hint is not used
        mChildNumberAdapter.setDropDownViewResource(R.layout.spinner_drop_down_list_item);
        mChildNumber.setEnabled(true);
        mChildNumber.setAdapter(mChildNumberAdapter);


        if (mChildNumberValue != -1 && mChildNumberValue < mChildNumberAdapter.getCount()) {
            mChildNumber.setSelection(mChildNumberValue);
        }


        mChildNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if (mChildNumber.isEnabled()) {
                        mChildBirthDayHolder.setEnabled(true);
                        mChildBirthDayText.setEnabled(true);
                    }
                } else {
                    mChildBirthDayHolder.setEnabled(false);
                    mChildBirthDayText.setEnabled(false);
                    mChildBirthDayText.setText("");
                    mChildBirthDay = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    protected void setUpDataSpinners() {

        final AdapterView.OnItemSelectedListener stateSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mStateAdapter.getCount()) {
                    setRegionSpinner(mStateAdapter.getItem(position), true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };


        mStateAdapter = new appcustomerSpinnerAdapter(getActivity());
        mStateAdapter.add(getString(R.string.login_hint_state));
        mStateAdapter.add(getString(R.string.login_spinner_loading));
        mStateAdapter.setEnabled(false);
        mState.setAdapter(mStateAdapter);
        mState.setSelection(mStateAdapter.getCount());
//        mState.setOnItemSelectedListener(stateSpinnerListener);
        mState.setEnabled(false);


        mRegionAdapter = new appcustomerSpinnerAdapter(getActivity());
        mRegionAdapter.setEnabled(false);
        mRegionAdapter.add(getString(R.string.login_hint_region));
        mRegionAdapter.add(getString(R.string.login_hint_region));
        mRegionAdapter.setEnabled(false);
        mRegion.setAdapter(mRegionAdapter);
        mRegion.setSelection(mRegionAdapter.getCount());
        mRegion.setEnabled(false);



        AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);
        registerDataProvider.getStates(new DataProviderListener<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                mState.setEnabled(true);

                Activity activity = getActivity();
                if (activity != null) {
                    mStateAdapter = new appcustomerSpinnerAdapter(getActivity());
                    for (String state : result) {
                        mStateAdapter.add(state);
                    }
                    mStateAdapter.add(getString(R.string.login_hint_state));
                    mStateAdapter.setEnabled(true);
                    mState.setAdapter(mStateAdapter);
//                    mState.setOnItemSelectedListener(null);

                    if (mStateValue != null && !mStateValue.equals("")) {
                        int spinnerPos = mStateAdapter.getPosition(mStateValue);
                        mState.setSelection(spinnerPos);
                        setRegionSpinner(mStateAdapter.getItem(spinnerPos), false);
                    } else {
                        mState.setSelection(mState.getCount());

                    }
                    mState.setOnItemSelectedListener(stateSpinnerListener);
                }

            }

            @Override
            public void onFail(DataProviderError error) {
                Log.e(LOG_TAG, "registerDataProvider.getStates failed >>> " + error.getMessage());
            }
        });

    }


    protected void setRegionSpinner(final String stateName, final boolean setEditableWhenDone) {

        mRegionAdapter = new appcustomerSpinnerAdapter(getActivity());
        mRegionAdapter.add(getString(R.string.login_hint_region));
        mRegionAdapter.add(getString(R.string.login_spinner_loading));
        mRegionAdapter.setEnabled(false);
        mRegion.setAdapter(mRegionAdapter);
        mRegion.setSelection(mRegionAdapter.getCount());
        mRegion.setEnabled(false);
        AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);
        registerDataProvider.getRegionsOfState(stateName, new DataProviderListener<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                mRegion.setEnabled(setEditableWhenDone);
                Activity activity = getActivity();
                if (activity != null) {
                    mRegionAdapter = new appcustomerSpinnerAdapter(getActivity());
                    mRegionAdapter.setEnabled(setEditableWhenDone);
                    for (String region : result) {
                        mRegionAdapter.add(region);
                    }
                    mRegionAdapter.add(getString(R.string.login_hint_region));
                    mRegion.setAdapter(mRegionAdapter);

                    if (mRegionValue != null && !mRegionValue.equals("")) {
                        int spinnerPos = mRegionAdapter.getPosition(mRegionValue);
                        mRegion.setSelection(spinnerPos);
                    } else {
                        mRegion.setSelection(0);
                    }

                }
            }

            @Override
            public void onFail(DataProviderError error) {

            }
        });
    }

    protected void setUpSwitchCompats() {
        mCouponNews.setChecked(mCouponNewsValue);
        mSpecialGiftNews.setChecked(mSpecialGiftNewsValue);
        mProductNews.setChecked(mProductNewsValue);
    }


    public void setupHideKeyboardOnUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity(), v);
                    clearFocus();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupHideKeyboardOnUI(innerView);
            }
        }
    }


    protected void clearFocus() {
        if (mLinearLayout != null) {
            mZipCode.setCursorVisible(false);
//            mLinearLayout.requestFocus();
        } 
    }


    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    protected String getFormattedDate(Date date) {
        String returnString = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.login_birthday_format));
            returnString = format.format(date);
        } catch (Exception e) {
            // DO Nothing if crash....
        }

        return returnString;
    }

    protected long getDateStringAsLong(String birthDay) {
        long returnLong = 0L;
        try {
            returnLong = Long.parseLong(birthDay);
        } catch (Exception e) {
            // DO Nothing if crash....
        }
        return returnLong;
    }

    public void showErrorDialog(final int errorTitleId, final int errorBodyId) {

        final Activity activity = getActivity();

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
//                            .setTitle(errorTitleId)
                            .setMessage(errorBodyId)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }

    }

    public void showFelicaInitErrorDialog(final DataProviderListener<SimpleResponse> listener) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.login_felica_init_error_title)
                            .setMessage(R.string.login_felica_init_error_message)
                            .setPositiveButton(R.string.login_felica_init_retry_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FeliCaDataProviderImpl dataProvider = DataProviders.get(FeliCaDataProviderImpl.class);
                                    dataProvider.initFelicaChip(new DataProviderListener<SimpleResponse>() {
                                        @Override
                                        public void onSuccess(SimpleResponse result) {
                                            listener.onSuccess(result);
                                        }

                                        @Override
                                        public void onFail(DataProviderError error) {
                                            listener.onFail(error);
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(R.string.login_felica_init_cancel_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppcustomerUserConfiguration userConfiguration = DataProviders.get(AppcustomerUserConfiguration.class);
                                    userConfiguration.clearUser();
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }
    }


    protected boolean validateForm() {
        boolean enableButton = true;
        String errorTitle = "";
        String errorMessage = "";

        if (TextUtils.isEmpty(mZipCode.getText().toString())) {
            Log.d("Appcustomer", "zipcode false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_zip_code_title);
            errorMessage = getString(R.string.login_error_zip_code_message);
        } else if (mState.getSelectedItemPosition() == mState.getCount()) {
            Log.d("Appcustomer", "state false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_state_title);
            errorMessage = getString(R.string.login_error_state_message);
        } else if (mRegion.getSelectedItemPosition() == mRegion.getCount()) {
            Log.d("Appcustomer", "region false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_region_title);
            errorMessage = getString(R.string.login_error_region_message);
        } else if (mGenderValue == -1) {
            Log.d("Appcustomer", "gender false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_gender_title);
            errorMessage = getString(R.string.login_error_gender_message);
        } else if (mBirthDay == null) {
            Log.d("Appcustomer", "birthday false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_birthday_title);
            errorMessage = getString(R.string.login_error_birthday_message);
        } else if (mJobType.getSelectedItemPosition() == mJobType.getCount()) {
            Log.d("Appcustomer", "job false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_employment_title);
            errorMessage = getString(R.string.login_error_employment_message);
        } else if (mChildNumber.getSelectedItemPosition() > 0 && mChildBirthDay == null) {
            Log.d("Appcustomer", "childbirthday false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_youngest_child_birthday_title);
            errorMessage = getString(R.string.login_error_youngest_child_birthday_message);
        } else if (mAcceptTermsAndCond != null && !mAcceptTermsAndCond.isChecked()) {
            Log.d("Appcustomer", "accept terms and cond. false");
            enableButton = false;
            errorTitle = getString(R.string.login_error_accept_terms_and_cond_title);
            errorMessage = getString(R.string.login_error_accept_terms_and_cond_message);
        } else if (mChildBirthDay != null && mBirthDay != null && mChildBirthDay.before(mBirthDay)) {
            Log.d("Appcustomer", "child cant be older than parent");
            enableButton = false;
            errorTitle = getString(R.string.login_error_child_older_than_parent_title);
            errorMessage = getString(R.string.login_error_child_older_than_parent_message);
        } else if (mBirthDay != null && mBirthDay.after(new Date())) {
            Log.d("Appcustomer", "birthday after today's date");
            enableButton = false;
            errorTitle = getString(R.string.login_error_birthday_after_today_title);
            errorMessage = getString(R.string.login_error_birthday_after_today_message);
        } else if (mChildBirthDay != null && mChildBirthDay.after(new Date())) {
            Log.d("Appcustomer", "child bday after today's date");
            enableButton = false;
            errorTitle = getString(R.string.login_error_child_birthday_after_today_title);
            errorMessage = getString(R.string.login_error_child_birthday_after_today_message);
        }


        if (!enableButton) {
            new AlertDialog.Builder(getActivity())

                    .setMessage(errorMessage)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

//        mRegisterButton.setEnabled(enableButton);

        return enableButton;

    }
}
