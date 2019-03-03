/*
 * Created by Tareq Islam on 3/3/19 12:01 AM
 *
 *  Last modified 3/2/19 10:34 PM
 */

package com.mti.jakewharton.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ravi on 31/01/18.
 */

public class Contact {

    String name;

    @SerializedName("image")
    String profileImage;

    String phone;
    String email;

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Checking contact equality against email
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Contact)) {
            return ((Contact) obj).getEmail().equalsIgnoreCase(email);
        }
        return false;
    }
}
