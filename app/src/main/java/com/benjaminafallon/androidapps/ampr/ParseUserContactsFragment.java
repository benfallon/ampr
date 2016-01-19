package com.benjaminafallon.androidapps.ampr;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
 * Created by BenFallon on 9/20/14.
 */

public class ParseUserContactsFragment extends Fragment {

    View rootView;
    ParseUserContactsAdapter adapter;
    ParseUser user;
    String[] contactedUserNumbers;
    HashMap<String, String> contactsMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.content_main2, container, false);

        new LoadParseUserContactsTask().execute();

        return rootView;

    }

    private class LoadParseUserContactsTask extends AsyncTask<Void, Void, Void> {
       // private ProgressDialog dialog;
        ArrayList<PhoneContact> parseUserContactsList = new ArrayList<PhoneContact>();
        //List<String> contactNumbersList = new ArrayList<String>();


//        public LoadParseUserContactsTask() {
//            dialog = new ProgressDialog(getActivity());
//        }

        @Override
        protected void onPreExecute() {
           // dialog.setMessage("Loading Contacts...");
           // dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            ListView parseUserContactsListView = (ListView) rootView.findViewById( R.id.parse_user_contacts_listview2 );

            //the following includes built-in layout XML files
            adapter = new ParseUserContactsAdapter(getActivity(), parseUserContactsList);
            parseUserContactsListView.setAdapter(adapter);

        }

        @Override
        protected Void doInBackground(Void... params) {

            retrieveContactList();

            return null;
        }

        public void retrieveContactList() {

            parseUserContactsList.add(new PhoneContact("Tomz", "1029384756", "sample"));

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
                phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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
                parseUserContactsList.add(new PhoneContact(value, phone_number, "sample"));
            }
        }
    }
}