package com.benjaminafallon.androidapps.ampr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final int PICK_PARSE_CONTACT = 2015;

    public static ArrayList<PhoneContact> activeContacts = new ArrayList<PhoneContact>();
    ParseUser currUser;
    public static List<ParseUser> allParseContacts = new ArrayList<ParseUser>();
    public static ArrayList<PhoneContact> startingActives;

    MainActivityContactsAdapter listviewAdapter;
    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("onCreate:", "in onCreate");

        startingActives = new ArrayList<PhoneContact>();

        currUser = ParseUser.getCurrentUser();
        //Log.i("currUser ==", "" + currUser.getUsername());

        //get reference to "Free?" toggle switch
        Switch freeSwitch = (Switch) findViewById(R.id.freeSwitch);
        //initialize switch to state saved in Parse database
        freeSwitch.setChecked(currUser.getBoolean("isFree"));
        freeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set boolean on Parse User to true; this will be used when checking if any friends you selected are also free
                    currUser.put("isFree", true);
                    // TO-DO: check all contacts for matches, so that if you open the app and
                    // toggle on your availability you can get notifications the same way you can
                    // when selecting contacts
                }
                else {
                    currUser.put("isFree", false);
                }
                currUser.saveInBackground();
            }
        });

        try {
            currUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> activeObjectIds;
        try {
            //1.) fetch objectIds from active property on User object in Parse
            activeObjectIds = (ArrayList<String>) (currUser.get("active"));

            //Log.i("activeObjectIds: ", activeObjectIds.size() + ".");

            //2.) get Users with those objectIds
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereContainedIn("objectId", activeObjectIds);
            List<ParseUser> initUsers = null;
            initUsers = userQuery.find();

            //3.) for each User with an objectId in current User's actives array, create a Phone Contact object
            //  This allows the user's information to be displayed in the listview
            for (ParseUser p: initUsers) {
                startingActives.add(new PhoneContact(p.getString("name"), p.getString("phone"), p.getObjectId()));
                //Log.i("startingActives: ", " " + p.getObjectId());
            }
        }
        catch (NullPointerException e) {
            System.err.println("Array 'active' is null, could not fetch data.");
            e.printStackTrace();
        }
        catch (ParseException e) {
            System.err.println("Could not fetch data from Parse.");
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("An unknown error occurred.");
            e.printStackTrace();
        }

        //Display listview of PhoneContacts in startingActives (derived from ParseUsers in initUsers)
        listviewAdapter = new MainActivityContactsAdapter(this, startingActives);
        mainListView = (ListView) findViewById(R.id.parse_user_contacts_listview);
        mainListView.setAdapter(listviewAdapter);

        //set-up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set-up FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SelectContactsActivity.class);
                startActivityForResult(i, PICK_PARSE_CONTACT);
            }
        });

        //set-up Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume:", "in onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart:", "in onStart");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PARSE_CONTACT && resultCode == RESULT_OK) {
            activeContacts = (ArrayList<PhoneContact>) data.getSerializableExtra("selections");
            //add selected users to active array in Parse database
            ArrayList<String> activeObjectIds = new ArrayList<String>();
            String currentObjectId;

            for (int i = 0; i < activeContacts.size(); i++) {
                boolean alreadyPresent = false;
                //for each newly-selected contact, only add to listview (startingActives) if not already present
                for (int j = 0; j < startingActives.size(); j++) {
                    //Only add selected contacts to the listview if they are not already in the listview
                    if (activeContacts.get(i).getContactObjectId().equals(startingActives.get(j).getContactObjectId())) {
                        alreadyPresent = true;
                    }
                }
                if (!alreadyPresent) {
                    startingActives.add(activeContacts.get(i));
                }

                //add newly-selected object ids to active array if not already in array
                currentObjectId = activeContacts.get(i).getContactObjectId();
                activeObjectIds.add(currentObjectId);
                currUser.addAllUnique("active", activeObjectIds);
                currUser.saveInBackground();
            }
            listviewAdapter.notifyDataSetChanged();

            //find ParseUsers that the current user has selected; i.e. the "active" ParseUsers for that user
            //once found, these users will have their active arrays searched for the current user, signaling a match
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContainedIn("objectId", activeObjectIds);
            List<ParseUser> selectedUsers = new ArrayList<ParseUser>();
            try {
                selectedUsers = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //selectedUsers includes all Parse Users whose objectId is in activeObjectIds, which reflects the current Contacts in the ListView
            //For each of the Parse Users in the ListView, search their active array, which includes the Parse Users THEY (not you) have selected
            for (int i = 0; i < selectedUsers.size(); i++) {
                ParseUser p = selectedUsers.get(i);

                //get selectedUser's list of objectId Strings
                ArrayList<String> otherUserActiveObjectIds = (ArrayList<String>) p.get("active");

                //for each objectId in the OTHER ParseUser's active array, check to see if it matches currUser's objectId
                //in this case, we have a match (both users let each other know they are free)
                //also check to make sure the OTHER ParseUser's isFree boolean is set to true (indicating they are free)
                //if this boolean is not set to true, the currUser should not be alerted that the otherUser is free, even if they are both
                //in each others' active array
                for (int j = 0; j < otherUserActiveObjectIds.size(); j++) {
                    if (p.getBoolean("isFree")) {
                        if ( otherUserActiveObjectIds.get(j).equals(currUser.getObjectId()) ) {
                            showMatchDialog(p.getString("name"), p.getString("phone"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_out) {
            ParseUser.logOut();
            Intent intent = new Intent(MainActivity.this,LoginDispatchActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMatchDialog(String name, final String phone) {
        AlertDialog.Builder matchDialog = new AlertDialog.Builder(this);
        matchDialog.setTitle(name);
        matchDialog.setMessage(name + " is free to Haang!");

        // Setting Positive "Yes" Btn
        matchDialog.setPositiveButton("Message",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", (String) theUser.get("phone"), null));
                        //sendIntent.putExtra("sms_body", "sample message here");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
                        //Log.i("Sending Intent", "Sending");
                    }
                }
        );
        // Setting Negative "NO" Btn
        matchDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // Show Alert Dialog
        matchDialog.show();
    }
}
