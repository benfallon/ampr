package com.benjaminafallon.androidapps.ampr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivityContactsAdapter extends ArrayAdapter<PhoneContact> {

    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    public static ArrayList<PhoneContact> selectedContacts = new ArrayList<PhoneContact>();
    private ParseUser currUser = ParseUser.getCurrentUser();

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
                MainActivity.activeContacts.remove(position);
                notifyDataSetChanged();
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