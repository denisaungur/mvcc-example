package service.impl;

import domain.Account;
import domain.Version;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import service.DomainDao;
import service.MessageService;
import service.VersionService;

import java.util.*;

import static util.ServerConstants.Messages.*;

public class MessageServiceImpl implements MessageService {

    private VersionService versionService;
    private DomainDao domainDao;

    private static final String MESSAGE_SPLIT_CHARACTER = " ";
    private static final String MESSAGE_ASSIGN_SPLIT = "=";

    private static List<String> versions = Collections.synchronizedList(new ArrayList<>());
    private static Logger LOG = Logger.getLogger(MessageServiceImpl.class);


    public String processRequest(String request, long threadId) throws InterruptedException {

        String result = verifyIfEndStatement(request, threadId);

        if (StringUtils.isEmpty(result)) {
            String[] tokens = request.split(MESSAGE_SPLIT_CHARACTER);
            if (tokens.length == 1) {
                result = tryReadStatement(tokens, threadId);
            }

            if (tokens.length == 2) {
                result = tryAssignStatement(tokens, threadId);
            }
        }

        return StringUtils.isEmpty(result) ? SERVER_ERROR_MESSAGE : result;
    }

    private String verifyIfEndStatement(String request, long threadId) {
        String result = "";
        if (EndStatemet.COMMIT.command.equals(request)) {
            versionService.commitChanges(threadId);
            result = SERVER_COMMIT_MESSAGE;
        }

        if (EndStatemet.ROLLBACK.command.equals(request)) {
            versionService.abortTransaction(threadId);
            result = SERVER_ABORT_MESSAGE;
        }

        return result;
    }

    private String tryReadStatement(String[] tokens, long threadId) {
        String result;
        String[] pair = tokens[0].split(MESSAGE_ASSIGN_SPLIT);

        Version version = domainDao.getVersionForTransaction(Integer.parseInt(pair[1]), threadId);
        if (version != null) {
            result = version.toString();
        } else {
            Account account = domainDao.getAccount(Integer.parseInt(pair[1]));
            result = account != null ? account.toString() : SERVER_RECORD_NOT_FOUND_MESSAGE;
        }

        return result;
    }

    private String tryAssignStatement(String[] tokens, long threadId) throws InterruptedException {
        String[] id = tokens[0].split(MESSAGE_ASSIGN_SPLIT);
        String[] total = tokens[1].split(MESSAGE_ASSIGN_SPLIT);

        Version writeVersion;

        do {
            LOG.info("Request resource...");
            writeVersion = versionService.requestWriteLock(parseInteger(id[1]), threadId, parseDouble(total[1]));
            Thread.sleep(1000);
        } while (Objects.nonNull(writeVersion));

        return SERVER_UPDATE_ACCOUNT_MESSAGE;
    }


    private Integer parseInteger(String integer) {
        return Integer.parseInt(integer);
    }

    private Double parseDouble(String doubleString) {
        return Double.parseDouble(doubleString);
    }

    public void setVersionService(VersionService versionService) {
        this.versionService = versionService;
    }

    public void setDomainDao(DomainDao domainDAO) {
        this.domainDao = domainDAO;
    }

    private enum EndStatemet {
        COMMIT("commit"),
        ROLLBACK("rollback");

        String command;

        EndStatemet(String command) {
            this.command = command;
        }
    }
}
