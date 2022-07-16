package Helper;

import java.io.FileOutputStream;
import java.io.IOException;

interface loginActvity {
    public String getFileName();
}

/**Helper class for logging system events.
 *@author Joe Foley*/
public class SystemLog {
    /**Lambda expression to return filename*/
    loginActvity log = () -> { return "login_activity.txt"; };

    /**logSignInAttempt is the function used to handle appending the loginStamp to the end of the login_activity.text
     * file. Uses the FileOutputStream class's write method to append to the end of the file.*/
    public void logSignInAttempt(String loginStamp) throws IOException {
        String filePath = log.getFileName();
        FileOutputStream log = new FileOutputStream(filePath, true);
        byte[] append = loginStamp.getBytes();
        log.write(append);
        log.close();
    }
}
