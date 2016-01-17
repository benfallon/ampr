package com.benjaminafallon.androidapps.ampr;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by BenFallon on 9/24/14.
 */
public class PhoneContact implements Parcelable {

    private String contactName;
    private String contactNumber;

    public PhoneContact(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public PhoneContact(Parcel source) {
        readFromParcel(source);
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    //returns ParseUser if found; else, return null
//    public ParseUser getParseContact() {
//
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//
//
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactName);
        parcel.writeString(contactNumber);
    }

    private void readFromParcel(Parcel in) {
        this.contactName = in.readString();
        this.contactNumber = in.readString();
    }

    public static final Parcelable.Creator<PhoneContact> CREATOR = new Parcelable.Creator<PhoneContact>() {

        @Override
        public PhoneContact createFromParcel(Parcel source) {
            return new PhoneContact(source);
        }

        @Override
        public PhoneContact[] newArray(int size) {
            return new PhoneContact[size];
        }

    };

}
