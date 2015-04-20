import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by isak.prytz on 04/02/15 Week: 6.
 */

public class RegisterOrLoginFragment extends BaseFragment {

    private String LOG_TAG = RegisterOrLoginFragment.class.getSimpleName();
    private Button mLoginButton;
    private Button mRegisterButton;
    private LoginFragmentsListener mFragmentCallback;

    public static RegisterOrLoginFragment newInstance() {
        return new RegisterOrLoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_or_login_layout, null);

        mLoginButton = (Button) view.findViewById(R.id.register_or_login_login_button);
        mRegisterButton = (Button) view.findViewById(R.id.register_or_login_register_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.goToSendLoginEmailFragment();
            }
        });

        final UserInfoDBBean oldDb = UserInfoDB.select(getActivity());
        if(oldDb != null){
            mLoginButton.setEnabled(false);
            mRegisterButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    UserDBBean oldUserDb = UserDB.select(getActivity());

                    UserRegistrationData registrationData = new UserRegistrationData(
                            null,
                            null,
                            null,
                            Integer.parseInt(oldDb.sex) - 1,
                            convertDate(oldDb.birthDay),
                            Integer.parseInt(oldDb.jobkind) - 1,
                            Integer.parseInt(oldDb.childrenNumId) - 1,
							convertDate(oldDb.ygstBirthDay),
							Integer.parseInt(oldDb.mail1) == 1,
							Integer.parseInt(oldDb.mail2) == 1,
							Integer.parseInt(oldDb.mail3) == 1);

                    mFragmentCallback.saveTempUserData(registrationData);
                    mFragmentCallback.goToUpgradeFragment(oldDb.mailAddress, oldDb.memId, oldDb.mail1, oldDb.mail2, oldDb.mail3, oldUserDb.password, oldUserDb.userId);
                }

				private Date convertDate(String dateString) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

					try {
						return  dateFormat.parse(dateString);
					} catch (ParseException e) {
						e.printStackTrace();

						return new Date();
					}
				}

            });
        } else {
            mLoginButton.setEnabled(true);
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragmentCallback.goToRegisterFragment();
                }
            });
        }

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mFragmentCallback = (LoginFragmentsListener) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement RegisterOrLoginListener");
        }
    }

}
