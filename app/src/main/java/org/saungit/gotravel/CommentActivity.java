package org.saungit.gotravel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.eyro.mesosfer.CountCallback;
import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;
import com.eyro.mesosfer.MesosferUser;
import com.eyro.mesosfer.RegisterCallback;
import com.eyro.mesosfer.SaveCallback;

import org.json.JSONException;
import org.saungit.gotravel.app.Config;
import org.saungit.gotravel.app.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class CommentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout mRevealView;
    private RelativeLayout relativeLayout;
    private boolean hidden = true;
    private Button btnOK;
    private Button btnCancel;
    private ListView listView;
    private SimpleAdapter adapter;
    private ProgressDialog loading;
    private AlertDialog dialog;
    private List<Map<String, String>> mapDataList;
    private static final int[] to = new int[]{R.id.textName, R.id.textAddress};
    private static final String[] from = new String[]{"name", "comment"};
    private List<MesosferData> listData;
    private EditText editTextComment;
    private String strName;
    private String strComment;
    private String strNameTravel;
    private SessionManager sessionManager;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mapDataList = new ArrayList<>();

        sessionManager = new SessionManager(this);

        listView = (ListView) findViewById(R.id.list_view_comment);
        adapter = new SimpleAdapter(this, mapDataList, R.layout.list_row_tourism, from, to);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        strNameTravel = intent.getStringExtra(Config.NAME_TRAVEL);

        loading = new ProgressDialog(CommentActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        btnOK = (Button) findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (editTextComment.getText().toString().trim().length() == 0){
                    Toast.makeText(view.getContext(), "Please type your comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
            }
        });

        editTextComment = (EditText) findViewById(R.id.edit_text_comment);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_comment);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
            }
        });

        updateAndShowDataList();

        countComment();
    }

    private void updateAndShowDataList() {
        MesosferQuery<MesosferData> query = MesosferData.getQuery("Comment");
        query.whereEqualTo(Config.NAME_TRAVEL, strNameTravel);

        // showing a progress dialog loading
        loading.setMessage("Please Wait...");
        loading.show();

        query.findAsync(new FindCallback<MesosferData>() {
            @Override
            public void done(List<MesosferData> list, MesosferException e) {
                // hide progress dialog loading
                loading.dismiss();

                // check if there is an exception happen
                if (e != null) {
                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setNegativeButton(android.R.string.ok, null);
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }

                // clear all data list
                mapDataList.clear();
                listData = new ArrayList<>(list);
                for (final MesosferData data : list) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", data.getDataString("name"));
                    map.put("comment", data.getDataString("comments"));
                    try {
                        map.put("data", data.toJSON().toString(1));
                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }

                    mapDataList.add(map);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addComment() {
        // showing a progress dialog loading
        loading.setMessage("Please wait...");
        loading.show();

        strComment = editTextComment.getText().toString().trim();

        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetails();
        strName = user.get(SessionManager.KEY_EMAIL);

        // create new instance of Mesosfer User
        MesosferData data = MesosferData.createData("Comment");
        // set default field
        data.setData("name", strName);
        data.setData("comments", strComment);
        data.setData(Config.NAME_TRAVEL, strNameTravel);

        // execute register user asynchronous
        data.saveAsync(new SaveCallback() {
            @Override
            public void done(MesosferException e) {
                loading.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                builder.setNegativeButton(android.R.string.ok, null);
                // check if there is an exception happen
                if (e != null) {
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    dialog = builder.show();
                    return;
                }
                Toast.makeText(CommentActivity.this, "Thanks for comment", Toast.LENGTH_SHORT).show();
                //setResult(Activity.RESULT_OK);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    private void countComment() {
        MesosferQuery<MesosferData> query = MesosferData.getQuery("Comment");
        query.whereEqualTo(Config.NAME_TRAVEL, strNameTravel);
        query.countAsync(new CountCallback() {
            @Override
            public void done(int i, MesosferException e) {
                if (e == null) {
                    if (i==0){
                        toolbar.setTitle("No Comment");
                    } else if (i==1) {
                        toolbar.setTitle(i + " Comment");
                    } else {
                        toolbar.setTitle(i + " Comments");
                    }
                } else {
                    Toast.makeText(CommentActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_comment) {
            if (sessionManager.isLoggedIn()) {
                int cx = (mRevealView.getLeft() + mRevealView.getRight());
//                int cy = (mRevealView.getTop() + mRevealView.getBottom())/2;
                int cy = mRevealView.getTop();

                int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(800);

                    SupportAnimator animator_reverse = animator.reverse();

                    if (hidden) {
                        mRevealView.setVisibility(View.VISIBLE);
                        animator.start();
                        hidden = false;
                    } else {
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;

                            }

                            @Override
                            public void onAnimationCancel() {

                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                        animator_reverse.start();

                    }
                } else {
                    if (hidden) {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                        mRevealView.setVisibility(View.VISIBLE);
                        anim.start();
                        hidden = false;

                    } else {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mRevealView.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                        });
                        anim.start();

                    }
                }
            } else {
                startActivity(new Intent(CommentActivity.this, LoginActivity.class));
            }

            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
