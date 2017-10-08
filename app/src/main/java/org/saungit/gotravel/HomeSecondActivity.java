package org.saungit.gotravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.saungit.gotravel.app.Config;

public class HomeSecondActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.all_tourism:
                        AllTravelFragment allTravelFragment = new AllTravelFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame,allTravelFragment);
                        fragmentTransaction.commit();
                        setTitle(menuItem.getTitle());
                        return true;

                    case R.id.island:
                        IslandFragment islandFragment = new IslandFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.frame,islandFragment);
                        fragmentTransaction2.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.culinary:
                        CulinaryFragment culinaryFragment = new CulinaryFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.replace(R.id.frame,culinaryFragment);
                        fragmentTransaction3.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.beach:
                        BeachFragment beachFragment = new BeachFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction4.replace(R.id.frame,beachFragment);
                        fragmentTransaction4.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.zoo:
                        ZooFragment zooFragment = new ZooFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction5 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction5.replace(R.id.frame,zooFragment);
                        fragmentTransaction5.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.park:
                        ParkFragment parkFragment = new ParkFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction6 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction6.replace(R.id.frame,parkFragment);
                        fragmentTransaction6.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.mountain:
                        MountainFragment mountainFragment = new MountainFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction7 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction7.replace(R.id.frame,mountainFragment);
                        fragmentTransaction7.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    case R.id.about:
                        AboutFragment aboutFragment = new AboutFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction8 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction8.replace(R.id.frame,aboutFragment);
                        fragmentTransaction8.commit();
                        setTitle(menuItem.getTitle());
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        navigationView.getMenu().getItem(0).setChecked(true).getTitle();
        AllTravelFragment allTravelFragment = new AllTravelFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,allTravelFragment);
        fragmentTransaction.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,
                R.string.close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }
}
