package ots.il.ac.shenkar.ots.dialogs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.ProcessingInstruction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.listeners.IGetCreatedUser;

/**
 * Created by moshe on 22-02-16.
 */
public class AddMemberDialog extends DialogFragment implements IGetCreatedUser {
    private EditText  mEPassword , mEName , mELName   ;
    private AutoCompleteTextView mEEmail;
    private Button mBtnCreate , mBtnCancel;
    private ParseUser parseUser;
    private User manager;
    private AppController appController;
    private String possibleEmail;
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.user_create_form_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parseUser = ParseUser.getCurrentUser();
        appController = new AppController(getActivity());
        manager = AppUtils.createUserFromPars(parseUser);
        mEName = (EditText) view.findViewById(R.id.id_add_dialog_name);
        mELName = (EditText) view.findViewById(R.id.id_add_member_dialog_last_user_name);
        mEEmail = (AutoCompleteTextView) view.findViewById(R.id.id_add_member_dialog_name_email);
        mEPassword = (EditText) view.findViewById(R.id.id_add_member_dialog_password);
        mBtnCancel = (Button) view.findViewById(R.id.id_add_member_cancel_button);
        mBtnCreate = (Button) view.findViewById(R.id.id_add_member_create_button);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,appController.getAllEmail() );
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mEEmail.setAdapter(adapter);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        getDialog().setTitle("Add new member");
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User retUser = createUser();
                if (retUser != null){
                    IGetCreatedUser createdUser = (IGetCreatedUser)getActivity();
                    createdUser.updateUserResult(retUser);
                    getDialog().dismiss();

                }
            }
        });
    }

    public User createUser(){
        boolean isEmpty = false;
        boolean isPassErr = false;
        User retUser = null;

        String name = mEName.getText().toString().trim();
        String lName = mELName.getText().toString().trim();
        String email = mEEmail.getText().toString().trim();
        String password = mEPassword.getText().toString().trim();

///check input from user
        if (name.equals("")){
            mEName.setError("Enter name");
            isEmpty = true;
        }
        if (lName.equals("")){
            mELName.setError("Enter last name");
            isEmpty = true;
        }
        if (email.equals("")){
            mEEmail.setError("Enter email");
            isEmpty = true;
        }
        if ( password.equals("")){
            mEPassword.setError("Enter password");
            isEmpty = true;
        }
        if ( password.length() < 6){
            mEPassword.setError("Password need to be at list 6 letters");
            isPassErr = true;
        }

        if (isEmpty == true) {
            AppUtils.Toast(getView().getContext(), AppConst.EMPTY_FIELDS);
//check if password is ok
        } else if(isPassErr == true){
            AppUtils.Toast(getView().getContext(), AppConst.PASS_ERROR);

        }else{

            AppUtils.Toast(getView().getContext(), AppConst.REGISTER_SUCCEED);
            retUser = new User();
            retUser.setUserName(name);
            retUser.setUserLName(lName);
            retUser.setMail(email);
            retUser.setPassword(password);
            retUser.setUserPhone(password);
            retUser.setIsManager(false);
            retUser.setManager(manager.getMail());
        }
        return retUser;
    }


    @Override
    public void updateUserResult(User user) {

    }}
