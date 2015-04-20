import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by isak.prytz on 05/02/15 Week: 6.
 */

public class DoneFragment extends BaseFragment {

    private DoneFragmentListener mFragmentCallback;
    public static final int DONE_TYPE_LOGIN = 0;
    public static final int DONE_TYPE_REGISTER = 1;
    public static final int DONE_TYPE_UPDATE_EMAIL = 2;
    private static final String BUNDLE_DONE_TYPE = "mFragmentDoneType";
    private int mFragmentDoneType;
    private Button mDoneButton;
    private TextView mExplainTextTitle;
    private TextView mExplainTextBody;


    public interface DoneFragmentListener{
        public void afterDoneAction();

    }

    public static DoneFragment newInstance(int fragmentDoneType) {
        DoneFragment fragment = new DoneFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_DONE_TYPE, fragmentDoneType);
        fragment.setArguments(args);
        return fragment;
    }

    public DoneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragmentDoneType = getArguments().getInt(BUNDLE_DONE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_done_layout, container, false);
        mDoneButton = (Button) view.findViewById(R.id.login_done_button);
        mExplainTextTitle = (TextView) view.findViewById(R.id.login_done_eplain_text_title);
        mExplainTextBody = (TextView) view.findViewById(R.id.login_done_explain_text_body);

        if(mFragmentDoneType == DONE_TYPE_LOGIN){
            setUpFragmentAsLoginDone();
        } else if(mFragmentDoneType == DONE_TYPE_REGISTER){
            setUpFragmentAsRegisterDone();
        }else if(mFragmentDoneType == DONE_TYPE_UPDATE_EMAIL){
            setUpFragmentAsUpdateDone();
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragmentCallback = (DoneFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginSendEmailFragmentListener");
        }
    }


    private void setUpFragmentAsLoginDone(){
        mExplainTextTitle.setText(R.string.login_login_done_explain_text_title);
        mExplainTextBody.setText(R.string.login_login_done_explain_text_body);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.afterDoneAction();
            }
        });

    }

    private void setUpFragmentAsRegisterDone(){
        mExplainTextTitle.setText(R.string.login_register_done_explain_text_title);
        mExplainTextBody.setText(R.string.login_register_done_explain_text_body);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.afterDoneAction();
            }
        });
    }

    private void setUpFragmentAsUpdateDone(){
        mExplainTextTitle.setText(R.string.update_email_done_explain_text_title);
        mExplainTextBody.setVisibility(View.GONE);
        mDoneButton.setText(R.string.update_email_done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentCallback.afterDoneAction();
            }
        });
    }


}
