play-transaction-retry
======================

Play1 module to automatically retry if a transaction fails

How to use
==========

Add the following to your `dependencies.yml`

```
# Application dependencies

require:
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


How to build
============

```
 play deps
 play build-module
```
