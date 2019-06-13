package andreyskakunenko.sunrisesunset;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import andreyskakunenko.sunrisesunset.Retrofit.GoogleAPI;
import andreyskakunenko.sunrisesunset.Retrofit.MyAPI;
import andreyskakunenko.sunrisesunset.Retrofit.RetrofitClient;
import andreyskakunenko.sunrisesunset.models.GPlaceModel;
import andreyskakunenko.sunrisesunset.models.Sun;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    public static final String SUN_URL = "https://api.sunrise-sunset.org/";
    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";
    private final static String API_KEY="AIzaSyD-UwFo8xGMLk96D8CX_wrdSXVeBMgRd3E";

    TextView sunRise, sunSet, mCurrentLocation,latitude,longitude;
    EditText mLocation;
    MyAPI mAPI;
    GoogleAPI mGoogleAPI;
    String srise="0",sset="0",lat,lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        sunRise=findViewById(R.id.sunrisetime);
        sunSet=findViewById(R.id.sunsettime);
        latitude =findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        mCurrentLocation = findViewById(R.id.current_location);
        mLocation = findViewById(R.id.location);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    lat = location.getLatitude()+"";
                    lng = location.getLongitude()+"";
                    fetchData(lat,lng);
                }

            }
        });

        mLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String newLocation = mLocation.getText().toString();
                    mLocation.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mLocation.getWindowToken(), 0);

                    Retrofit retrofitG = new Retrofit.Builder()
                            .baseUrl(PLACES_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    mGoogleAPI = retrofitG.create(GoogleAPI.class);
                    mGoogleAPI.getLocation(PLACES_URL+"json?input="+newLocation+"&inputtype=textquery&fields=geometry&key="+API_KEY).enqueue(new Callback<GPlaceModel>() {
                        @Override
                        public void onResponse(Call<GPlaceModel> call, Response<GPlaceModel> response) {
                            if (response.body()!=null) {
                                lat = response.body().getCandidates().get(0).getGeometry().getLocation().getLat().toString();
                                lng = response.body().getCandidates().get(0).getGeometry().getLocation().getLng().toString();
                                fetchData(lat, lng);
                            }
                        }

                        @Override
                        public void onFailure(Call<GPlaceModel> call, Throwable t) {
                        }
                    });

                    mCurrentLocation.setText(R.string.selected_location);
                    return true;
                }
                return false;
            }
        });

    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }
    private void fetchData(String lat, String lng){
        latitude.setText(lat);
        longitude.setText(lng);
        Retrofit retrofit = RetrofitClient.getInstance();
        mAPI = retrofit.create(MyAPI.class);
        mAPI.getData(SUN_URL+"json?lat="+lat+"&lng="+lng).enqueue(new Callback<Sun>() {
            @Override
            public void onResponse(Call<Sun> call, Response<Sun> response) {
                if (response.body()!=null) {
                    srise = response.body().getResults().getSunrise();
                    sset = response.body().getResults().getSunset();

                    sunRise.setText(timeFormatter(srise));
                    sunSet.setText(timeFormatter(sset));
                }

            }

            @Override
            public void onFailure(Call<Sun> call, Throwable t) {

            }
        });
    }
    private String timeFormatter(String time){

        String hh,mm,ss;
        String apm = time.substring(time.lastIndexOf(' ') + 1);
        if (time.length()==11){
            hh = time.substring(0,2);
            mm = time.substring(3,5);
            ss = time. substring(6,8);
        }else {
            hh = time.substring(0,1);
            mm = time.substring(2,4);
            ss = time. substring(5,7);
        }
        int h = Integer.parseInt(hh);
        switch (h){
            case 10: hh = "1";
            if(apm.equals("PM")){apm="AM";}else {apm="PM";};
            break;
            case 11: hh = "2";
                if(apm.equals("PM")){apm="AM";}else {apm="PM";}
            break;
            case 12: hh = "3";
                if(apm.equals("PM")){apm="AM";}else {apm="PM";}
            break;
            default: hh = h+3+"";
            break;
        }
        return hh+":"+mm+":"+ss+" "+apm;
    }
}
