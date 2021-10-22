# Getshop PMS v4  - PHP frontend and java backend

## Build instructions

Setup instructions for building with Gradle

This code has been adapted to compile using Gradle version 6.7/6.8

Create all the jars in the artifacts/builds folder by running './scripts/assemble.sh <version-name>' from the root folder of the main project.

### First time, create the gradle wrapper

`gradle wrapper`

### When upgrading Gradle, update the wrapper

`./gradlew wrapper --gradle-version 6.7` Change to the new version

### Build from commandline

`./gradlew clean assembleGetshop`

### Enlist customers for v5 redirection

Run and update the PMS_V5_CUSTOMERS array with a text editor

 `cp com.getshop.client/ROOT/v5customers.php ~/thundashopimages`
