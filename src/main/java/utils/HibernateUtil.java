package utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    public static Session getHibernateSession() {
        String config = "criteria.cfg.xml";
        if (System.getProperty("ENV") != null && System.getProperty("ENV").equals("DEV")) {
            config = "criteria_test.cfg.xml";
        }
        SessionFactory sf = new Configuration().configure(config).buildSessionFactory();
        return sf.openSession();
    }

}
