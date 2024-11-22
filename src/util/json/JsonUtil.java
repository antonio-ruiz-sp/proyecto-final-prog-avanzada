package util.json;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JsonUtil {
    static ObjectMapper om = new ObjectMapper();
    
    public static String toJsonRepresentation(Object o){
        String result = "";
        try {
            result = om.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
