package com.example.cooldowncalculator;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static String PATTERN = "(\\d{1,3}[.]\\d*)[, ]+-?(\\d{1,3}[.]\\d*)";
    private Pattern sPattern = Pattern.compile(PATTERN);

    final DecimalFormat decimalFormat = new DecimalFormat("#.####");

    double lat1,lat2,lon1,lon2,distance;
    EditText distanza,tempo,distanza1,distanza2;

    Geocoder geocoder;
    List<Address> addresses,addresses2;
    int flagOffset = 0x1F1E6;
    int asciiOffset = 0x41;
    TextView info1,info2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        distanza1 =  findViewById(R.id.editText);
        distanza2  =  findViewById(R.id.editText2);

        final TextInputLayout til =  findViewById(R.id.text_input_layout);
        final TextInputLayout til2 =  findViewById(R.id.text_input_layout2);

        Button calcolaDistanza  =  findViewById(R.id.button);

        distanza =  findViewById(R.id.editText3);
        tempo =  findViewById(R.id.editText4);

        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        info1 = findViewById(R.id.textView4);
        info2 = findViewById(R.id.textView5);

        calcolaDistanza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til.setError(null);
                til2.setError(null);
                distanza.setText("");
                tempo.setText("");
                info1.setText("");
                info2.setText("");

                if(distanza1.getText() == null || distanza1.getText().toString().isEmpty()){
                    if(distanza2.getText() == null || distanza2.getText().toString().isEmpty()){
                        til2.setError(getText(R.string.insert_valid_coordinate));
                        til.setError(getText(R.string.insert_valid_coordinate));
                    }
                }

                if ( isValid(distanza1.getText()) ) {
                    Log.d("distanza1","valido");
                    if( isValid(distanza2.getText()) ){
                        setDistance();
                    }else{
                        Log.d("distanza2","NON valido");
                        try{
                            Matcher m = sPattern.matcher(distanza2.getText().toString());
                            if(m.find()){
                                distanza2.setText(m.group());
                                setDistance();
                            }else{
                                til2.setError(getText(R.string.insert_valid_coordinate));
                            }
                        }catch (Exception e){
                            Log.d("TAG", "Exception: " + e.getMessage());
                        }
                    }
                }else{
                    Log.d("distanza1","NON valido");
                    try{
                        Matcher m = sPattern.matcher(distanza1.getText().toString());
                        if(m.find()){
                            distanza1.setText(m.group());
                            setDistance();
                        }else{
                            til.setError(getText(R.string.insert_valid_coordinate));
                        }
                    }catch (Exception e){
                        Log.d("TAG", "Exception: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void setDistance(){

        Log.d("distanza2","valido");
        lat1 = Double.valueOf(distanza1.getText().toString().substring(0,distanza1.getText().toString().indexOf(',')));
        Log.d("LAT1",String.valueOf(lat1));

        lon1 = Double.valueOf(distanza1.getText().toString().substring(distanza1.getText().toString().indexOf(',')+1));
        Log.d("LON1",String.valueOf(lon1));

        lat2 = Double.valueOf(distanza2.getText().toString().substring(0,distanza2.getText().toString().indexOf(',')));
        Log.d("LAT2",String.valueOf(lat2));

        lon2 = Double.valueOf(distanza2.getText().toString().substring(distanza2.getText().toString().indexOf(',')+1));
        Log.d("LON2",String.valueOf(lon2));

        distance = distance(lat1,lat2,lon1,lon2,0,0);
        String formatedDistance = decimalFormat.format(distance);
        Log.d("Distance: ", formatedDistance);
        distanza.setText(formatedDistance +" " + getText(R.string.km));
        tempo.setText(setTime(distance));
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat1, lon1, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String countryCode = addresses.get(0).getCountryCode();

            int firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset;
            int secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset;

            String flag = new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
            String cityState = city + " - " + state;
            info1.append(flag);
            info1.append(cityState);
        //------------------------------------------------------------------------------------------------------//
            addresses2 = geocoder.getFromLocation(lat2, lon2, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String city2 = addresses2.get(0).getLocality();
            String state2 = addresses2.get(0).getAdminArea();
            String countryCode2 = addresses2.get(0).getCountryCode();

            int firstChar2 = Character.codePointAt(countryCode2, 0) - asciiOffset + flagOffset;
            int secondChar2 = Character.codePointAt(countryCode2, 1) - asciiOffset + flagOffset;

            String flag2 = new String(Character.toChars(firstChar2)) + new String(Character.toChars(secondChar2));
            String cityState2 = city2 + " - " + state2;
            info2.append(flag2);
            info2.append(cityState2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String setTime(double distance){
        if (distance <=1 ) return "1 "+ getText(R.string.minute);
        if (distance <=2 ) return "2 " + getText(R.string.minutes);
        if (distance <=3 ) return "3 " + getText(R.string.minutes);
        if (distance <=4 ) return "3 " + getText(R.string.minutes);
        if (distance <=5 ) return "4 " + getText(R.string.minutes);
        if (distance <=8 ) return "5 " + getText(R.string.minutes);
        if (distance <=10 ) return "7 " + getText(R.string.minutes);
        if (distance <=15 ) return "9 " + getText(R.string.minutes);
        if (distance <=20 ) return "12 " + getText(R.string.minutes);
        if (distance <=25 ) return "15 " + getText(R.string.minutes);
        if (distance <=30 ) return "17 " + getText(R.string.minutes);
        if (distance <=35 ) return "18 " + getText(R.string.minutes);
        if (distance <=40 ) return "19 " + getText(R.string.minutes);
        if (distance <=45 ) return "19 " + getText(R.string.minutes);
        if (distance <=50 ) return "20 " + getText(R.string.minutes);
        if (distance <=60 ) return "21 " + getText(R.string.minutes);
        if (distance <=70 ) return "23 " + getText(R.string.minutes);
        if (distance <=80 ) return "24 " + getText(R.string.minutes);
        if (distance <=90 ) return "25 " + getText(R.string.minutes);
        if (distance <=100 ) return "26 " + getText(R.string.minutes);
        if (distance <=125 ) return "29 " + getText(R.string.minutes);
        if (distance <=150 ) return "32 " + getText(R.string.minutes);
        if (distance <=175 ) return "34 " + getText(R.string.minutes);
        if (distance <=201 ) return "37 " + getText(R.string.minutes);
        if (distance <=250 ) return "41 " + getText(R.string.minutes);
        if (distance <=300 ) return "46 " + getText(R.string.minutes);
        if (distance <=328 ) return "48 " + getText(R.string.minutes);
        if (distance <=350 ) return "50 " + getText(R.string.minutes);
        if (distance <=400 ) return "54 " + getText(R.string.minutes);
        if (distance <=450 ) return "58 " + getText(R.string.minutes);
        if (distance <=500 ) return "62 " + getText(R.string.minutes);
        if (distance <=550 ) return "66 " + getText(R.string.minutes);
        if (distance <=600 ) return "70 " + getText(R.string.minutes);
        if (distance <=650 ) return "74 " + getText(R.string.minutes);
        if (distance <=700 ) return "77 " + getText(R.string.minutes);
        if (distance <=751 ) return "82 " + getText(R.string.minutes);
        if (distance <=802 ) return "84 " + getText(R.string.minutes);
        if (distance <=839 ) return "88 " + getText(R.string.minutes);
        if (distance <=897 ) return "90 " + getText(R.string.minutes);
        if (distance <=900 ) return "91 " + getText(R.string.minutes);
        if (distance <=948 ) return "95 " + getText(R.string.minutes);
        if (distance <=1007 ) return "98 " + getText(R.string.minutes);
        if (distance <=1020 ) return "102 " + getText(R.string.minutes);
        if (distance <=1100 ) return "104 " + getText(R.string.minutes);
        if (distance <=1180 ) return "109 " + getText(R.string.minutes);
        if (distance <=1200 ) return "111 " + getText(R.string.minutes);
        if (distance <=1221 ) return "113 " + getText(R.string.minutes);
        if (distance <=1300 ) return "117 " + getText(R.string.minutes);
        if (distance <=1344 ) return "119 " + getText(R.string.minutes);
        if (distance >=1355 ) return "120 " + getText(R.string.minutes);
        return "";
    }

    private boolean isValid(CharSequence s) {
        return sPattern.matcher(s).matches();
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
