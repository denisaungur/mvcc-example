import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.MessageService;
import service.SocketServer;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private static final String APPLICATION_SPRING_CONFIG = "spring-beans.xml";
    private static final int SOCKET_PORT = 9000;
    private static final String MESSAGE_SERVICE_BEAN = "messageService";


    private static final Logger LOG = Logger.getLogger(Main.class);


    public static void main(String[] args) {
        BasicConfigurator.configure();
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_SPRING_CONFIG);

        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {

            while (true) {
                new SocketServer(serverSocket.accept(), (MessageService) context.getBean(MESSAGE_SERVICE_BEAN)).start();
            }

        } catch (IOException ex) {
            LOG.error("Unable to start the server.", ex);
        }
    }
}

