import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isak.prytz on 26/02/15 Week: 9.
 */
public class BaseActivity extends AppcustomerActionbarActivity {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    public static final String CONSUMER_EMAIL_KEY = "consumer_email";
    public static final String CONSUMER_VERIFICATION_TOKEN_KEY = "consumer_verification_token";
    public static final String START_ACTIVITY_AS_KEY = "start_activity_as";
    public static final String START_ACTIVITY_AS_REGISTRATION_EMAIL_RECEIVED = "registration_email_received";
    public static final String START_ACTIVITY_AS_LOGIN_EMAIL_RECEIVED = "login_email_received";
    public static final String START_ACTIVITY_AS_UPDATE_EMAIL_RECEIVED = "update_email_received";
    protected static final String BACK_STACK_TAG = "to_send_email_fragment";
    protected static final String DONE_FRAGMENT_TAG = "done_fragment_tag";


    private final String[] isAllowedSharePackage = {"mail", "com.google.android.gm"};

    protected Intent createSendEmailIntent(EmailVerificationActivity.Action action, final String subject, final String body) {

        final Intent emailIntent = new Intent();
        final String emailadress = aprovider.getInstance().getSettingsManager().getEmailVerificationAddress(action);
                
        emailIntent.setAction(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.fromParts("mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                   
        return emailIntent;
    }

    protected Intent generateCustomIntentChooser(Intent intentToShare, String chooserTitle) {

        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent chooserIntent;
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intentToShare, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {

                String packageName = resolveInfo.activityInfo.packageName;
                if (isAllowedSharePackage(packageName)) {

                    Intent targetedShareIntent = (Intent) intentToShare.clone();
                    targetedShareIntent.setPackage(packageName);
                    targetedShareIntent.setClassName(packageName, resolveInfo.activityInfo.name);
                    targetedShareIntents.add(targetedShareIntent);
                }
            }
        }

        if (targetedShareIntents.size() > 0) {
            chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
            return chooserIntent;
        } else {
            return Intent.createChooser(intentToShare, chooserTitle);
        }
    }


    protected boolean isAllowedSharePackage(String packageName) {
        if (packageName != null) {
            for (String allowedPackage : isAllowedSharePackage) {
                if (packageName.contains(allowedPackage)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean doneFragmentIsVisible() {
        Fragment doneFragment = getSupportFragmentManager().findFragmentByTag(DONE_FRAGMENT_TAG);
        return doneFragment != null && doneFragment.isVisible();
    }


}
