package ots.il.ac.shenkar.ots.apputiles;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ots.il.ac.shenkar.ots.controlers.AppController;

/**
 * Created by moshe on 08-03-16.
 */
public class GetContacts extends Thread {

    private Context context;
    private List<String> emails;
    private AppController appController;

    /**
     *CTOR
     * @param context
     */
    public GetContacts(Context context) {
        this.context = context;
        this.emails = new ArrayList<>();
        this.appController = new AppController(this.context);
    }

    /**
     * runnable
     */
    public void run() {
        String email;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,
                        null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                while (emailCursor.moveToNext()) {
                    email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                    emails.add(email);
                }
                emailCursor.close();
            }

        }
        appController.updateLocalEmailDB(emails);
    }

}