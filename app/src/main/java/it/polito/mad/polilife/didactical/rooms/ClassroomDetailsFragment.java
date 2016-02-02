package it.polito.mad.polilife.didactical.rooms;

import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.Utility;
import it.polito.mad.polilife.maps.MapsUtil;

/**
 * Created by luigi onSelectAppliedJobs 12/11/15.
 */
public class ClassroomDetailsFragment extends Fragment implements OnMapReadyCallback {

    public static ClassroomDetailsFragment newInstance(
            String name, double lat, double lng, String details){
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        args.putString("details", details);
        ClassroomDetailsFragment fragment = new ClassroomDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private LatLngBounds.Builder mBoundsBuilder = new LatLngBounds.Builder();
    private LatLng myPosition;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_classroom_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String details = getArguments().getString("details");
        ((TextView) view.findViewById(R.id.room_details)).setText(details);
        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.map_container, mapFragment); // map_container is your FrameLayout container
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack(null);
        ft.commit();
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed onSelectAppliedJobs the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                if (myPosition == null) {
                    myPosition = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(myPosition).title("It's Me!"));
                    mBoundsBuilder.include(myPosition);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 100));
                }
            }
        });

        String name = getArguments().getString("name");
        double latitude = getArguments().getDouble("lat");
        double longitude = getArguments().getDouble("lng");



        LatLng room = new LatLng(latitude, longitude);
        mBoundsBuilder.include(room);
        mMap.addMarker(new MarkerOptions().position(room).title(name));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 100));

        //float zoom = MapsUtil.calculateZoomLevel(getActivity(), 2000);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(room, zoom));
    }

}