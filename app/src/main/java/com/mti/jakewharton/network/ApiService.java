/*
 * Created by Tareq Islam on 3/3/19 12:04 AM
 *
 *  Last modified 3/3/19 12:04 AM
 */

package com.mti.jakewharton.network;

import com.mti.jakewharton.network.model.Contact;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/***
 * Created by mtita on 03,March,2019.
 */
public interface ApiService {

    @GET("contacts.php")
    Single<List<Contact>> getContacts(@Query("source") String source, @Query("search") String  query);
}
