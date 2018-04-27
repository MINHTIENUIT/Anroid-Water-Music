package com.example.minhtien.watermusic;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastConnection;
import com.example.minhtien.watermusic.BroadcastReceiver.BroadcastEnable;
import com.example.minhtien.watermusic.Connection.ManagerConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DevicesFragment.OnFragmentInteractionListener,
        ControllerFragment.OnFragmentInteractionListener{
    private static final int REQUEST_CODE = 0;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;

    private DevicesFragment devicesFragment;
    private ControllerFragment controllerFragment;
    private ProgressDialog pd;

    final BroadcastConnection broadcastConnection = new BroadcastConnection(new BroadcastConnection.ActionConnect() {
        @Override
        public void actionConnected(BluetoothDevice device) {
            devicesFragment.dismiss();
            controllerFragment = ControllerFragment.newInstance(devicesFragment.getDevice().getAddress(),"TODO");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,controllerFragment).commit();
        }

        @Override
        public void actionDisconnected() {
            if (pd != null){
                pd.dismiss();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,devicesFragment).commit();
        }
    });

    final BroadcastEnable broadcastEnable = new BroadcastEnable(new BroadcastEnable.InterfaceEnable() {
        @Override
        public void start() {
            devicesFragment.getDevicePared();
        }

        @Override
        public void stop() {
            devicesFragment.clearDevicePared();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        devicesFragment = DevicesFragment.newInstance(broadcastConnection,"TODO");

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,devicesFragment);
        fragmentTransaction.commit();

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastEnable,intentFilter);
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
        switch (id){
            case R.id.nav_turn_on:
                if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                }
                break;
            case R.id.nav_disconnect:
                if (ManagerConnection.isConnected()){
                    ManagerConnection.cancel();
                    pd = new ProgressDialog(this);
                    pd.setTitle("Disconneting");
                    pd.setMessage("Please wait");
                    pd.show();
                }
                break;
            case R.id.nav_devices:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,devicesFragment).commit();
                break;
            case R.id.nav_controller:
                if (ManagerConnection.isConnected())
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,controllerFragment).commit();
                break;
            case R.id.nav_manage:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Request Permission");
                    builder.setMessage("Permission Location denied.\nAre you allow this again?");
                    builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission();
                        }
                    });
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }
        }
    }

    public void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @Override
    public void onFragmentInteractionDevices(Uri uri) {

    }

    @Override
    public void onFragmentInteractionController(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ManagerConnection.isConnected()){
            unregisterReceiver(broadcastConnection);

        }
        unregisterReceiver(broadcastEnable);
    }
}
