package Support;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 18/01/2017.
 */

public class SendDataOnlineToggl {

    public SendDataOnlineToggl(String URL, JSONObject jsonObject) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("JSONObject response= "+response.toString());
                        //mTxtDisplay.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR response= "+error.toString());
                        // TODO Auto-generated method stub

                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                String creds = String.format("%s:%s","lplperez@hotmail.com","wherchojo89");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
    }
}
