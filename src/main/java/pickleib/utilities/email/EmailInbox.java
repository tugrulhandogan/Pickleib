package pickleib.utilities.email;

import lombok.*;
import org.openqa.selenium.TimeoutException;
import utils.EmailUtilities;
import utils.Printer;
import java.util.concurrent.TimeUnit;

import static utils.EmailUtilities.Inbox.EmailField.CONTENT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("UnusedReturnValue")
public class EmailInbox {

    Printer log = new Printer(EmailInbox.class);
    EmailUtilities.Inbox.EmailField filter;
    String filterKey;
    String initialKeyword;
    String finalKeyword;
    Boolean print;
    Boolean save;
    Boolean saveAttachments;
    String host;
    String port;
    String secureCon;
    String email;
    String emailApplicationPassword;

    public EmailInbox(String host, String port,  String email, String emailApplicationPassword, String secureCon) {
        this.host = host;
        this.port = port;
        this.secureCon = secureCon;
        this.email = email;
        this.emailApplicationPassword = emailApplicationPassword;
    }

    public static String getEmail(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon,
            EmailUtilities.Inbox.EmailField filter,
            String filterKey,
            Boolean print,
            Boolean save,
            Boolean saveAttachments){
        EmailUtilities.Inbox inbox = getInbox(
                email,
                emailApplicationPassword,
                host,
                port,
                secureCon,
                filter,
                filterKey,
                print,
                save,
                saveAttachments
        );
        return inbox.messages.get(0).get(CONTENT).toString();
    }

    public String getEmail(){
        EmailUtilities.Inbox inbox = getInbox(
                email,
                emailApplicationPassword,
                host,
                port,
                secureCon,
                filter,
                filterKey,
                print,
                save,
                saveAttachments
        );
        return inbox.messages.get(0).get(CONTENT).toString();
    }

    public String getEmail(
            EmailUtilities.Inbox.EmailField filter,
            String filterKey,
            Boolean print,
            Boolean save,
            Boolean saveAttachments){
        EmailUtilities.Inbox inbox = getInbox(
                email,
                emailApplicationPassword,
                host,
                port,
                secureCon,
                filter,
                filterKey,
                print,
                save,
                saveAttachments
        );
        return inbox.messages.get(0).get(CONTENT).toString();
    }

    public static void clearInbox(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon
    ){
        new Printer(EmailInbox.class).new Info("Flushing email inbox...");
        new EmailUtilities.Inbox(
                host,
                port,
                email,
                emailApplicationPassword,
                secureCon,
                false,
                false,
                false
        );
    }

    public void clearInbox(){
        log.new Info("Flushing email inbox...");
        new EmailUtilities.Inbox(
                host,
                port,
                email,
                emailApplicationPassword,
                secureCon,
                false,
                false,
                false
        );
    }

    private static EmailUtilities.Inbox getInbox(
            String email,
            String emailApplicationPassword,
            String host,
            String port,
            String secureCon,
            EmailUtilities.Inbox.EmailField filter,
            String filterKey,
            Boolean print,
            Boolean save,
            Boolean saveAttachments
    ){
        double initialTime = System.currentTimeMillis();
        Printer log = new Printer(EmailInbox.class);
        log.new Info("Acquiring email...");
        EmailUtilities.Inbox inbox;
        do {
            inbox = new EmailUtilities.Inbox(
                    host,
                    port,
                    email,
                    emailApplicationPassword,
                    secureCon,
                    filter,
                    filterKey,
                    print,
                    save,
                    saveAttachments
            );
            try {TimeUnit.SECONDS.sleep(3);}
            catch (InterruptedException e) {throw new RuntimeException(e);}
            if (System.currentTimeMillis() - initialTime > 45000) throw new TimeoutException("Verification email did not arrive!");
        }
        while (inbox.messages.size() == 0);
        log.new Success("Email(s) acquired!");
        return inbox;
    }
}
