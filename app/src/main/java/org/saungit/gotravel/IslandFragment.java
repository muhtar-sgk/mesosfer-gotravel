package org.saungit.gotravel;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.eyro.mesosfer.FindCallback;
import com.eyro.mesosfer.MesosferData;
import com.eyro.mesosfer.MesosferException;
import com.eyro.mesosfer.MesosferQuery;

import org.json.JSONException;
import org.saungit.gotravel.app.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class IslandFragment extends Fragment {
    private ListView listView;
    private SimpleAdapter adapter;
    private ProgressDialog loading;
    private AlertDialog dialog;
    private List<Map<String, String>> mapDataList;
    private static final int[] to = new int[] { R.id.textName, R.id.textAddress};
    private static final String[] from = new String[] {"name","address"};
    private List<MesosferData> listData;
    private TextView textViewEmpty;


    public IslandFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_travel, container, false);

        mapDataList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.listview);
        textViewEmpty = (TextView)view.findViewById(R.id.emptyElement);
        adapter = new SimpleAdapter(getActivity(), mapDataList, R.layout.list_row_tourism, from, to);
        listView.setAdapter(adapter);

        loading = new ProgressDialog(getActivity());
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        updateAndShowDataList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MesosferData data = listData.get(i);
                Intent intent = new Intent(getActivity(), DetailTourismActivity.class);
                intent.putExtra(Config.NAME_TRAVEL, data.getDataString(Config.NAME_TRAVEL));
                intent.putExtra(Config.ADDRESS, data.getDataString(Config.ADDRESS));
                intent.putExtra(Config.DESCRIPTION, data.getDataString(Config.DESCRIPTION));
                intent.putExtra(Config.FACILITIES_TRAVEL, data.getDataString(Config.FACILITIES_TRAVEL));
                intent.putExtra(Config.CONTACT_TRAVEL, data.getDataString(Config.CONTACT_TRAVEL));
                intent.putExtra(Config.PHOTO, data.getDataString(Config.PHOTO));
                intent.putExtra(Config.LG, data.getDataString(Config.LG));
                intent.putExtra(Config.LT, data.getDataString(Config.LT));
                intent.putExtra(Config.COST, data.getDataString(Config.COST));
                intent.putExtra(Config.SCHEDULE, data.getDataString(Config.SCHEDULE));
                startActivity(intent);
            }
        });

        listView.setEmptyView(textViewEmpty);

        return view;
    }

    private void updateAndShowDataList() {
        MesosferQuery<MesosferData> query = MesosferData.getQuery("Travel");
        query.whereEqualTo("typeTravel","Pulau");

        // showing a progress dialog loading
        loading.setMessage("Please wait...");
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
                mapDataList.clear();
                listData = new ArrayList<>(list);
                for (final MesosferData data : list) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", data.getDataString("nameTravel"));
                    map.put("address", data.getDataString("address"));
                    try {
                        map.put("data", data.toJSON().toString(0));
                    } catch (JSONException e1) {
                        map.put("data", data.toJSON().toString());
                    }

                    mapDataList.add(map);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }


}
