package pickleib.utilities.email;

import context.ContextStore;
import utils.EmailUtilities;
import utils.StringUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;

public class EmailAcquisition {

    StringUtilities strUtils = new StringUtilities();
    EmailInbox emailInbox;

    public EmailAcquisition(EmailInbox emailInbox){
        this.emailInbox = emailInbox;
    }

    public String acquireEmail(EmailUtilities.Inbox.EmailField filterType, String filterKey) {
        emailInbox.log.new Info("Acquiring & saving email(s) by " +
                strUtils.highlighted(BLUE, filterType.name()) +
                strUtils.highlighted(GRAY, " -> ") +
                strUtils.highlighted(BLUE, filterKey)
        );
        emailInbox.getEmail(filterType, filterKey, false, true, true);
        File dir = new File("inbox");
        String absolutePath = null;
        for (File email : Objects.requireNonNull(dir.listFiles()))
            try {
                boolean nullCheck = Files.probeContentType(email.toPath()) != null;
                if (nullCheck && Files.probeContentType(email.toPath()).equals("text/html")) {
                    absolutePath = "file://" + email.getAbsolutePath().replaceAll("#", "%23");
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return absolutePath;
    }
}
