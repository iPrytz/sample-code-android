/**
 * Created by isak.prytz on 10/02/15 Week: 7.
 */
public interface LoginFragmentsListener {

    public static final int EMAIL_VERIFICATION_ACTION_LOGIN = 0;
    public static final int EMAIL_VERIFICATION_ACTION_REGISTER = 1;

    public void goToRegisterOrLoginFragment();
    public void goToRegisterFragment();
    public void goToUpgradeFragment(String email, String memId, String mail1, String mail2, String mail3, String password, String userId);
    public void goToSendLoginEmailFragment();
    public void goToSendRegisterEmailFragment();
    public void sendVerificationEmail(int verificationType);
    public void saveTempUserData(UserRegistrationData userRegistrationData);
    public UserRegistrationData getTempUserData();
    public void clearTempUserData();
    public void goToRegisterDoneFragment();
    public void goToLoginDoneFragment();
}
