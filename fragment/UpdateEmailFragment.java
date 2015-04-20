import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by isak.prytz on 16/02/15 Week: 8.
 */
public class UpdateEmailFragment extends BaseFragment {

    private static final String LOG_TAG = UpdateEmailFragment.class.getSimpleName();
    private static final String BUNDLE_VERIFICATION_TOKEN = "mVerificationToken";
    private static final String BUNDLE_CONSUMER_EMAIL = "mOldConsumerEmail";
    private static final String BUNDLE_EMAIL_SENT_FLAG = "mSentEmailFlag";

    public static final int SEND_VERIFICATION_EMAIL = 0;
    public static final int EMAIL_DEEP_LINK_WAS_CLICKED = 1;

    protected int mSentEmailFlag;

    private Button mUpdateEmailButton;
    private TextView mEmailInputText;
    private String mVerificationToken;
    private String mOldConsumerEmail;
    private String mNewConsumerEmail;
    private ProfileFragmentListener mFragmentCallback;

    public static UpdateEmailFragment newInstance(int sentEmailFlag) {
        return newInstance(sentEmailFlag, null, null);
    }


    public static UpdateEmailFragment newInstance(int sentEmailFlag, String consumerEmail, String verificationToken) {

        UpdateEmailFragment fragment = new UpdateEmailFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_EMAIL_SENT_FLAG, sentEmailFlag);
        if (consumerEmail != null) {
            args.putString(BUNDLE_CONSUMER_EMAIL, consumerEmail);
        }
        if (verificationToken != null) {
            args.putString(BUNDLE_VERIFICATION_TOKEN, verificationToken);
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSentEmailFlag = getArguments().getInt(BUNDLE_EMAIL_SENT_FLAG);
            mNewConsumerEmail = getArguments().getString(BUNDLE_CONSUMER_EMAIL);
            mVerificationToken = getArguments().getString(BUNDLE_VERIFICATION_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_update_email_layout, null);

        mUpdateEmailButton = (Button) view.findViewById(R.id.login_update_email_button);
        mEmailInputText = (TextView) view.findViewById(R.id.login_update_consumer_email);

        mUpdateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateEmailButton.setText(R.string.login_send_email_again_button);
                mFragmentCallback.sendUpdateVerificationEmail();

            }
        });

        countryUserConfiguration userConfig = DataProviders.get(countryUserConfiguration.class);
        countryUserModel model = userConfig.getUserConfig();


        if (model != null) {
            mOldConsumerEmail = model.getEmail();
            mEmailInputText.setText(mOldConsumerEmail);
        }


        if (mSentEmailFlag == SEND_VERIFICATION_EMAIL) {
            mUpdateEmailButton.setText(R.string.login_send_email_button);

        } else if (mSentEmailFlag == EMAIL_DEEP_LINK_WAS_CLICKED) {
            mUpdateEmailButton.setText(R.string.login_send_email_again_button);
            mUpdateEmailButton.setEnabled(false);

            ((appcustomerActionbarActivity) getActivity()).showLoader();
            updateEmail();
        }

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (ProfileFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginSendEmailFragmentListener");
        }
    }



    private void updateEmail() {


        if (mVerificationToken != null) {

            countryRegisterDataProvider registerDataProvider = DataProviders.get(countryRegisterDataProvider.class);

            registerDataProvider.updateEmail(mVerificationToken, mNewConsumerEmail, new DataProviderListener<SimpleResponse>() {
                @Override
                public void onSuccess(SimpleResponse result) {
                    ((appcustomerActionbarActivity)getActivity()).hideLoader();
                    if(result.getOk()){
                        mFragmentCallback.goToUpdateEmailDoneFragment();
                    } else {

                        mUpdateEmailButton.setEnabled(true);

                    }

                }

                @Override
                public void onFail(DataProviderError error) {

                    if (error instanceof AuthError){
                        showErrorDialog(R.string.login_error_change_email_error_title, R.string.login_error_change_email_error_auth);
                    } else if (error instanceof ConflictError){
                        showErrorDialog(R.string.login_error_change_email_error_title, R.string.login_error_change_email_error_already_used);
                    } else if (error instanceof DataError){
                        showErrorDialog(R.string.login_error_change_email_error_title, R.string.login_error_change_email_data_error);
                    } else {
                        showErrorDialog(R.string.login_error_change_email_error_title, R.string.core_error_dialog_no_internet);
                    }

                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    ((appcustomerActionbarActivity) getActivity()).hideLoader();
                    mUpdateEmailButton.setEnabled(true);
                }

            });

        } else {
            ((appcustomerActionbarActivity) getActivity()).hideLoader();
        }
    }

    public void showErrorDialog(final int errorTitleId, final int errorBodyId){

        final Activity activity = getActivity();

        if(activity != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle(errorTitleId)
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


}

