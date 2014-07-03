transaction-retry
======================

Play1 module to automatically retry the request if a transaction fails because of a locking issue.

Locking issue may look [like this](http://stackoverflow.com/questions/17747906/getting-deadlock-found-when-trying-to-get-lock-try-restarting-transaction) or throw error like this: 
```
PersistenceException occured : org.hibernate.exception.LockAcquisitionException: Could not execute JDBC batch update

play.exceptions.JavaExecutionException: org.hibernate.exception.LockAcquisitionException: Could not execute JDBC batch u
pdate
        at play.mvc.ActionInvoker.invoke(ActionInvoker.java:237)
        at Invocation.HTTP Request(Play!)
Caused by: javax.persistence.PersistenceException: org.hibernate.exception.LockAcquisitionException: Could not execute J
DBC batch update
        at org.hibernate.ejb.AbstractEntityManagerImpl.convert(AbstractEntityManagerImpl.java:1389)
        at org.hibernate.ejb.AbstractEntityManagerImpl.convert(AbstractEntityManagerImpl.java:1317)
        at org.hibernate.ejb.AbstractEntityManagerImpl.convert(AbstractEntityManagerImpl.java:1323)
        at org.hibernate.ejb.AbstractEntityManagerImpl.flush(AbstractEntityManagerImpl.java:965)
        at play.db.jpa.JPABase._save(JPABase.java:41)
        at play.db.jpa.GenericModel.save(GenericModel.java:215)
        ... 1 more
Caused by: org.hibernate.exception.LockAcquisitionException: Could not execute JDBC batch update
        at org.hibernate.exception.SQLStateConverter.convert(SQLStateConverter.java:107)
        at org.hibernate.exception.JDBCExceptionHelper.convert(JDBCExceptionHelper.java:66)
        at org.hibernate.jdbc.AbstractBatcher.executeBatch(AbstractBatcher.java:275)
        ... 9 more
Caused by: java.sql.BatchUpdateException: Deadlock found when trying to get lock; try restarting transaction
        at com.mysql.jdbc.PreparedStatement.executeBatchSerially(PreparedStatement.java:2034)
        at com.mysql.jdbc.PreparedStatement.executeBatch(PreparedStatement.java:1468)
        at org.hibernate.jdbc.BatchingBatcher.doExecuteBatch(BatchingBatcher.java:70)
        at org.hibernate.jdbc.AbstractBatcher.executeBatch(AbstractBatcher.java:268)
        ... 15 more
Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException: Deadlock found when trying to get lock; tr
y restarting transaction
        at com.mysql.jdbc.Util.handleNewInstance(Util.java:411)
        at com.mysql.jdbc.Util.getInstance(Util.java:386)
        at com.mysql.jdbc.SQLError.createSQLException(SQLError.java:1064)
        ... 18 more
```
This is particularly helpful if you are using a heavy duty database that have row-level locking (such as Mysql's INNODB).

The module intercepts **locking related exceptions** and retries to process the request a certain number of time to give the database the time to resolve the locking issues.

Locking related exceptions are exceptions that are of the following types (or have a `cause` of the following types): 
  - `javax.persistence.OptimisticLockException`
  - `org.hibernate.StaleStateException`
  - `org.hibernate.exception.LockAcquisitionException`

This module is based on code provided on this [Stackoverflow question](http://stackoverflow.com/questions/7322297/playframework-catch-a-deadlock-and-reissue-transaction/8646105#8646105).

How to use
==========

Add the following to your `dependencies.yml` : 

``` yaml
require:
    ...
	- customModules -> transaction-retry 0.9.1
    
repositories:
    - githubModules:
        type: http
        artifact: "https://github.com/PerfectCarl/play-transaction-retry/raw/master/dist/[module]-[revision].zip"
        contains:
            - customModules -> *
```

Configuration
=============

The `transaction-retry.max` sets the number of time a request is retried.

It can be set in your `application.conf` : 
```
# DB locking management
# ~~~~~
transaction-retry.max=5
```
The *default value* is 3.

If `transaction-retry.max` is set to 0, the module will repost the request till it succeeds (ie, till the exception disappears). This may lead to infinite loops **damaging your server performance**.

How to build
============

```
 play deps
 play build-module
```
