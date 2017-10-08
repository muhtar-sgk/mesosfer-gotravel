package org.saungit.gotravel;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;
import com.eyro.mesosfer.SaveCallback;

import org.json.JSONException;
import org.saungit.gotravel.app.Config;
import org.saungit.gotravel.app.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

import static android.content.Context.ALARM_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {
    private List<Map<String, String>> mapDataList;
    private List<Map<String, String>> mapDataListPlan;
    private SimpleAdapter adapter;
    private static final int[] to = new int[]{R.id.textName, R.id.textAddress};
    private static final String[] from = new String[]{"name","dateTravel"};
    private AutoCompleteTextView autoNameTravel;
    private List<String> listNama = new ArrayList<String>();
    private Button btnDate;
    private Button btnTime;
    private TextView textDateTime;
    private Calendar mDateAndTime = Calendar.getInstance();
    private String date;
    private LinearLayout mRevealView;
    private SessionManager sessionManager;
    private boolean hidden = true;
    private RelativeLayout relativeLayout;
    private Button btnOK;
    private Button btnCancel;
    private ProgressDialog loading;
    private String strNameTravel;
    private String strName;
    private AlertDialog dialog;
    private ListView listViewPlan;
    private List<MesosferData> listData;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        sessionManager = new SessionManager(getActivity());

        listViewPlan = (ListView)view.findViewById(R.id.list_view_comment);

        loading = new ProgressDialog(getActivity());
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        autoNameTravel = (AutoCompleteTextView) view.findViewById(R.id.edit_text_comment);
        btnDate = (Button) view.findViewById(R.id.btn_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        mDateAndTime.set(Calendar.YEAR, year);
                        mDateAndTime.set(Calendar.MONTH, monthOfYear);
                        mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateAndTimeDisplay();
                    }
                };

                new DatePickerDialog(getActivity(), mDateListener,
                        mDateAndTime.get(Calendar.YEAR),
                        mDateAndTime.get(Calendar.MONTH),
                        mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnTime = (Button) view.findViewById(R.id.btn_time);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mDateAndTime.set(Calendar.MINUTE, minute);
                        updateDateAndTimeDisplay();
                    }
                };

                new TimePickerDialog(getActivity(), mTimeListener,
                        mDateAndTime.get(Calendar.HOUR_OF_DAY),
                        mDateAndTime.get(Calendar.MINUTE), true).show();
            }
        });

        textDateTime = (TextView) view.findViewById(R.id.set_time_date);

        mapDataList = new ArrayList<>();
        mapDataListPlan = new ArrayList<>();

        adapter = new SimpleAdapter(getActivity(), mapDataListPlan, R.layout.list_row_tourism, from, to);
        listViewPlan.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, listNama);
        autoNameTravel.setAdapter(arrayAdapter);
        autoNameTravel.setThreshold(1);

        mRevealView = (LinearLayout) view.findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.fragment_plan);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
            }
        });

        updateAndShowDataList();

        btnOK = (Button) view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
                addPlan();
            }
        });

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRevealView.setVisibility(View.INVISIBLE);
                hidden = true;
            }
        });

        getDataTravel();

        return view;
    }

    private void updateAndShowDataList() {
        MesosferQuery<MesosferData> query = MesosferData.getQuery("Plan");

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                //mapDataListPlan.clear();
                listData = new ArrayList<>(list);
                for (final MesosferData data : list) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", data.getDataString(Config.NAME_TRAVEL));
                    map.put("dateTravel", data.getDataString("dateTravel"));
                    try {
                        map.put("data", data.toJSON().toString(0));
                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }

                    mapDataListPlan.add(map);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateDateAndTimeDisplay() {
        date = DateUtils.formatDateTime(getActivity(),
                mDateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_TIME);
        textDateTime.setText(date);
    }

    private void getDataTravel() {
        MesosferQuery<MesosferData> query = MesosferData.getQuery("Travel");

        query.findAsync(new FindCallback<MesosferData>() {
            @Override
            public void done(List<MesosferData> list, MesosferException e) {
                if (e != null) {
                    // setup alert dialog builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setNegativeButton(android.R.string.ok, null);
                    builder.setTitle("Error Happen");
                    builder.setMessage(
                            String.format(Locale.getDefault(), "Error code: %d\nDescription: %s",
                                    e.getCode(), e.getMessage())
                    );
                    return;
                }

                // clear all data list
                mapDataList.clear();
                for (final MesosferData data : list) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", data.getDataString("nameTravel"));
                    String strNama = data.getDataString("nameTravel");
                    try {
                        map.put("nameTravel", data.toJSON().toString(0));
                    } catch (JSONException e1) {
                        map.put("nameTravel", data.toJSON().toString());
                    }

                    mapDataList.add(map);
                    listNama.add(strNama);
                }
            }
        });
    }

    public void addPlan() {
        loading.setMessage("Please wait...");
        loading.show();

        strNameTravel = autoNameTravel.getText().toString().trim();

        HashMap<String, String> user = sessionManager.getUserDetails();
        strName = user.get(SessionManager.KEY_EMAIL);

        // create new instance of Mesosfer User
        MesosferData data = MesosferData.createData("Plan");
        // set default field
        data.setData("name", strName);
        data.setData(Config.NAME_TRAVEL, strNameTravel);
        data.setData("dateTravel", date);

        // execute register user asynchronous
        data.saveAsync(new SaveCallback() {
            @Override
            public void done(MesosferException e) {
                loading.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                Toast.makeText(getActivity(), "Enjoy your plan", Toast.LENGTH_SHORT).show();
                getFragmentManager()
                        .beginTransaction()
                        .commit();
                adapter.notifyDataSetChanged();
            }
        });

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_plan, menu);
        MenuItem planItem = menu.findItem(R.id.action_plan);
        planItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
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
                return false;
            }
        });
    }


}
