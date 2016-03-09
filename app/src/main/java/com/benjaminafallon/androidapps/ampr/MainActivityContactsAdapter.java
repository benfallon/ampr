package com.benjaminafallon.androidapps.ampr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityContactsAdapter extends ArrayAdapter<PhoneContact> {

    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    public static ArrayList<PhoneContact> selectedContacts = new ArrayList<PhoneContact>();
    private ParseUser currUser = ParseUser.getCurrentUser();
    ArrayList<String> deletedObjectId = new ArrayList<String>();


    public MainActivityContactsAdapter(Context context, ArrayList<PhoneContact> contactsArrayList) {
        super(context, 0, contactsArrayList);

        // initialize all items value with false
        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final PhoneContact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parse_user_contacts_row2, parent, false);
        }

        final ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteImageButton); // your

        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String idToRemove = MainActivity.startingActives.get(position).getContactObjectId();
                deletedObjectId.add(idToRemove);
                Log.i("removing objectId: ", " " + idToRemove);
                //remove active User from Parse database
                //currUser.removeAll("active", deletedObjectId);
                currUser.removeAll("active", Arrays.asList(idToRemove));
                currUser.saveInBackground();


                //remove active User from variable
                MainActivity.startingActives.remove(position);
                Log.i("startingActives size: ", MainActivity.startingActives.size() + ".");
                //MainActivity.globalActiveObjectIds.remove(position);
                notifyDataSetChanged();

                //Log.i("active.size(): " + currUser.get("active"));


            }
        });

        // Lookup view for data population
        TextView contactName = (TextView) convertView.findViewById(R.id.parseUserContactsNameTextView);
        TextView contactNumber = (TextView) convertView.findViewById(R.id.parseUserContactsNumberTextView);

        // Populate the data into the template view using the data object
        contactName.setText(contact.getContactName());
        contactNumber.setText(contact.getContactNumber());

        // Return the completed view to render on screen
        return convertView;
    }

    public static ArrayList<PhoneContact> getCheckedItems() {
        return selectedContacts;
    }

}