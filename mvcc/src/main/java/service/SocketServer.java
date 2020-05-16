package service;

import org.apache.log4j.Logger;
import util.ServerConstants;

import java.io.*;
import java.net.Socket;

public class SocketServer extends Thread {

    private Socket socket;
    private MessageService messageService;

    private static Logger LOG = Logger.getLogger(SocketServer.class);

    public SocketServer(Socket socket, MessageService messageService) {
        this.messageService = messageService;
        this.socket = socket;
        LOG.info(ServerConstants.Messages.CLIENT_CONNECTED_MESSAGE);
    }

    public void run() {

        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String request;
            while ((request = br.readLine()) != null) {
                LOG.info(ServerConstants.Messages.CLIENT_MESSAGE_RECEIVED + request);
                String result = messageService.processRequest(request, this.getId());
                result += '\n';
                outputStream.write(result.getBytes());
                outputStream.flush();
            }

        } catch (IOException ex) {
            LOG.error("Unable to get streams from client", ex);
        } catch (InterruptedException e) {
            LOG.error(e.getStackTrace());
        }
    }

}
