package com.homenas.filepicker;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.homenas.filepicker.filter.CompositeFilter;
import com.homenas.filepicker.filter.PatternFilter;
import com.homenas.filepicker.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import static com.homenas.filepicker.Constant.ARG_CLOSEABLE;
import static com.homenas.filepicker.Constant.ARG_CURRENT_PATH;
import static com.homenas.filepicker.Constant.ARG_FILTER;
import static com.homenas.filepicker.Constant.ARG_START_PATH;
import static com.homenas.filepicker.Constant.ARG_TITLE;
import static com.homenas.filepicker.Constant.HANDLE_CLICK_DELAY;
import static com.homenas.filepicker.Constant.PERMISSIONS_REQUEST_CODE;
import static com.homenas.filepicker.Constant.RESULT_FILE_PATH;
import static com.homenas.filepicker.Constant.STATE_CURRENT_PATH;
import static com.homenas.filepicker.Constant.STATE_START_PATH;
import static com.homenas.filepicker.Constant.permission;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DirectoryFragment.FileClickListener {

    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;

    private CompositeFilter mFilter;
    private CharSequence mTitle;
    private Boolean mCloseable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        initArguments(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateTitle();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!mCurrentPath.equals(mStartPath)) {
                fm.popBackStack();
                mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
                updateTitle();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
        }else{
//            initArguments(savedInstanceState);
            initBackStackState();
            initFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBackStackState();
                    initFragment();
                } else {
                    showError();
                }
            }
        }
    }

    private void showError() {
        Toast.makeText(this, "Allow external storage reading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            mCurrentPath = clickedFile.getPath();
            if (mCurrentPath.equals("/storage/emulated"))
                mCurrentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            addFragmentToBackStack(mCurrentPath);
            updateTitle();
        } else {
            setResultAndFinish(clickedFile.getPath());
        }
    }

    private void initArguments(Bundle savedInstanceState) {
        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);

            if (filter instanceof Pattern) {
                ArrayList<FileFilter> filters = new ArrayList<>();
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }

        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
            updateTitle();
        } else {
            if (getIntent().hasExtra(ARG_START_PATH)) {
                mStartPath = getIntent().getStringExtra(ARG_START_PATH);
                mCurrentPath = mStartPath;
            }

            if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
                String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);

                if (currentPath.startsWith(mStartPath)) {
                    mCurrentPath = currentPath;
                }
            }
        }

        if (getIntent().hasExtra(ARG_TITLE)) {
            mTitle = getIntent().getCharSequenceExtra(ARG_TITLE);
        }

        if (getIntent().hasExtra(ARG_CLOSEABLE)) {
            mCloseable = getIntent().getBooleanExtra(ARG_CLOSEABLE, true);
        }
    }

    private void initFragment() {
        DirectoryFragment DF = new DirectoryFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.Container, DF.getInstance(mCurrentPath, mFilter));
        ft.addToBackStack(null);
        ft.commit();
    }

    private void initBackStackState() {
        String pathToAdd = mCurrentPath;
        ArrayList<String> separatedPaths = new ArrayList<>();

        while (!pathToAdd.equals(mStartPath)) {
            pathToAdd = FileUtils.cutLastSegmentOfPath(pathToAdd);
            separatedPaths.add(pathToAdd);
        }

        Collections.reverse(separatedPaths);

        for (String path : separatedPaths) {
            addFragmentToBackStack(path);
        }
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.Container, DirectoryFragment.getInstance(
                        path, mFilter))
                .addToBackStack(null)
                .commit();
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            String titlePath = mCurrentPath.isEmpty() ? "/" : mCurrentPath;
            if (TextUtils.isEmpty(mTitle)) {
                getSupportActionBar().setTitle(titlePath);
            } else {
                getSupportActionBar().setSubtitle(titlePath);
            }
        }
    }

    private void setResultAndFinish(String filePath) {
        Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        setResult(RESULT_OK, data);
        finish();
    }
}
