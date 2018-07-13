package cl.cutiko.pocplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_GEOLOCATION = 323;
    private PlaceDetectionClient placeDetectionClient;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeDetectionClient = Places.getPlaceDetectionClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, RC_GEOLOCATION);
        } else {
            getPlaces();
        }
    }


    @SuppressLint("MissingPermission")
    private void getPlaces(){
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(new PlaceFilter());

        placeResult.addOnSuccessListener(new OnSuccessListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onSuccess(PlaceLikelihoodBufferResponse placeLikelihoods) {
                Log.d("PLACES_POC", "FUNCTIONO");
                List<Place> places = new ArrayList<>();
                for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                    Place place = placeLikelihood.getPlace();
                    String name = place.getName().toString();
                    Log.d("PLACES_POC", name);
                    for (int type : placeLikelihood.getPlace().getPlaceTypes()) {
                        if (Place.TYPE_CAR_REPAIR == type) {
                            Log.d("PLACES_POC", name);
                            places.add(place);
                        }
                    }
                }
                Log.d("PLACES_POC", "size: " + places.size());
                placeLikelihoods.release();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PLACES_POC", "FALLO", e);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (RESULT_OK == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPlaces();
        }
    }
}
