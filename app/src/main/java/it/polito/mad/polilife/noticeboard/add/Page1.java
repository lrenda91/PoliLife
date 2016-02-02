package it.polito.mad.polilife.noticeboard.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.google.android.gms.maps.model.LatLng;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.parcel.PNoticeData;
import it.polito.mad.polilife.maps.MapsUtil;
import it.polito.mad.polilife.noticeboard.NoticeUpdater;

public class Page1 extends Fragment implements NoticeUpdater {

    private AutoCompleteTextView locationEditText;
    private EditText titleEditText, descrEditText;
    private DatePicker datePicker;
    private SeekBar mPrice, mSize;
    private RadioGroup contractRG, propertyRG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationEditText = (AutoCompleteTextView) view.findViewById(R.id.location);
        MapsUtil.setAutoCompleteGMaps(locationEditText);
        titleEditText = (EditText) view.findViewById(R.id.title);
        descrEditText = (EditText) view.findViewById(R.id.description);
        datePicker = (DatePicker) view.findViewById(R.id.availableDatePicker);
        contractRG = (RadioGroup) view.findViewById(R.id.contract_type_radio_group);
        propertyRG = (RadioGroup) view.findViewById(R.id.property_type_radio_group);

        mSize = (SeekBar) view.findViewById(R.id.size_seekbar);
        mPrice = (SeekBar) view.findViewById(R.id.price);
    }

    @Override
    public void update(PNoticeData data) {
        String typedLocation = locationEditText.getText().toString();
        RadioButton contractRB = (RadioButton) contractRG.findViewById(contractRG.getCheckedRadioButtonId());
        RadioButton propertyRB = (RadioButton) propertyRG.findViewById(propertyRG.getCheckedRadioButtonId());
        data.title(titleEditText.getText().toString())
                .description(descrEditText.getText().toString())
                .availableFrom(
                    datePicker.getDayOfMonth(),
                    datePicker.getMonth(),
                    datePicker.getYear())
                .contractType(contractRB.getText().toString())
                .propertyType(propertyRB.getText().toString())
                .cost(mPrice.getProgress())
                .size(mSize.getProgress())
                .location(typedLocation);
        if (!typedLocation.isEmpty()){
            LatLng geoPosition = MapsUtil.getFirstGMapsAddress(getActivity(), typedLocation);
            if (geoPosition != null)
                data.latitude(geoPosition.latitude).longitude(geoPosition.longitude);
        }
    }

}
