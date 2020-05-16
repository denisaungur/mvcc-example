package service.impl;

import domain.Account;
import domain.Version;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import service.DomainDao;

import java.util.List;

@Transactional
public class DomainDaoImpl implements DomainDao {

    @Autowired
    private SessionFactory sessionFactory;

    public Account getAccount(int id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Account AS p WHERE p.id = :id";
        Query query = session.createQuery(hql);
        query.setParameter("id", id);
        return (Account) query.uniqueResult();
    }

    public void deleteVersion(Version version) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(version);
    }

    public void createVersion(int id, long transactionId, String blockType, double total) {
        Session session = sessionFactory.getCurrentSession();
        Version version = new Version();
        version.setBlockType(blockType);
        version.setTransactionId(Long.toString(transactionId));
        version.setAccount(getAccount(id));
        version.setTotal(total);
        session.save(version);
    }

    public Version getVersionForTransaction(int accountId, long idTransaction) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Version AS p WHERE p.account.id = :id AND p.transactionId = :transaction";
        Query query = session.createQuery(hql);
        query.setParameter("id", accountId);
        query.setParameter("transaction", Long.toString(idTransaction));
        Version version = (Version) query.uniqueResult();

        return version;
    }


    public List<Version> getVersionsByTransaction(long idTransaction) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Version AS p WHERE p.transactionId = :transaction";
        Query query = session.createQuery(hql);
        query.setParameter("transaction", Long.toString(idTransaction));

        return query.list();
    }

    public Version getVersionsByBlockType(String blockType, int accountId, long transactionId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Version AS v WHERE v.blockType = :blockType AND v.id=:accountId AND v.transactionId != :transactionId";
        Query query = session.createQuery(hql);
        session.setCacheMode(CacheMode.IGNORE);
        query.setParameter("blockType", blockType);
        query.setParameter("accountId", accountId);
        query.setParameter("transactionId", Long.toString(transactionId));
        Version version = (Version) query.uniqueResult();

        return version;
    }

    public void updateAccount(int id, double total) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "Update Account as a set a.total=:total where a.id=:idAccount";
        Query query = session.createQuery(hql);
        query.setParameter("total", total);
        query.setParameter("idAccount", id);
        query.executeUpdate();
    }
}
