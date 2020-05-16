package service;

public interface MessageService {

    String processRequest(String request, long threadId) throws InterruptedException;
}
