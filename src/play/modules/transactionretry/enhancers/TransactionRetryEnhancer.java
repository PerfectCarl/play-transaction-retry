package play.modules.transactionretry.enhancers;

import javassist.CtClass;
import javassist.CtMethod;

import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.hibernate.StaleStateException;
import org.hibernate.exception.LockAcquisitionException;

import play.Invoker;
import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport;
import play.classloading.enhancers.Enhancer;
import play.db.jpa.JPA;
import play.mvc.Http;

public class TransactionRetryEnhancer extends Enhancer {

    private static final String PLUGIN_NAME = "transaction-retry";
    private static final String PLUGIN_MAX = PLUGIN_NAME + ".max";

    private static final long DEFAULT_MAX = 3;

    private static long retriesMax = -1;

    public static void process(PersistenceException e) throws PersistenceException {

        Logger.info("TransactionRetryEnhancer.process");
        if (isHandled(e) || isHandled(e.getCause())) {
            long max = getMaxRetries() ;
            long retries = parseLong(Http.Request.current().args.get("transaction-retry"));
            if (retries <= max || max == 0)
            {
                String message = "";
                if (e.getCause() != null)
                    message = e.getCause().getMessage();
                else
                    message = e.getMessage();
                Logger.info("TransactionRetryEnhancer.process: swallowed " + message + " (retries: " + retries + ")");
                final EntityTransaction tx = JPA.em().getTransaction();
                if (tx.isActive()) {
                    tx.setRollbackOnly();
                }
                Http.Request.current().isNew = false;

                retries++;
                Http.Request.current().args.put("transaction-retry", retries);
                throw new Invoker.Suspend(250);
            }
            else
            {
                Logger.info(PLUGIN_NAME + ": retry max (" + max
                        + ") exceeded. The exception is rethrowed. You may increase this value by setting '"
                        + PLUGIN_MAX + "' in you application.config");
            }
        }
        Http.Request.current().args.remove("transaction-retry");
        throw e;
    }

    private static long getMaxRetries() {
        if (retriesMax != -1)
            return retriesMax;
        final String max = Play.configuration.getProperty(PLUGIN_MAX);
        if (max == null)
            retriesMax= DEFAULT_MAX;
        try {
            retriesMax = Long.parseLong(max);
        } catch (Throwable e) {
            retriesMax = DEFAULT_MAX;
        }
        return retriesMax;
    }

    private static long parseLong(Object retries) {
        if (retries == null)
            return 1;
        long result = 1;
        try {
            result = Long.parseLong(retries.toString());
        } catch (Throwable e) {
            // Do nothing
        }
        return result;
    }

    private static boolean isHandled(final Throwable cause) {
        return cause instanceof OptimisticLockException || cause instanceof StaleStateException
                || cause instanceof LockAcquisitionException;
    }

    public void enhanceThisClass(final ApplicationClass applicationClass) throws Exception {

        final CtClass ctClass = makeClass(applicationClass);

        // enhances only Controller classes
        if (!ctClass.subtypeOf(classPool.get(ControllerSupport.class.getName()))) {
            return;
        }

        String entityName = ctClass.getName();

        for (final CtMethod ctMethod : ctClass.getDeclaredMethods()) {

            // Only enhance action
            if (ctMethod.getReturnType() == CtClass.voidType) {
                Logger.info(PLUGIN_NAME + ": enhancing method " + entityName + "." + ctMethod.getName());
                ctMethod.addCatch(
                        "play.modules.transactionretry.enhancers.TransactionRetryEnhancer.process(_e);return;",
                        classPool.makeClass("javax.persistence.PersistenceException"), "_e");
            }
        }
        // Done.
        applicationClass.enhancedByteCode = ctClass.toBytecode();

        ctClass.defrost();
    }
}