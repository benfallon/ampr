package com.benjaminafallon.androidapps.ampr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivityParseContactsAdapter extends ArrayAdapter<ParseUser> {

    private ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
    public static ArrayList<ParseUser> selectedContacts = new ArrayList<ParseUser>();
    private ParseUser currUser = ParseUser.getCurrentUser();

    public MainActivityParseContactsAdapter(Context context, ArrayList<ParseUser> contactsArrayList) {
        //super(context, android.R.layout.simple_list_item_1, nutrientsArrayList);
        super(context, 0, contactsArrayList);

        // initialize all items value with false
        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, false);
        }

        //selectedContacts.clear();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final ParseUser contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parse_user_contacts_row2, parent, false);
        }

        final ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteImageButton); // your
        deleteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //ArrayList<ParseUser> fromParse = new ArrayList<PhoneContact>();
                //fromParse = (ArrayList<ParseUser>) currUser.get("active");

                //Log.i("size before delete: " + MainActivity.activeContacts.size(), "" + fromParse.size());

                //currUser.removeAll("active", MainActivity.activeContacts);
                //Log.i("size before delete: " + MainActivity.activeContacts.size(), "" + fromParse.size());

                MainActivity.contactsWithParse.remove(position);
                notifyDataSetChanged();

                //currUser.addAllUnique("active", MainActivity.activeContacts);
                //notifyDataSetChanged();

                //fromParse = (ArrayList<ParseUser>) currUser.get("active");
                //Log.i("size after delete: (local) " + MainActivity.activeContacts.size(), "(Parse) " + fromParse.size());


            }
        });

        //cBox.setChecked(itemChecked.get(position));

        // Lookup view for data population
        TextView contactName = (TextView) convertView.findViewById(R.id.parseUserContactsNameTextView);
        TextView contactNumber = (TextView) convertView.findViewById(R.id.parseUserContactsNumberTextView);

        // Populate the data into the template view using the data object
        contactName.setText(contact.getString("name"));
        contactNumber.setText(contact.getString("phone"));

        // Return the completed view to render on screen
        return convertView;
    }

    public static ArrayList<ParseUser> getCheckedItems() {
        return selectedContacts;
    }

}