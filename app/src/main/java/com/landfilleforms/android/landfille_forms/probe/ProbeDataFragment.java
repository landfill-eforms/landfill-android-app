package com.landfilleforms.android.landfille_forms.probe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.landfilleforms.android.landfille_forms.DatePickerFragment;
import com.landfilleforms.android.landfille_forms.R;
import com.landfilleforms.android.landfille_forms.database.dao.ProbeDao;
import com.landfilleforms.android.landfille_forms.model.ProbeData;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Work on 3/27/2017.
 */

public class ProbeDataFragment extends Fragment {
    private static final String EXTRA_LANDFILL_LOCATION = "com.landfilleforms.android.landfille_forms.landfill_location";
    private static final String ARG_PROBE_DATA_ID = "probe_data_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    private ProbeData mProbeData;

    private TextView mLocationLabel;
    private Spinner mProbeNumberSpinner;
    private TextView mInspectorLabel;
    private TextView mDateLabel;
    private TextView mBaroLevelField;
    private EditText mRemarksField;
    private EditText mWaterPressureField;
    private EditText mMethanePercentageField;
    private Button mSubmitButton;

    public static ProbeDataFragment newInstance(UUID probeDataId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROBE_DATA_ID, probeDataId);
        ProbeDataFragment fragment = new ProbeDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID probeDataId = (UUID) getArguments().getSerializable(ARG_PROBE_DATA_ID);
        mProbeData = ProbeDao.get(getActivity()).getProbeData(probeDataId);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        ProbeDao.get(getActivity()).updateProbeData(mProbeData);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_probe_data, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_probe_data:
                AlertDialog.Builder alertBuilder =  new AlertDialog.Builder(getActivity());
                dialogDeleteProbeEntry(alertBuilder);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_probe_data, container, false);

        mInspectorLabel = (TextView)v.findViewById(R.id.inspector_name);
        mInspectorLabel.setText(mProbeData.getInspectorName());

        mLocationLabel = (TextView)v.findViewById(R.id.location);
        mLocationLabel.setText(mProbeData.getLocation());

        mBaroLevelField = (TextView)v.findViewById(R.id.baro_reading);
        if(mProbeData.getBarometricPressure() != 0)
            mBaroLevelField.setText(Double.toString(mProbeData.getBarometricPressure()));

        mDateLabel = (TextView)v.findViewById(R.id.date_label);
        mDateLabel.setText(DateFormat.format("EEEE, MMM d, yyyy",mProbeData.getDate()));

        mProbeNumberSpinner = (Spinner)v.findViewById(R.id.probe_number_spinner);
        ArrayAdapter<CharSequence> adapter;
        switch(mProbeData.getLocation()) {
            case "Bishops":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.bishops_probe, R.layout.dark_spinner_layout);
                break;
            case "Gaffey":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gaffey_probe, R.layout.dark_spinner_layout);
                break;
            case "Lopez":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.lopez_probe, R.layout.dark_spinner_layout);
                break;
            case "Sheldon":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.sheldon_probe, R.layout.dark_spinner_layout);
                break;
            case "Toyon":
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.toyon_probe, R.layout.dark_spinner_layout);
                break;
            default:
                adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.empty_array, R.layout.dark_spinner_layout);;
        }
        mProbeNumberSpinner.setAdapter(adapter);
        mProbeNumberSpinner.setSelection(adapter.getPosition(mProbeData.getProbeNumber()));

        mProbeNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProbeData.setProbeNumber(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mWaterPressureField = (EditText)v.findViewById(R.id.water_pressure_field);
        if(mProbeData.getWaterPressure() != 0)
            mWaterPressureField.setText(Double.toString(mProbeData.getWaterPressure()));
        mWaterPressureField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s=="" || count == 0) mProbeData.setWaterPressure(0);
                else mProbeData.setWaterPressure(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMethanePercentageField = (EditText)v.findViewById(R.id.methane_percentage_field);
        if(mProbeData.getMethanePercentage() != 0)
            mMethanePercentageField.setText(Double.toString(mProbeData.getMethanePercentage()));
        mMethanePercentageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s=="" || count == 0) mProbeData.setMethanePercentage(0);
                else mProbeData.setMethanePercentage(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mRemarksField = (EditText)v.findViewById(R.id.remarks_field);
        mRemarksField.setText(mProbeData.getRemarks());
        mRemarksField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProbeData.setRemarks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSubmitButton = (Button)v.findViewById(R.id.submit);
        mSubmitButton.setText(R.string.submit_button_label);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(mProbeData.getMethanePercentage() < 0 || mProbeData.getMethanePercentage() >=100) {
                    Toast.makeText(getActivity(), R.string.improper_methane_percentage_toast, Toast.LENGTH_SHORT).show();
                }
                else {
                    getActivity().finish();
                    Toast.makeText(getActivity(), R.string.warmspot_added_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    //TODO: Delete later if not needed
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mProbeData.setDate(date);
            updateDate();
        }

    }

    private void updateDate() {
        mDateLabel.setText(DateFormat.format("EEEE, MMM d, yyyy",mProbeData.getDate()));
    }

    private void dialogDeleteProbeEntry(AlertDialog.Builder alertBuilder) {
        alertBuilder.setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProbeDao.get(getActivity()).removeProbeData(mProbeData);
                        getActivity().finish();
                        Toast.makeText(getActivity(), R.string.entry_deleted_toast, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog deleteAlert = alertBuilder.create();
        deleteAlert.setTitle("Delete Probe Entry");
        deleteAlert.show();
    }
}