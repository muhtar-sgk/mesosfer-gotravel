package org.saungit.gotravel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferObject;
import com.eyro.mesosfer.MesosferQuery;
import com.eyro.mesosfer.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.saungit.gotravel.app.Config;
import org.saungit.gotravel.app.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailTourismActivity extends AppCompatActivity {
    private TextView textAddress;
    private TextView textDescription;
    private TextView textContact;
    private TextView textCost;
    private TextView textSchedule;
    private TextView textFacilities;
    private ImageView imageView;

    private String strTourism;
    private String strAddress;
    private String strDescription;
    private String strCost;
    private String strSchedule;
    private String strFacilities;
    private String strContact;
    private String strLg;
    private String strLt;
    private String strUrl;
    private String strImage;
    private SessionManager sessionManager;

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private String strName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animate_toolbar);

        sessionManager = new SessionManager(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = NavUtils.getParentActivityIntent(DetailTourismActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(DetailTourismActivity.this, intent);
            }
        });

        Intent intent = getIntent();
        strTourism = intent.getStringExtra(Config.NAME_TRAVEL);
        strAddress = intent.getStringExtra(Config.ADDRESS);
        strDescription = intent.getStringExtra(Config.DESCRIPTION);
        strFacilities = intent.getStringExtra(Config.FACILITIES_TRAVEL);
        strContact = intent.getStringExtra(Config.CONTACT_TRAVEL);
        strImage = intent.getStringExtra(Config.PHOTO);
        strCost = intent.getStringExtra(Config.COST);
        strSchedule = intent.getStringExtra(Config.SCHEDULE);
        strLg = intent.getStringExtra(Config.LG);
        strLt = intent.getStringExtra(Config.LT);
        strUrl = intent.getStringExtra(Config.URL_TRAVEL);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(strTourism);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));

        textAddress = (TextView) findViewById(R.id.text_address);
        textDescription = (TextView) findViewById(R.id.text_description);
        textContact = (TextView) findViewById(R.id.text_contact);
        textCost = (TextView) findViewById(R.id.text_cost);
        textSchedule = (TextView) findViewById(R.id.text_schedule);
        textFacilities = (TextView) findViewById(R.id.text_facilities);
        imageView = (ImageView) findViewById(R.id.image_travel);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        textAddress.setText(strAddress);
        textDescription.setText(strDescription);
        textContact.setText(strContact);
        textCost.setText(strCost);
        textSchedule.setText(strSchedule);
        textFacilities.setText(strFacilities);

        Picasso.with(this)
                .load(strImage)
                .error(R.drawable.background)
                .placeholder(R.drawable.background)
                .resize(250, 200)
                .into(imageView);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + strTourism);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(DetailTourismActivity.this, "please install google map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.comment) {
            Intent intent = new Intent(DetailTourismActivity.this, CommentActivity.class);
            intent.putExtra(Config.NAME_TRAVEL, strTourism);
            startActivity(intent);
            return true;
        } else if (id == R.id.share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, strUrl);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

