import lombok.Data;

@Data
public class Webhook_Info {
    private String id;
    private String date;
    private String subject;
    private String from_email;
    private String from_name;
}
