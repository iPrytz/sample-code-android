package appcustomer.country.login.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.aprovider.sdk.consumer.EmailVerificationActivity;
import appcustomer.country.login.activity.BaseActivity;
import appcustomer.country.login.activity.LoginActivity;
import appcustomer.country.login.activity.ProfileActivity;

/**
 * Created by isak.prytz on 13/02/15 Week: 7.
 */
public class EmailVerificationReceiver extends BroadcastReceiver {

    private final String LOG_TAG = EmailVerificationReceiver.class.getSimpleName();
    private String mConsumerEmail;
    private String mVerificationToken;
    private Context mContext;

    public EmailVerificationReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getBooleanExtra(EmailVerificationActivity.KEY_SUCCESS, false)) {

            mConsumerEmail = intent.getStringExtra(EmailVerificationActivity.KEY_EMAIL);
            mVerificationToken = intent.getStringExtra(co.aprovider.sdk.consumer.EmailVerificationActivity.KEY_VERIFICATION_TOKEN);
            mContext = context;
            String action = intent.getStringExtra(EmailVerificationActivity.KEY_ACTION);

            Log.d(LOG_TAG, ">>> \n mVerificationToken = " + mVerificationToken
                    + " ,\n mConsumerEmail = " + mConsumerEmail
                    + " ,\n action = " + action);

            if (EmailVerificationActivity.Action.SIGN_UP.equals(action)) {
                Log.d(LOG_TAG, "SIGN_UP action received");

                startActivity(BaseActivity.START_ACTIVITY_AS_REGISTRATION_EMAIL_RECEIVED);


            } else if (EmailVerificationActivity.Action.LOGIN.equals(action)) {
                Log.d(LOG_TAG, "LOGIN action received");

                startActivity(BaseActivity.START_ACTIVITY_AS_LOGIN_EMAIL_RECEIVED);

            } else if (EmailVerificationActivity.Action.UPDATE_EMAIL.equals(action)) {
                Log.d(LOG_TAG, "UPDATE_EMAIL action received");

                startActivity(BaseActivity.START_ACTIVITY_AS_UPDATE_EMAIL_RECEIVED);

            }
        } else {
            Log.d(LOG_TAG, "Something went wrong when reading the deep link");

        }

    }

    private void startActivity(String startActivityAs) {
        if (mContext != null && mConsumerEmail != null && mVerificationToken != null) {

            mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()));

            Intent startActivity;
            if (startActivityAs.equals(BaseActivity.START_ACTIVITY_AS_UPDATE_EMAIL_RECEIVED)) {
                startActivity = new Intent(mContext, ProfileActivity.class);
            } else {
                startActivity = new Intent(mContext, LoginActivity.class);
            }
            startActivity.putExtra(BaseActivity.CONSUMER_EMAIL_KEY, mConsumerEmail);
            startActivity.putExtra(BaseActivity.CONSUMER_VERIFICATION_TOKEN_KEY, mVerificationToken);
            startActivity.putExtra(BaseActivity.START_ACTIVITY_AS_KEY, startActivityAs);
            startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(startActivity);
        }
    }

}