package service;

import domain.Version;

import java.util.List;
import java.util.Objects;

import static util.ServerConstants.Messages.SERVER_ABORT_MESSAGE;
import static util.ServerConstants.Messages.SERVER_COMMIT_MESSAGE;

public class VersionService {

    DomainDao domainDaoInterface;

    public Version requestWriteLock(int id, long transactionId, double total) {
        Version writeVersion = domainDaoInterface.getVersionsByBlockType("write", id, transactionId);

        if (Objects.isNull(writeVersion)) {
            domainDaoInterface.createVersion(id, transactionId, "write", total);
        }

        return writeVersion;
    }


    public String commitChanges(long threadId) {
        List<Version> versions = domainDaoInterface.getVersionsByTransaction(threadId);

        if (versions.isEmpty()) {
            return "Nothing to commit";
        }

        for (Version version : versions) {
            domainDaoInterface.updateAccount(version.getAccount().getId(), version.getTotal());
            domainDaoInterface.deleteVersion(version);
        }

        return SERVER_COMMIT_MESSAGE;
    }

    public String abortTransaction(long id) {
        List<Version> versions = domainDaoInterface.getVersionsByTransaction(id);
        for (Version version : versions) {
            domainDaoInterface.deleteVersion(version);
        }

        return SERVER_ABORT_MESSAGE;
    }

    public boolean requestReadLock(int id, long threadId) {
        Version version = domainDaoInterface.getVersionForTransaction(id, threadId);

        if (version != null && version.getBlockType().equals("certify")) {
            return false;
        }

        return true;
    }

    public void setDomainDaoInterface(DomainDao domainDaoInterface) {
        this.domainDaoInterface = domainDaoInterface;
    }
}
