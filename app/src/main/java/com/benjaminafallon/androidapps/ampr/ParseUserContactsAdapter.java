package com.benjaminafallon.androidapps.ampr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ParseUserContactsAdapter extends ArrayAdapter<PhoneContact> {


    public ParseUserContactsAdapter(Context context, ArrayList<PhoneContact> contactsArrayList) {
        //super(context, android.R.layout.simple_list_item_1, nutrientsArrayList);
        super(context, 0, contactsArrayList);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final PhoneContact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parse_user_contacts_row, parent, false);
        }

        // Lookup view for data population
        TextView contactName = (TextView) convertView.findViewById(R.id.parseUserContactsNameTextView);
        TextView contactNumber = (TextView) convertView.findViewById(R.id.parseUserContactsNumberTextView);

        // Populate the data into the template view using the data object
        contactName.setText(contact.getContactName());
        contactNumber.setText(contact.getContactNumber());

        // Return the completed view to render on screen
        return convertView;
    }

}