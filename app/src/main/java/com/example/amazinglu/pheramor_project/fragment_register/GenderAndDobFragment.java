package com.example.amazinglu.pheramor_project.fragment_register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amazinglu.pheramor_project.MainActivity;
import com.example.amazinglu.pheramor_project.R;
import com.example.amazinglu.pheramor_project.base.BaseFragment;
import com.example.amazinglu.pheramor_project.model.User;
import com.example.amazinglu.pheramor_project.utils.DateUtil;
import com.example.amazinglu.pheramor_project.utils.InputUtil;
import com.example.amazinglu.pheramor_project.utils.ViewUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenderAndDobFragment extends BaseFragment {

    @BindView(R.id.user_gender) AppCompatSpinner userGenderChooser;
    @BindView(R.id.user_dob) AppCompatTextView userDobChooser;
    @BindView(R.id.next_button_three) Button nextButton;
    @BindView(R.id.user_interest_in_gender) AppCompatSpinner genderInterestChooser;
    @BindView(R.id.min_age_picker) NumberPicker minAgePicker;
    @BindView(R.id.max_age_picker) NumberPicker maxAgePicker;
    @BindView(R.id.min_age_text) AppCompatTextView minAgeText;
    @BindView(R.id.max_age_text) AppCompatTextView maxAgeText;

    private static final int MAX_AGE = 200;
    private static final int MIN_AGE = 0;

    private User user;
    private String gender;
    private Date dateOfBirth;
    private String genderInInterest;
    private Integer interestMinAge, interestMaxAge;

    public static GenderAndDobFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.KEY_USER, user);
        GenderAndDobFragment fragment = new GenderAndDobFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gender_dob, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        user = getArguments().getParcelable(MainActivity.KEY_USER);

        // set up the user gender chooser
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_chooser, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGenderChooser.setAdapter(adapter);
        userGenderChooser.setVisibility(View.VISIBLE);
        userGenderChooser.setOnItemSelectedListener(new GenderPickerOnItemSelectListener());

        // setup the interest in gender chooser
        ArrayAdapter<CharSequence> genderInterestSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.interest_in_gender_chooser, android.R.layout.simple_spinner_item);
        genderInterestSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderInterestChooser.setAdapter(genderInterestSpinnerAdapter);
        genderInterestChooser.setVisibility(View.VISIBLE);
        genderInterestChooser.setOnItemSelectedListener(new GenderInterestOnItemSelectListener());

        // setup the age range number picker
        minAgePicker.setMinValue(MIN_AGE);
        minAgePicker.setMaxValue(MAX_AGE);
        minAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                interestMinAge = numberPicker.getValue();
            }
        });

        maxAgePicker.setMinValue(MIN_AGE);
        maxAgePicker.setMaxValue(MAX_AGE);
        maxAgePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                interestMaxAge = numberPicker.getValue();
            }
        });

        // if the value has been set before, show the value
        if (user.gender != null) {
            gender = user.gender;
            ViewUtil.setSpinText(userGenderChooser, user.gender);
        }
        if (user.dateOfBirth != null) {
            dateOfBirth = user.dateOfBirth;
            userDobChooser.setText(DateUtil.dateToString(user.dateOfBirth));
        } else {
            Calendar c = Calendar.getInstance();
            userDobChooser.setText(DateUtil.dateToString(c.getTime()));
        }
        if (user.genderInInterest != null) {
            genderInInterest = user.genderInInterest;
            ViewUtil.setSpinText(genderInterestChooser, user.genderInInterest);
        }
        if (user.interestMinAge != User.AGE_DEFAULT_VALUE) {
            interestMinAge = user.interestMinAge;
            minAgePicker.setValue(user.interestMinAge);
        }
        if (user.interestMaxAge != User.AGE_DEFAULT_VALUE) {
            interestMaxAge = user.interestMaxAge;
            maxAgePicker.setValue(user.interestMaxAge);
        }


        // set up dob chooser
        final DatePickerOnSetListener datePickerOnSetListener = new DatePickerOnSetListener();
        userDobChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                Dialog dialog = new DatePickerDialog(getContext(), datePickerOnSetListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        // set up the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    user.dateOfBirth = dateOfBirth;
                    user.gender = gender;
                    user.genderInInterest = genderInInterest;
                    user.interestMinAge = interestMinAge;
                    user.interestMaxAge = interestMaxAge;
                    Toast.makeText(getContext(), "input is valid", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, RaceAndReligionFragment.newInstance(user))
                            .commit();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, UserInfoEditFragment.newInstance(user))
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        TextView genderChooserErrorText = (TextView) userGenderChooser.getSelectedView();
        userDobChooser.setError(null);
        genderChooserErrorText.setError(null);
        TextView genderInterestChooserErrorText = (TextView) genderInterestChooser.getSelectedView();
        genderInterestChooserErrorText.setError(null);
        minAgeText.setError(null);
        maxAgeText.setError(null);

        if (InputUtil.isEmpty(gender) && InputUtil.isEmpty(user.gender)) {
            genderChooserErrorText.setTextColor(Color.RED);
            genderChooserErrorText.setError(
                    getActivity().getResources().getString(R.string.gender_empty_warning));
            return false;
        }

        if (dateOfBirth == null && user.dateOfBirth == null) {
            userDobChooser.setError(getActivity().getResources().getString(R.string.dob_empty_warning));
            return false;
        } else if (!InputUtil.isDobValid(dateOfBirth)) {
            userDobChooser.setError(getActivity().getResources().getString(R.string.dob_not_valid_warning));
            return false;
        }

        if (InputUtil.isEmpty(genderInInterest) && InputUtil.isEmpty(user.genderInInterest)) {
            genderInterestChooserErrorText.setTextColor(Color.RED);
            genderInterestChooserErrorText.setError(
                    getActivity().getResources().getString(R.string.gender_interest_empty_warning));
            return false;
        }

        if (interestMinAge == null && user.interestMinAge == User.AGE_DEFAULT_VALUE) {
            minAgeText.setError(getActivity().getResources().getString(R.string.min_interest_age_empty_warning));
            return false;
        }
        if (interestMaxAge == null && user.interestMaxAge == User.AGE_DEFAULT_VALUE) {
            maxAgeText.setError(getActivity().getResources().getString(R.string.max_interest_age_empty_warning));
            return false;
        }
        if (!InputUtil.isMinMaxAgeValid(interestMinAge, interestMaxAge)) {
            minAgeText.setError(
                    getActivity().getResources().getString(R.string.min_interest_age_not_valid_warning));
            return false;
        }


        return true;
    }

    /**
     * gender spinner on select listener
     * */
    class GenderPickerOnItemSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            gender = ((TextView)view).getText().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    /**
     * dob chooser OnDateSetListener
     * */
    class DatePickerOnSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            if (user.dateOfBirth != null) {
                c.setTime(user.dateOfBirth);
            }
            c.set(year, month, day);
            dateOfBirth = c.getTime();
            userDobChooser.setText(DateUtil.dateToString(dateOfBirth));
        }
    }

    /**
     * interest in gender onItemSelectListener
     * */
    class GenderInterestOnItemSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            genderInInterest = ((TextView)view).getText().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}