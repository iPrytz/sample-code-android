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

/**
 * Created by isak.prytz on 16/02/15 Week: 8.
 */

abstract class SendEmailBaseFragment extends BaseFragment {

    private static final String LOG_TAG = SendEmailBaseFragment.class.getSimpleName();
    protected static final String BUNDLE_SEND_EMAIL_TYPE = "mSendEmailType";
    protected static final String BUNDLE_VERIFICATION_TOKEN = "mVerificationToken";
    protected static final String BUNDLE_CONSUMER_EMAIL = "mConsumerEmail";

    protected int mSendEmailType;
    protected String mVerificationToken;
    protected String mConsumerEmail;

    protected LoginFragmentsListener mFragmentCallback;
    protected Button mSendEmailButton;
    protected TextView mExplainTextTitle;
    protected TextView mExplainTextBody;
    protected boolean mAlreadyLoaded = false;
    public static final int EMAIL_SEND_VERIFICATION_EMAIL = 0;
    public static final int EMAIL_DEEP_LINK_WAS_CLICKED = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSendEmailType = getArguments().getInt(BUNDLE_SEND_EMAIL_TYPE);
            mVerificationToken = getArguments().getString(BUNDLE_VERIFICATION_TOKEN);
            mConsumerEmail = getArguments().getString(BUNDLE_CONSUMER_EMAIL);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_send_email_layout, null);
        mSendEmailButton = (Button) view.findViewById(R.id.login_send_email_button);
        mExplainTextTitle = (TextView) view.findViewById(R.id.login_send_email_explain_text_title);
        mExplainTextBody = (TextView) view.findViewById(R.id.login_send_email_explain_text_body);

        setUpFragmentText();

        if (savedInstanceState == null && !mAlreadyLoaded) {
            mAlreadyLoaded = true;
            setUpFragmentAction();
        }

        return view;
    }


    protected static Bundle getBundle(int emailType, String consumerEmail, String verificationToken){
        Bundle args = new Bundle();
        args.putInt(BUNDLE_SEND_EMAIL_TYPE, emailType);
        if (consumerEmail != null && verificationToken != null) {
            args.putString(BUNDLE_CONSUMER_EMAIL, consumerEmail);
            args.putString(BUNDLE_VERIFICATION_TOKEN, verificationToken);
        }
        return args;
    }

    protected abstract void setUpFragmentText();


    private void setUpFragmentAction() {

        if (mSendEmailType == EMAIL_SEND_VERIFICATION_EMAIL) {
            mSendEmailButton.setText(R.string.login_send_email_button);
        } else if (mSendEmailType == EMAIL_DEEP_LINK_WAS_CLICKED) {
            mSendEmailButton.setText(R.string.login_send_email_again_button);
            mSendEmailButton.setEnabled(false);
            doFragmentAction();
        }
        mSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmailButtonClick();
                mSendEmailButton.setText(R.string.login_send_email_again_button);
            }
        });
    }


    protected abstract void doFragmentAction();


    abstract void onEmailButtonClick();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (LoginFragmentsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginSendEmailFragmentListener");
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
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mFragmentCallback.goToRegisterOrLoginFragment();
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }

    }

    public void showFelicaInitErrorDialog(final DataProviderListener<SimpleResponse> listener){
        final Activity activity = getActivity();
        if(activity != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.login_felica_init_error_title)
                            .setMessage(R.string.login_felica_init_error_message)
                            .setCancelable(false)
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
                                    mFragmentCallback.goToRegisterOrLoginFragment();
                                }
                            }).show();
                }
            });
        }
    }


}
