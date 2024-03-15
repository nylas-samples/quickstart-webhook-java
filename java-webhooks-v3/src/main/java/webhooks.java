// Import Nylas packages
import com.nylas.NylasClient;
import com.nylas.models.*;
import com.nylas.resources.Webhooks;
import com.nylas.models.WebhookTriggers;
import java.util.*;

// Import DotEnv to handle .env files
import io.github.cdimascio.dotenv.Dotenv;

public class webhooks {
    public static void main(String[] args) throws NylasSdkTimeoutError, NylasApiError {
        // Load the .env file
        Dotenv dotenv = Dotenv.load();
        // Initialize the Nylas client
        NylasClient nylas = new NylasClient.Builder(dotenv.get("V3_TOKEN")).build();

        List<WebhookTriggers> triggers = new ArrayList<>();
        triggers.add(WebhookTriggers.MESSAGE_CREATED);

        CreateWebhookRequest webhookRequest = new CreateWebhookRequest(triggers, "https://ensvplathk.us14.qoddiapp.com/webhooks",
                "My first webhook", Collections.singletonList(dotenv.get("GRANT_ID")));
        try{
            Response<WebhookWithSecret> webhook = new Webhooks(nylas).create(webhookRequest);
            System.out.println(webhook.getData());
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }
}