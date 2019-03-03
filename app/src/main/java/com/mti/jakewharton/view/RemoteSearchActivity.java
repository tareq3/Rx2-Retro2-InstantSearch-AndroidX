/*
 * Created by Tareq Islam on 3/3/19 12:10 AM
 *
 *  Last modified 3/3/19 12:10 AM
 */

package com.mti.jakewharton.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.mti.jakewharton.R;
import com.mti.jakewharton.adapter.ContactsAdapter;
import com.mti.jakewharton.adapter.ContactsAdapterFilterable;
import com.mti.jakewharton.network.ApiClient;
import com.mti.jakewharton.network.ApiService;
import com.mti.jakewharton.network.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteSearchActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener{
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    ContactsAdapter mContactsAdapter;

    List<Contact> mContactList=new ArrayList<>();

    @BindView(R.id.input_search)
    EditText mEditText;

    /*for disposing all Observer at a single time*/
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    /*as we want to continue observing from the perticular point when observer attached, we don't whanna know about what have been emitted before*/
    PublishSubject<String> mPublishSubject=PublishSubject.create();

    ApiService apiService;
    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         unbinder     = ButterKnife.bind(this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mContactsAdapter=new ContactsAdapter(this,mContactList,this);

        mRecyclerView.setAdapter(mContactsAdapter);

        /*calling api*/

        apiService = ApiClient.getClient().create(ApiService.class);

        //using switchMap overwrite or switch a previous request with new request
        switchGetContacts();

        // filter elements by publishing new element
        searchFilter();


        // passing empty string fetches all the contacts
        mPublishSubject.onNext("");

    }

    private void switchGetContacts() {
        mCompositeDisposable.add(mPublishSubject
                                    .debounce(300, TimeUnit.MILLISECONDS)
                                    .switchMapSingle(s-> apiService.getContacts(null, s)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread()))
                                    .subscribeWith(new DisposableObserver<List<Contact>>() {
                                        @Override
                                        public void onNext(List<Contact> contacts) {
                                            mContactList.clear();
                                            mContactList.addAll(contacts);
                                            mContactsAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e("" + getClass().getName(), "" + e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    }));
    }

    private void searchFilter() {
        mCompositeDisposable.add(
                RxTextView.textChangeEvents(mEditText)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<TextViewTextChangeEvent>() {
                            @Override
                            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                                Log.d("" +getClass().getName(), ""+textViewTextChangeEvent.text());

                                /*publish new element*/
                                mPublishSubject.onNext(textViewTextChangeEvent.text().toString());
                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.e("" +getClass().getName(), ""+e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(Contact contact) {

    }
}
