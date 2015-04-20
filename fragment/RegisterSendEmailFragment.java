import android.util.Log;
import android.widget.Toast;

/**
 * Created by isak.prytz on 16/02/15 Week: 8.
 */

public class RegisterSendEmailFragment extends SendEmailBaseFragment {

    private static final String LOG_TAG = RegisterSendEmailFragment.class.getSimpleName();

    public static RegisterSendEmailFragment newInstance(int emailType, String consumerEmail, String verificationToken) {
        RegisterSendEmailFragment fragment = new RegisterSendEmailFragment();
        fragment.setArguments(getBundle(emailType, consumerEmail, verificationToken));
        return fragment;
    }

    public static RegisterSendEmailFragment newInstance(int emailType) {
        RegisterSendEmailFragment fragment = new RegisterSendEmailFragment();
        fragment.setArguments(getBundle(emailType, null, null));
        return fragment;
    }


    public RegisterSendEmailFragment() {
    }

    @Override
    protected void setUpFragmentText() {
        mExplainTextTitle.setText(R.string.login_send_email_for_register_explain_text_title);
        mExplainTextBody.setText(R.string.login_send_email_for_register_explain_text_body);
    }


    @Override
    protected void doFragmentAction() {
        ((appcustomerActionbarActivity) getActivity()).showLoader();
        register();
    }


    @Override
    void onEmailButtonClick() {
        mFragmentCallback.sendVerificationEmail(mFragmentCallback.EMAIL_VERIFICATION_ACTION_REGISTER);
    }


    private void register() {

        AppcustomerRegisterDataProvider registerDataProvider = DataProviders.get(AppcustomerRegisterDataProvider.class);
        UserRegistrationData userRegistrationData = mFragmentCallback.getTempUserData();

        if (userRegistrationData != null && mVerificationToken != null && mConsumerEmail != null) {

            registerDataProvider.signUp(
                    mVerificationToken,
                    mConsumerEmail,
                    userRegistrationData.getZipCode(),
                    userRegistrationData.getState(),
                    userRegistrationData.getRegion(),
                    userRegistrationData.getGender(),
                    userRegistrationData.getBirthDate(),
                    userRegistrationData.getJob(),
                    userRegistrationData.getChildNumber(),
                    userRegistrationData.getChildBirthDate(),
                    userRegistrationData.getCouponNews(),
                    userRegistrationData.getGiftNews(),
                    userRegistrationData.getProductNews(),
                    new DataProviderListener<SimpleResponse>() {

                        @Override
                        public void onSuccess(SimpleResponse result) {

                            if (result.getOk()) {

                                ((appcustomerActionbarActivity) getActivity()).hideLoader();

                                mFragmentCallback.clearTempUserData();
                                mFragmentCallback.goToRegisterDoneFragment();

                            } 

                        }

                        @Override
                        public void onFail(DataProviderError error) {

                            if (error instanceof AuthError){
                                showErrorDialog(R.string.login_error_register_error_title, R.string.login_error_register_error_auth);
                            } else if (error instanceof ConflictError){
                                showErrorDialog(R.string.login_error_register_error_title, R.string.login_error_register_error_already_used);
                            } else if (error instanceof DataError){
                                showErrorDialog(R.string.login_error_register_error_title, R.string.login_error_register_error_auth);
                            } else if (error instanceof FelicaAccessError || error instanceof FelicaGetAuthUrlError){
                                showFelicaInitErrorDialog(this);
                            } else {
                                showErrorDialog(R.string.login_error_register_error_title, R.string.core_error_dialog_no_internet);
                            }

                            mSendEmailButton.setEnabled(true);
                            if(getActivity() != null){
                                ((appcustomerActionbarActivity) getActivity()).hideLoader();
                            }

                        }
                    });

        } 
    }
}
