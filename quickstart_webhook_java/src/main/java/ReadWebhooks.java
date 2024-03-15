// Import Spark, Jackson and Mustache libraries
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.mustache.MustacheTemplateEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Import Java libraries
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Import external libraries
import org.apache.commons.codec.digest.HmacUtils;

public class ReadWebhooks {
    // Function to get Hmac
    public static String getHmac(String data, String key) {
        return new HmacUtils("HmacSHA256", key).hmacHex(data);
    }

    public static void main(String[] args) {
        // Array list of Webhooks
        ArrayList<Webhook_Info> array = new ArrayList<Webhook_Info>();

        // Default path when we load our web application
        get("/", (request, response) -> {
            // Create a model to pass information to the mustache template
            Map<String, Object> model = new HashMap<>();
            model.put("webhooks", array);
            // Call the mustache template
            return new ModelAndView(model, "show_webhooks.mustache");
        }, new MustacheTemplateEngine());

        // Validate our webhook with the Nylas server
        get("/webhook", (request, response) ->
                request.queryParams("challenge"));

        // Getting webhook information
        post("/webhook", (request, response) -> {
                    // Create Json object mapper
                    ObjectMapper mapper = new ObjectMapper();
                    // Read the response body as a Json object
                    JsonNode incoming_webhook = mapper.readValue(request.body(), JsonNode.class);
                    // Make sure we're reading our calendar
                    if (getHmac(request.body(), URLEncoder.encode(System.getenv("CLIENT_SECRET"), "UTF-8")).
                            equals(request.headers("X-Nylas-Signature"))) {
                         // Create a new Webhook_Info record
                         Webhook_Info new_webhook = new Webhook_Info();
                         // Fill webhook information
                         System.out.println(incoming_webhook.get("data").get("object"));
                         new_webhook.setId(incoming_webhook.get("data").get("object").get("id").textValue());
                         new_webhook.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                                 format(new java.util.Date((incoming_webhook.get("data").get("object").get("date").asLong() * 1000L))));
                         new_webhook.setSubject(incoming_webhook.get("data").get("object").get("subject").textValue());
                         new_webhook.setFrom_email(incoming_webhook.get("data").get("object").get("from").get(0).get("email").textValue());
                         new_webhook.setFrom_name(incoming_webhook.get("data").get("object").get("from").get(0).get("name").textValue());
                         // Add webhook call to an array, so that we display it on screen
                         array.add(new_webhook);
                    }
                    response.status(200);
                    return "Webhook Received";
                }
            );
    }
}