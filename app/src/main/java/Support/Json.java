package Support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/01/2017.
 */

public class Json {
    private List<EventClass> listOfEvents;

    public Json(List<EventClass> listOfEvents) {
        listOfEvents = new ArrayList<EventClass>();
        this.listOfEvents = listOfEvents;
    }

    public JSONObject generateJson() throws JSONException {
        if (listOfEvents == null) {
            return null;
        }
        JSONObject baseObject = new JSONObject();

        JSONArray baseArray = new JSONArray();

        for (EventClass event : listOfEvents
                ) {
            baseArray.put(event.getData().toJson());
        }
        baseObject.put("arrayEvents", baseArray);
        return baseObject;
    }

}
