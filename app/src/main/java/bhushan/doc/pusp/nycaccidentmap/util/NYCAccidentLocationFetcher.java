package bhushan.doc.pusp.nycaccidentmap.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import bhushan.doc.pusp.nycaccidentmap.entities.NYCAccidentDetails;
import bhushan.doc.pusp.nycaccidentmap.exception.NYCOpenDataException;

/**
 * Created by PBhushan on 2/28/2015.
 */
public class NYCAccidentLocationFetcher {

    private static final String BASE_URL = "https://data.cityofnewyork.us/resource/h9gi-nx95.json";
    private static final String TAG = "NYCAccidentLocationFetcher";

    /**
     * Fetches records of NYC Motor Vehicle Collision happened in particular ZipCode after 2015-02-01
     *
     * @param zip_code area code of NYC where collision took place
     * @return NYCAccidentDetails
     * @throws bhushan.doc.pusp.nycaccidentmap.exception.NYCOpenDataException
     */

    public static ArrayList<NYCAccidentDetails> getNYCAccidentDetails(String zip_code) throws NYCOpenDataException {
        ArrayList<NYCAccidentDetails> nycAccidentList = new ArrayList<NYCAccidentDetails>();
        String URL_EXTRA = "?$where=date>'2015-02-01'&zip_code='" + zip_code + "'";

        RestClient restClient = new RestClient(BASE_URL);
        restClient.setExtraUrl(URL_EXTRA);
        // restClient.addParam("zip_code", zip_code);

        try {
            restClient.execute(RestClient.RequestMethod.GET);
            if (restClient.isRequestSuccessful()) {
                Object nycResponseJSONTypeCheck;
                JSONArray nycResponseJSONArray;
                JSONObject nycResponseJSONObject;
                String nycOpenDataResponse = restClient.getResponse();
                Log.d(TAG, "data response collected");
                nycResponseJSONTypeCheck = new JSONTokener(nycOpenDataResponse).nextValue();
                if (nycResponseJSONTypeCheck instanceof JSONArray) {
                    nycResponseJSONArray = (JSONArray) nycResponseJSONTypeCheck;
                    if (nycResponseJSONArray.length() != 0) {
                        for (int i = 0; i < nycResponseJSONArray.length(); i++) {
                            JSONObject nycResponseJSON = nycResponseJSONArray.getJSONObject(i);
                            NYCAccidentDetails nycAccidentDetails = new NYCAccidentDetails();
                            nycAccidentDetails.setDate(nycResponseJSON.optString("date"));
                            nycAccidentDetails.setTime(nycResponseJSON.optString("time"));
                            nycAccidentDetails.setBorough(nycResponseJSON.optString("borough"));
                            nycAccidentDetails.setZipCode(nycResponseJSON.optString("zip_code"));

                            nycAccidentDetails.setStreetName(nycResponseJSON.optString("on_street_name"));
                            nycAccidentDetails.setCrossStreetName(nycResponseJSON.optString("off_street_name"));
                            nycAccidentDetails.setPersonInjured(nycResponseJSON.optInt("number_of_persons_injured", 0));

                            nycAccidentDetails.setPersonKilled(nycResponseJSON.optInt("number_of_persons_killed", 0));
                            nycAccidentDetails.setPedestrianInjured(nycResponseJSON.optInt("number_of_pedestrians_injured", 0));
                            nycAccidentDetails.setPedestrianKilled(nycResponseJSON.optInt("number_of_pedestrians_killed", 0));
                            nycAccidentDetails.setCyclistInjured(nycResponseJSON.optInt("number_of_cyclist_injured", 0));
                            nycAccidentDetails.setCyclistKilled(nycResponseJSON.optInt("number_of_cyclist_killed", 0));
                            nycAccidentDetails.setMotoristInjured(nycResponseJSON.optInt("number_of_motorist_injured", 0));
                            nycAccidentDetails.setMotoristKilled(nycResponseJSON.optInt("number_of_motorist_killed", 0));
                            nycAccidentDetails.setFactorVehicle1(nycResponseJSON.optString("contributing_factor_vehicle_1", "Unspecified"));
                            nycAccidentDetails.setFactorVehicle2(nycResponseJSON.optString("contributing_factor_vehicle_2", "Unspecified"));
                            nycAccidentDetails.setUniqueKey(nycResponseJSON.optString("unique_key", "None"));
                            nycAccidentDetails.setVehicleType1(nycResponseJSON.optString("vehicle_type_code1", "Unspecified"));
                            nycAccidentDetails.setVehicleType2(nycResponseJSON.optString("vehicle_type_code2", "Unspecified"));
                            // for location
                            JSONObject locationJSON = nycResponseJSON.getJSONObject("location");
                            nycAccidentDetails.setLatitude(locationJSON.optDouble("latitude", 0));
                            nycAccidentDetails.setLongitude(locationJSON.optDouble("longitude", 0));


                            nycAccidentList.add(nycAccidentDetails);
                        }
                    } else {
                        Log.i(TAG, "No result found for this query");
                    }

                } else if (nycResponseJSONTypeCheck instanceof JSONObject) {
                    nycResponseJSONObject = (JSONObject) nycResponseJSONTypeCheck;
                    throw new NYCOpenDataException(nycResponseJSONObject.optString("message"));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while fetching NYC Open Data Collision for ZipCode " + zip_code, e);
        } catch (IOException e) {
            Log.e(TAG, "Error while parsing NYC OpenData response from server ", e);
        }

        return nycAccidentList;
    }
}

