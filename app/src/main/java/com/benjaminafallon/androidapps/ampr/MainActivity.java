package com.benjaminafallon.androidapps.ampr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //public final int PICK_CONTACT = 2015;
    public final int PICK_PARSE_CONTACT = 2015;

    public static ArrayList<PhoneContact> activeContacts = new ArrayList<PhoneContact>();
    ParseUser currUser;
    public static List<ParseUser> allParseContacts = new ArrayList<ParseUser>();
    //public static List<String> activeObjectIds = new ArrayList<String>();
    public static ArrayList<PhoneContact> startingActives = new ArrayList<PhoneContact>();


    MainActivityContactsAdapter listviewAdapter;
    ListView mainListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startingActives.clear();
        Log.i("onCreate:", "in onCreate");
        currUser = ParseUser.getCurrentUser();
        Log.i("currUser ==", "" + currUser.getUsername() );

        try {
            currUser.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> activeObjectIds;
        try {
            activeObjectIds = (ArrayList<String>) (currUser.get("active"));
            Log.i("activeObjectIds: ", activeObjectIds.size() + ".");
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereContainedIn("objectId", activeObjectIds);
            List<ParseUser> initUsers = null;
            initUsers = userQuery.find();
            for (ParseUser p: initUsers) {
                startingActives.add(new PhoneContact(p.getString("name"), p.getString("phone"), p.getObjectId()));
                Log.i("startingActives: ", " " + p.getObjectId());
            }
            Log.i("sA size(): ", " " + startingActives.size());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PARSE_CONTACT && resultCode == RESULT_OK) {
            activeContacts = (ArrayList<PhoneContact>) data.getSerializableExtra("selections");
//            for (PhoneContact newlySelected: activeContacts) {
////                startingActives.add(newlySelected);
////                Log.i("startingActives size: ", startingActives.size() + ".");
//            }
            //listviewAdapter.notifyDataSetChanged();

            //mainListView.
            //MainActivityContactsAdapter listviewAdapter = new MainActivityContactsAdapter(this, activeContacts);

            //add selected users to active array in Parse database
            ArrayList<String> activeObjectIds = new ArrayList<String>();
            String currentObjectId;
            for (int i = 0; i < activeContacts.size(); i++) {
                startingActives.add(activeContacts.get(i));
                Log.i("startingActives size: ", startingActives.size() + ".");

                currentObjectId = activeContacts.get(i).getContactObjectId();
                activeObjectIds.add(currentObjectId);
                currUser.addUnique("active", activeObjectIds);
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

            Log.i("currUserObjectId: ", " " + currUser.getObjectId());
            for (int i = 0; i < selectedUsers.size(); i++) {
                ParseUser p = selectedUsers.get(i);
                Log.i("otherUser[" + i + "] ObjectId: ", " " + p.getObjectId());

                ArrayList<String> otherUserActiveObjectIds = (ArrayList<String>) p.get("active");

                //Log.i("active list size:", " " + otherUserActiveObjectIds.size());

                //for (String otherUserActiveSelection: otherUserActiveObjectIds) {

                for (int j = 0; j < otherUserActiveObjectIds.size(); j++) {
                    //Log.i("otherUserChoice: ", " " + otherUserActiveObjectIds.get(j).toString());
                    if (otherUserActiveObjectIds.get(j).equals(currUser.getObjectId())) {
                        Log.i("MATCH!", "MATCH! MATCH! MATCH!");

                        showMatchDialog(p.getString("name"));

                    }
                }

            }

//            ListView mainListView = (ListView) findViewById(R.id.parse_user_contacts_listview);
//            mainListView.setAdapter(listviewAdapter);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            ParseUser.logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMatchDialog(String name) {
        AlertDialog.Builder matchDialog = new AlertDialog.Builder(this);
        matchDialog.setTitle("It's a Match!");
        matchDialog.setMessage("You matched with " + name + "!");

        // Setting Positive "Yes" Btn
        matchDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
//
        // Setting Negative "NO" Btn
        matchDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        matchDialog.show();
    }
}
