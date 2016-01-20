package com.benjaminafallon.androidapps.ampr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BenFallon on 1/17/16.
 */
public class SelectContactsActivity extends AppCompatActivity {

    ParseUserContactsAdapter adapter;
    ParseUser user;
    String[] contactedUserNumbers;
    HashMap<String, String> contactsMap = new HashMap<>();
    ArrayList<PhoneContact> contactSelections = new ArrayList<PhoneContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                //Stores phone numbers of selected contacts, to be returned to MainActivity.java
                contactSelections = adapter.getCheckedItems();
                returnIntent.putExtra("selections", contactSelections);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
        new LoadParseUserContactsTask().execute();
    }

    private class LoadParseUserContactsTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        ArrayList<PhoneContact> parseUserContactsList = new ArrayList<PhoneContact>();

        public LoadParseUserContactsTask() {
            dialog = new ProgressDialog(SelectContactsActivity.this);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading Contacts...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            ListView parseUserContactsListView = (ListView) findViewById( R.id.parse_user_contacts_listview2 );

            //the following includes built-in layout XML files
            adapter = new ParseUserContactsAdapter(SelectContactsActivity.this, parseUserContactsList);

            parseUserContactsListView.setAdapter(adapter);

        }

        @Override
        protected Void doInBackground(Void... params) {
            retrieveContactList();
            return null;
        }

        //retrieve the user's contacts from their phone
        public void retrieveContactList() {

            parseUserContactsList.add(new PhoneContact("Tomz", "1029384756", "sample"));
            Cursor phones = null;

            try {
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (phones.moveToNext())
                {
                    String _number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                    String _name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactsMap.put(_number, _name);
                }
                phones.close();

            } catch ( Exception e ) {}

            finally {
                if(phones != null){
                    phones.close();
                }
            }

            try {
                retrieveContactedUsers(contactsMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //from the user's contacts, find those with a user account on Parse based on their phone number
        public void retrieveContactedUsers(Map<String, String> numbers) throws ParseException {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            //find ParseUsers whose phone numbers are in the user's Contacts List
            query.whereContainedIn("phone", numbers.keySet());

            List<ParseUser> users = query.find();
            //save list of contacts with parse to parseContacts static variable in MainActivity
            MainActivity.allParseContacts = users;

            contactedUserNumbers = new String[users.size()];

            Log.i("contacts with Parse: ", users.size() + " ");

            //add Contacts with Parse to parseUserContactsList to display in the 'Choose Contacts' ListView
            for (int i = 0; i < users.size(); i++) {
                String name = (String) users.get(i).get("name");
                String phone_number = (String) users.get(i).get("phone");
                String object_id = (String) users.get(i).getObjectId();
                contactedUserNumbers[i] = phone_number;
                parseUserContactsList.add(new PhoneContact(name, phone_number, object_id));
            }
        }
    }
}