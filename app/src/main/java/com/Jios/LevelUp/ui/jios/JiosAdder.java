package com.Jios.LevelUp.ui.jios;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.MainActivity;
import com.example.LevelUp.ui.jios.JiosFragment;
import com.example.tryone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JiosAdder extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
    private int selection;
    TextView mDateSelected;
    TextView mTimeSelected;
    EditText mEventTitle;
    Button mDateSelector;
    Button mTimeSelector;
    Button mSaveJio;
    EditText mEventDescription;
    EditText mEventLocation;
    Uri currentUri;
    private int hourOfDay;
    private int minute;
    boolean validDate;
    boolean validTime = false;
    boolean dateIsSame;

    Spinner jioSpinner;

    private static final String[] categories = {
            "Arts", "Sports", "Talks", "Volunteering", "Food", "Others"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jios_adder);
        currentUri = getIntent().getData();

        mEventTitle = findViewById(R.id.event_title);

        mDateSelector = findViewById(R.id.event_date);
        mDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        mDateSelected = findViewById(R.id.date_selected);

        mTimeSelector = findViewById(R.id.event_time);
        mTimeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mTimeSelected = findViewById(R.id.time_selected);

        mEventDescription = findViewById(R.id.event_description);
        mEventLocation = findViewById(R.id.location);

        initializeSpinner();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Jios");
        mSaveJio = findViewById(R.id.save_jio);
        mSaveJio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JiosItem jiosItem = null;
                String key = mDatabaseReference.push().getKey();
                String jioCreatorUID = MainActivity.currUser.getId();
                try {
                    jiosItem = new JiosItem(0, key, jioCreatorUID,
                            df.parse((String) mDateSelected.getText()), (String) mTimeSelected.getText(),
                            hourOfDay, minute, mEventLocation.getText().toString(),
                            mEventTitle.getText().toString(), mEventDescription.getText().toString(), selection);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                String str = sdf.format(Calendar.getInstance().getTime());
                int intCurrentTime = Integer.parseInt(str);
                try {
                    validDate = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(Calendar.getInstance().getTime()))
                            .compareTo(df.parse(mDateSelected.getText().toString())) > 0;
                    dateIsSame = df.parse(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(Calendar.getInstance().getTime()))
                            .compareTo(df.parse(mDateSelected.getText().toString())) == 0;
                    if (!mTimeSelected.getText().toString().equals("No Time Selected") && dateIsSame) {
                        validTime = intCurrentTime > Integer.parseInt(mTimeSelected.getText().toString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean factors = !mEventLocation.getText().toString().equals("")
                        && !mEventTitle.getText().toString().equals("")
                        && !mEventDescription.getText().toString().equals("")
                        && !mTimeSelected.getText().toString().equals("No Time Selected")
                        && !mDateSelected.getText().toString().equals("No Date Selected")
                        //validDate must be an incorrect date
                        && !validDate
                        //validTime must be an incorrect time
                        && !validTime;
                if (!factors) {
                    Toast.makeText(JiosAdder.this, "Please check all fields and try again", Toast.LENGTH_LONG).show();
                } else if (factors) {
                    mDatabaseReference.child(key).setValue(jiosItem);
                    Toast.makeText(JiosAdder.this, "Jio saved successfully", Toast.LENGTH_LONG).show();
                    JiosFragment.setRefresh(true);
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(c.getTime());
        mDateSelected.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String initial = hourOfDay < 10 ? "0" : "";
        String after = minute < 10 ? "0" : "";
        String currentTimeString = initial + hourOfDay + after + minute;
        mTimeSelected.setText(currentTimeString);
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    private void initializeSpinner() {
        jioSpinner = findViewById(R.id.jio_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JiosAdder.this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jioSpinner.setAdapter(adapter);
        jioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selection = 0;
                        break;
                    case 1:
                        selection = 1;
                        break;
                    case 2:
                        selection = 2;
                        break;
                    case 3:
                        selection = 3;
                        break;
                    case 4:
                        selection = 4;
                        break;
                    case 5:
                        selection = 5;
                        break;
                    default:
                        selection = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
