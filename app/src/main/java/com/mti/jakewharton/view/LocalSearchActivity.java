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
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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
import com.mti.jakewharton.adapter.ContactsAdapterFilterable;
import com.mti.jakewharton.network.ApiClient;
import com.mti.jakewharton.network.ApiService;
import com.mti.jakewharton.network.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalSearchActivity extends AppCompatActivity implements  ContactsAdapterFilterable.ContactsAdapterListener{

    @BindView(R.id.recycler_view)
            RecyclerView mRecyclerView;

    ContactsAdapterFilterable mContactsAdapterFilterable;

    List<Contact> mContactList=new ArrayList<>();

    @BindView(R.id.input_search)
    EditText mEditText;

    /*for disposing all Observer at a single time*/
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    ApiService apiService;
    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        unbinder= ButterKnife.bind(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mContactsAdapterFilterable=new ContactsAdapterFilterable(this,mContactList,this);

        mRecyclerView.setAdapter(mContactsAdapterFilterable);

        /*calling api*/

        apiService = ApiClient.getClient().create(ApiService.class);

        //todo: fetch Contacts and Update Recycler Adapter
        // source: `gmail` or `linkedin`
        // fetching all contacts on app launch
        // only gmail will be fetched
          fetchContacts("gmail");


          //Search contacts along with textInput changes and filter adapter data
        searchFilter();

    }
/*Filter the adapter Element*/
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
                        mContactsAdapterFilterable.getFilter().filter(textViewTextChangeEvent.text());
                    }

                    @Override
                    public void onError(Throwable e) {
                      Log.d("" +getClass().getName(), ""+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    private void fetchContacts(String source) {
        mCompositeDisposable.add( apiService.getContacts(source,null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Contact>>() {
                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        mContactList.clear();
                        mContactList.addAll(contacts);
                        mContactsAdapterFilterable.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("" +getClass().getName(), "Data can't fetch");
                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mCompositeDisposable.clear();
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
