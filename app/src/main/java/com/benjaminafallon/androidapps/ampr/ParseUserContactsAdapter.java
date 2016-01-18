package com.benjaminafallon.androidapps.ampr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ParseUserContactsAdapter extends ArrayAdapter<PhoneContact> {

    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    public static ArrayList<PhoneContact> selectedContacts = new ArrayList<PhoneContact>();

    public ParseUserContactsAdapter(Context context, ArrayList<PhoneContact> contactsArrayList) {
        //super(context, android.R.layout.simple_list_item_1, nutrientsArrayList);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parse_user_contacts_row, parent, false);
        }

        final CheckBox cBox = (CheckBox) convertView.findViewById(R.id.checkBox); // your
        // CheckBox
        cBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);

                //check and add contact to selectedContacts
                if (cb.isChecked()) {
                    itemChecked.set(position, true);
                    selectedContacts.add(contact);
                    Log.i("ADDING", "" + contact.getContactName());


                    //uncheck and remove contact from selectedContacts
                } else if (!cb.isChecked()) {
                    itemChecked.set(position, false);
                    // do some operations here
                    selectedContacts.remove(contact);
                    Log.i("REMOVING", "" + contact.getContactNumber());

                }
            }
        });

        cBox.setChecked(itemChecked.get(position));

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