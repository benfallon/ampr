package com.benjaminafallon.androidapps.ampr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


//        Fragment parseUserContactsFrag = new ParseUserContactsFragment();
//        android.support.v4.app.FragmentManager fragmentManger = getSupportFragmentManager();
//        fragmentManger.beginTransaction().replace(R.id.gager2, parseUserContactsFrag).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                //Stores phone numbers of selected contacts, to be returned to PlusPage.java
                //ArrayList<String> contactSelections = adapter.getCheckedItems();
                ArrayList<PhoneContact> contactSelections = adapter.getCheckedItems();


                //returnIntent.putStringArrayListExtra("selections", contactSelections );
                returnIntent.putExtra("selections", contactSelections);
                //returnIntent.putExtra("result",theitems);
                setResult(RESULT_OK,returnIntent);
                finish();
                //SENDS RETURN INTENT TO PageSlidingTabStripFragment's onActivityResult method

//                result = ParseUserContactsAdapter.getCheckedItems();
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", result);
//                setResult(RESULT_OK, returnIntent);
//                finish();
            }
        });

        new LoadParseUserContactsTask().execute();


//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result",result);
//        setResult(RESULT_OK, returnIntent);
//        finish();
    }

    private class LoadParseUserContactsTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        ArrayList<PhoneContact> parseUserContactsList = new ArrayList<PhoneContact>();
        List<String> contactNumbersList = new ArrayList<String>();



        public LoadParseUserContactsTask() {
//            dialog = new ProgressDialog(SelectContactsActivity.this);
        }

        @Override
        protected void onPreExecute() {
//            dialog.setMessage("Loading Contacts...");
//            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            ListView parseUserContactsListView = (ListView) findViewById( R.id.parse_user_contacts_listview2 );

            //the following includes built-in layout XML files
            adapter = new ParseUserContactsAdapter(SelectContactsActivity.this, parseUserContactsList);
            parseUserContactsListView.setAdapter(adapter);

        }

        @Override
        protected Void doInBackground(Void... params) {

            retrieveContactList();

            return null;

//            user = ParseUser.getCurrentUser();
//
//            //get user's phone contacts that are stored in Parse
//            List<HashMap> contactsArray = user.getList("userContacts");
//
//            //get arrayList of user contact phone numbers
//            for (int t = 0; t < contactsArray.size(); t++) {
//                String cNumber = (String) contactsArray.get(t).get("contactNumber");
//                contactNumbersList.add(cNumber);
//            }
//
//            // Construct a ParseUser query that will find friends whose
//            // phone number is in the List contactNumbersList
//            ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery("_User");
//            friendQuery.whereContainedIn("phone", contactNumbersList);
//
//            // findObjects will return a list of ParseUsers that are friends with
//            // the current user
//            List<ParseObject> friendUsers = null;
//            try {
//                friendUsers = friendQuery.find();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            for (ParseObject friend : friendUsers) {
//                String currName = friend.getString("name");
//                String currNumber = friend.getString("phone");
//                parseUserContactsList.add(new PhoneContact(currName, currNumber));
//            }
//
//            return null;
        }

        public void retrieveContactList() {

            parseUserContactsList.add(new PhoneContact("Tomz", "1029384756"));

            ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
            query.whereEqualTo("foo", "bar");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        Log.d("TestObjects", "Retrieved " + scoreList.size() + " found");
                    } else {
                        Log.d("TestObjects", "Error: " + e.getMessage());
                    }
                }
            });

            Cursor phones = null;

            try {
                phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (phones.moveToNext())
                {
                    String _number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                    String _name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    //parseUserContactsList.add(new PhoneContact(_name, _number));
                    //contactsMap.put(_number, _name);
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

        public void retrieveContactedUsers(Map<String, String> numbers) throws ParseException {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            //query.whereContainedIn("username", numbers.keySet());
            query.whereContainedIn("phone", numbers.keySet());

            List<ParseUser> users= query.find();
            contactedUserNumbers = new String[users.size()];
            Log.i("size: " + users.size(), " ");
            for (int i = 0; i < users.size(); i++) {
                String value = users.get(i).getUsername();
                String phone_number = (String) users.get(i).get("phone");
                Log.i("name: " + value, "phone: " + phone_number);
                contactedUserNumbers[i] = value;
                //contactedUserNumbers[i] = phone_number;
                parseUserContactsList.add(new PhoneContact(value, phone_number));
            }
        }
        }
    }

