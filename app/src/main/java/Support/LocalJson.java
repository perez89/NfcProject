package Support;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 10/01/2017.
 */

public class LocalJson {
    private LocalEvent event;

    public LocalJson(LocalEvent event) {
        this.event = event;
    }

    public JSONObject generateJson() throws JSONException {
        if (event == null) {
            return null;
        }
        JSONObject baseObject = new JSONObject();


        JSONObject baseArray = event.getData().toJsonToggl();
        baseObject.put("time_entry", baseArray);
        if(baseObject!=null)
            System.out.println("NOT NULL= " + baseObject.toString());
        return baseObject;
    }

}
