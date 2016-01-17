package com.benjaminafallon.androidapps.ampr;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RetrieveContactedUsersTask extends AsyncTask<String, Void, String> {

    private Activity activity;
    HashMap<String, String> contactsMap = new HashMap<>();
    String[] contactedUserNumbers;
    ListView contactsView;


    public RetrieveContactedUsersTask (Activity activity, ListView contactsView) {

        this.activity = activity;
        this.contactsView = contactsView;
    }

    @Override
    protected String doInBackground(String... params) {

        retrieveContactList();
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

        TreeMap<String, String> contactedUsersMap = new TreeMap<>();

        for (int i = 0; i < contactedUserNumbers.length; i++) {
            contactedUsersMap.put(contactsMap.get(contactedUserNumbers[i]), contactedUserNumbers[i]);
        }

        //contactsView.setAdapter(new ParseUserContactsAdapter(getContext(), contactedUsersMap));
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}

    public void retrieveContactList() {

        Cursor phones = null;

        try {
            phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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

    public void retrieveContactedUsers(Map<String, String> numbers) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("username", numbers.keySet());

        List<ParseUser> users= query.find();
        contactedUserNumbers = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            String value = users.get(i).getUsername();
            contactedUserNumbers[i] = value;
        }
    }
}