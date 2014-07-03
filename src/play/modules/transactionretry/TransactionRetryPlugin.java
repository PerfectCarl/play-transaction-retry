package play.modules.transactionretry;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.transactionretry.enhancers.TransactionRetryEnhancer;

final public class TransactionRetryPlugin extends PlayPlugin {

    TransactionRetryEnhancer enhancer = new TransactionRetryEnhancer();

    @Override
    public void onApplicationStart() {
        super.onApplicationStart();
    }

    @Override
    public void enhance(ApplicationClass applicationClass) throws Exception {
        enhancer.enhanceThisClass(applicationClass);
    }

}