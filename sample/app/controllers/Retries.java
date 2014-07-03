package controllers;

import java.sql.SQLException;
import java.util.Random;

import javax.persistence.OptimisticLockException;

import org.hibernate.StaleStateException;

import play.mvc.Controller;

public class Retries extends Controller {

    private static final int MAX_COUNT = 3;

    // FIXME: should this test be consistent - ie set seed or not?
    private static Random random = new Random();
    private static int err1 = -1;
    private static int err2 = -1;
    private static int err3 = -1;
    private static int err31 = -1;
    private static int err32 = -1;
    private static int err4 = -1;
    private static int err41 = -1;

    public static void err1()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err1 < MAX_COUNT)
        {
            err1++;
            throw new javax.persistence.PersistenceException("PersistenceException" + " count:" + err1);
        }
        renderText("HELLO");
    }

    public static void err2()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err2 < MAX_COUNT)
        {
            err2++;
            throw new javax.persistence.PersistenceException(new org.hibernate.exception.LockAcquisitionException(
                    "LockAcquisitionException" + " count:" + err2,
                    new SQLException("I've been a bad bad boy" + " count:" + err2)));
        }
        renderText("HELLO");
    }

    public static void err3()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err3 < 2)
        {
            err3++;
            throw new javax.persistence.PersistenceException(new OptimisticLockException("OptimisticLockException"
                    + " count:" + err3));
        }
        renderText("HELLO");
    }

    public static void err31()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err31 < 4)
        {
            err31++;
            throw new javax.persistence.PersistenceException(new OptimisticLockException("OptimisticLockException"
                    + " count:" + err31));
        }
        renderText("HELLO");
    }

    public static void err32()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err32 < 5)
        {
            err32++;
            throw new javax.persistence.PersistenceException(new OptimisticLockException("OptimisticLockException"
                    + " count:" + err32));
        }
        renderText("HELLO");
    }

    public static void err4()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err4 < 5)
        {
            err4++;
            throw new javax.persistence.PersistenceException(new StaleStateException("StaleStateException" + " count:"
                    + err4));
        }
        renderText("HELLO");
    }

    public static void err41()
    {
        response.setHeader("Cache-Control", "no-cache");
        if (err41 < 2)
        {
            err41++;
            throw new javax.persistence.PersistenceException(new StaleStateException("StaleStateException" + " count:"
                    + err41));
        }
        renderText("HELLO");
    }

    public static void err5()
    {
        response.setHeader("Cache-Control", "no-cache");
        int i = 5 / 0;
        renderText("HELLO");
    }

    public static void err6()
    {
        response.setHeader("Cache-Control", "no-cache");
        Object o = null;
        o.toString();
        renderText("HELLO");
    }
}