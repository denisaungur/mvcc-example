package service;

import domain.Account;
import domain.Version;

import java.util.List;

public interface DomainDao {

    Account getAccount(int id);

    void deleteVersion(Version version);

    void createVersion(int idAccount, long idTransaction, String blockType, double total);

    Version getVersionForTransaction(int accoutId, long idTransaction);

    List<Version> getVersionsByTransaction(long idTransaction);

    Version getVersionsByBlockType(String blockType, int accoutId, long transactionId);

    void updateAccount(int id, double total);


}
