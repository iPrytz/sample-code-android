import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;

/**
 * Created by *** on 23/11/14.
 * Modified by isak.prytz on 26/02/15 Week: 9.
 */
public class LoginActivity extends BaseActivity implements LoginFragmentsListener, DoneFragment.DoneFragmentListener {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    public static final String PREFERENCE_NAME_REGISTRATION = "appcustomer_registration";
    public static final String PREFERENCE_REGISTRATION_JSON_DATA = "appcustomer_registration_data";

    protected static final Gson gson = new Gson();
    protected static SharedPreferences mPreference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActivity();
        mPreference = this.getSharedPreferences(PREFERENCE_NAME_REGISTRATION, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setUpActivity();
    }

    private void setUpActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String startActivityAs = bundle.getString(START_ACTIVITY_AS_KEY);
            String consumerEmail = bundle.getString(CONSUMER_EMAIL_KEY);
            String verificationToken = bundle.getString(CONSUMER_VERIFICATION_TOKEN_KEY);

            if (startActivityAs.equals(START_ACTIVITY_AS_LOGIN_EMAIL_RECEIVED)) {

                getSupportFragmentManager().popBackStackImmediate(BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(LoginSendEmailFragment.newInstance(LoginSendEmailFragment.EMAIL_DEEP_LINK_WAS_CLICKED, consumerEmail, verificationToken), true);

            } else if (startActivityAs.equals(START_ACTIVITY_AS_REGISTRATION_EMAIL_RECEIVED)) {

                getSupportFragmentManager().popBackStackImmediate(BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(RegisterSendEmailFragment.newInstance(RegisterSendEmailFragment.EMAIL_DEEP_LINK_WAS_CLICKED, consumerEmail, verificationToken), true);

            } else {
                replaceFragment(RegisterOrLoginFragment.newInstance());
            }

        } else {

            replaceFragment(RegisterOrLoginFragment.newInstance());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (doneFragmentIsVisible()) {
                    afterDoneAction();
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    onBackPressed();
                }else {
                clearFragmentBackstack();
                replaceFragment(RegisterOrLoginFragment.newInstance());
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToRegisterOrLoginFragment() {
        clearFragmentBackstack();
        replaceFragment(RegisterOrLoginFragment.newInstance());
    }

    @Override
    public void goToRegisterFragment() {
        replaceFragment(RegisterFragment.newInstance(), true);
    }

    @Override
    public void goToUpgradeFragment(String email, String memId, String mail1, String mail2, String mail3, String password, String userId) {
        replaceFragment(UpgradeFragment.newInstance(email, memId, mail1, mail2, mail3, password, userId));
    }

    @Override
    public void goToSendLoginEmailFragment() {
        replaceFragment(LoginSendEmailFragment.newInstance(LoginSendEmailFragment.EMAIL_SEND_VERIFICATION_EMAIL), null, BACK_STACK_TAG, true);
    }


    @Override
    public void goToSendRegisterEmailFragment() {
        replaceFragment(RegisterSendEmailFragment.newInstance(RegisterSendEmailFragment.EMAIL_SEND_VERIFICATION_EMAIL), null, BACK_STACK_TAG, true);
    }

    @Override
    public void goToLoginDoneFragment() {
        clearFragmentBackstack();
        replaceFragment(DoneFragment.newInstance(DoneFragment.DONE_TYPE_LOGIN), DONE_FRAGMENT_TAG, false);
    }

    @Override
    public void goToRegisterDoneFragment() {
        clearFragmentBackstack();
        replaceFragment(DoneFragment.newInstance(DoneFragment.DONE_TYPE_REGISTER), DONE_FRAGMENT_TAG, false);
    }

    @Override
    public void sendVerificationEmail(int verificationType) {

        EmailVerificationActivity.Action action = null;
        String title = "";
        String body = "";
        String chooserTitle = "";

        switch (verificationType) {
            case EMAIL_VERIFICATION_ACTION_LOGIN:
                action = EmailVerificationActivity.Action.LOGIN;
                title = getString(R.string.login_login_email_intent_subject);
                body = getString(R.string.login_login_email_intent_body);
                chooserTitle = getString(R.string.login_login_email_intent_app_chooser_title);
                break;
            case EMAIL_VERIFICATION_ACTION_REGISTER:
                action = EmailVerificationActivity.Action.SIGN_UP;
                title = getString(R.string.login_register_email_intent_subject);
                body = getString(R.string.login_register_email_intent_body);
                chooserTitle = getString(R.string.login_register_email_intent_app_chooser_title);
                break;
            default:
                Log.d(LOG_TAG, "sendVerificationEmail - verificationType invalid");
                break;
        }

        if (action != null) {
            Intent emailIntent = createSendEmailIntent(action, title, body);
            startActivity(generateCustomIntentChooser(emailIntent, chooserTitle));
        }
    }

    @Override
    public void saveTempUserData(UserRegistrationData userRegistrationData) {
        SharedPreferences.Editor editor = mPreference.edit();
        String json = gson.toJson(userRegistrationData);
        editor.putString(PREFERENCE_REGISTRATION_JSON_DATA, json).apply();
    }

    @Override
    public UserRegistrationData getTempUserData() {
        String json = mPreference.getString(PREFERENCE_REGISTRATION_JSON_DATA, null);
        UserRegistrationData userRegistrationData = gson.fromJson(json, UserRegistrationData.class);
        return userRegistrationData;
    }

    @Override
    public void clearTempUserData() {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void afterDoneAction() {

        Intent intent = new Intent(this, HomeWrapperActivity.class);
        intent.putExtra(HomeWrapperActivity.INTENT_KEY_OPEN_URL, "/Voucher");
        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        if (doneFragmentIsVisible()) {
            afterDoneAction();
        } else {
            super.onBackPressed();
        }
    }

}
