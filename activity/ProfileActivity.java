import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by *** on 30/11/14.
 * Modied by isak.prytz on 26/02/15 Week: 9.
 */
public class ProfileActivity extends BaseActivity implements ProfileFragmentListener, DoneFragment.DoneFragmentListener {


    protected static final String PROFILE_FRAGMENT_TAG = "profile_fragment_tag";
    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActivity();
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

        if (bundle != null && bundle.getString(START_ACTIVITY_AS_KEY).equals(START_ACTIVITY_AS_UPDATE_EMAIL_RECEIVED)) {
            getSupportFragmentManager().popBackStackImmediate(BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            replaceFragment(UpdateEmailFragment.newInstance(UpdateEmailFragment.EMAIL_DEEP_LINK_WAS_CLICKED, bundle.getString(CONSUMER_EMAIL_KEY), bundle.getString(CONSUMER_VERIFICATION_TOKEN_KEY)), true);
            setIntent(null);
        } else {
            replaceFragment(ProfileFragment.newInstance(), PROFILE_FRAGMENT_TAG, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (profileFragmentIsVisible()) {
                    return false;
                } else {
                    onBackPressed();
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToUpdateEmailFragment() {
        replaceFragment(UpdateEmailFragment.newInstance(UpdateEmailFragment.SEND_VERIFICATION_EMAIL), null, BACK_STACK_TAG, true);
    }


    @Override
    public void goToUpdateEmailDoneFragment() {
        getSupportFragmentManager().popBackStackImmediate(BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(DoneFragment.newInstance(DoneFragment.DONE_TYPE_UPDATE_EMAIL), DONE_FRAGMENT_TAG, false);
    }

    @Override
    public void sendUpdateVerificationEmail() {
        EmailVerificationActivity.Action action = EmailVerificationActivity.Action.UPDATE_EMAIL;
        String title = getString(R.string.login_update_email_intent_subject);
        String body = getString(R.string.login_update_email_intent_body);
        String chooserTitle = getString(R.string.login_update_email_intent_app_chooser_title);

        Intent emailIntent = createSendEmailIntent(action, title, body);
        startActivity(generateCustomIntentChooser(emailIntent, chooserTitle));
    }

    @Override
    public void afterDoneAction() {
        clearFragmentBackstack();
        replaceFragment(ProfileFragment.newInstance(), PROFILE_FRAGMENT_TAG, false);

    }

    @Override
    public void goBack() {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (doneFragmentIsVisible()) {
            afterDoneAction();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean profileFragmentIsVisible() {
        Fragment profileFragment = getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
        return profileFragment != null && profileFragment.isVisible();
    }
}
