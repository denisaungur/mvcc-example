import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    private static final String SERVER_HOST_NAME = "127.0.0.1";
    private static final int SERVER_PORT = 9000;
    private static final String EMPTY_STRING = "";

    private static Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] arg) {
        BasicConfigurator.configure();
        LOG.info("Transaction Started.");

        try (Socket echoSocket = new Socket(SERVER_HOST_NAME, SERVER_PORT);
             PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("client: ");
                String userInput = stdIn.readLine();
                /** Exit on 'q' char sent */
                if ("q".equals(userInput)) {
                    break;
                }

                if (userInput != EMPTY_STRING) {
                    out.println(userInput);
                    out.flush();
                    System.out.println("server: " + in.readLine());
                }
            }


        } catch (UnknownHostException e) {
            LOG.error("Unknown host name.");
        } catch (IOException e) {
            LOG.error("Unable to get streams from server!");
        }
    }


}
