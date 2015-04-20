import android.util.Log;

/**
 * Created by isak.prytz on 05/02/15 Week: 6.
 */

public class LoginSendEmailFragment extends SendEmailBaseFragment {

    private static final String LOG_TAG = LoginSendEmailFragment.class.getSimpleName();

    public static LoginSendEmailFragment newInstance(int emailType, String consumerEmail, String verificationToken) {
        LoginSendEmailFragment fragment = new LoginSendEmailFragment();
        fragment.setArguments(getBundle(emailType,consumerEmail,verificationToken));
        return fragment;
    }

    public static LoginSendEmailFragment newInstance(int emailType) {
        LoginSendEmailFragment fragment = new LoginSendEmailFragment();
        fragment.setArguments(getBundle(emailType,null,null));
        return fragment;
    }

    public LoginSendEmailFragment() {}


    @Override
    protected void setUpFragmentText() {
        mExplainTextTitle.setText(R.string.login_send_email_for_login_explain_text_title);
        mExplainTextBody.setText(R.string.login_send_email_for_login_explain_text_body);

    }


    @Override
    protected void doFragmentAction() {
        login();
    }


    @Override
    void onEmailButtonClick() {
        mFragmentCallback.sendVerificationEmail(mFragmentCallback.EMAIL_VERIFICATION_ACTION_LOGIN);
    }


    private void login() {

        ((AppcustomerActionbarActivity)getActivity()).showLoader();
        AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);
        registerDataProvider.loginUser(
                mVerificationToken,
                mConsumerEmail,
                new DataProviderListener<SimpleResponse>() {

                    @Override
                    public void onSuccess(SimpleResponse result) {

                            if(getActivity() != null){
                                ((appcustomerActionbarActivity)getActivity()).hideLoader();
                            }

                            mFragmentCallback.goToLoginDoneFragment();

                    }

                    @Override
                    public void onFail(DataProviderError error) {

                        if (error instanceof AuthError){
                            showErrorDialog(R.string.login_error_login_error_title, R.string.login_error_login_error_auth);
                        } else if (error instanceof DataError){
                            showErrorDialog(R.string.login_error_login_error_title, R.string.login_error_login_error_body);
                        } else if (error instanceof FelicaAccessError || error instanceof FelicaGetAuthUrlError){
                            showFelicaInitErrorDialog(this);
                        } else {
                            showErrorDialog(R.string.login_error_login_error_title, R.string.core_error_dialog_no_internet);
                        }

                        mSendEmailButton.setEnabled(true);
                        if(getActivity() != null){
                            ((AppcustomerActionbarActivity)getActivity()).hideLoader();
                        }

                    }
                });


    }
}
