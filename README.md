play-transaction-retry
======================

Play1 module to automatically retry the request if a transaction fails because of a locking issue.

The module intercepts locking related exceptions and retries to process the request a certain number of time to give the database the time to resolve the locking issues.

Locking related exceptions are exceptions that are of the following types (or have a `cause` of the following types): 
  - `javax.persistence.OptimisticLockException`
  - `org.hibernate.StaleStateException`
  - `org.hibernate.exception.LockAcquisitionException`

This module is based on code provided on this [Stackoverflow question](http://stackoverflow.com/questions/7322297/playframework-catch-a-deadlock-and-reissue-transaction/8646105#8646105).

How to use
==========

Add the following to your `dependencies.yml`

``` yaml
require:
    ...
	- customModules -> transaction-retry 0.9
    
repositories:
    - githubModules:
        type: http
        artifact: "https://github.com/PerfectCarl/play-transaction-retry/raw/master/dist/[module]-[revision].zip"
        contains:
            - customModules -> *
```

Configuration
=============

The number of retries can be set in your `application.conf`: 
```
# DB locking management
# ~~~~~
transaction-retry.max=5
```
The default value is 3.

If `transaction-retry.max` is set to 0, the module will repost the request till it succeeds (ie, till the exception disappears).


How to build
============

```
 play deps
 play build-module
```
