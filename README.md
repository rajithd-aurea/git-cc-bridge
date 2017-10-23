## **Prerequisites**
* Java 1.8
* Gradle 3

## **Build Project**
```
./gradlew clean build
```

## **Setup Environment**
```
export GIT_CC_PATH_MAPPER_FILE=file:/path/to/clearcase-vob-mapper.yml
export GIT_CC_CLEARCASE_VIEW_NAME=
export GIT_CC_CLEARCASE_HOSTNAME=
export GIT_CC_CLEARCASE_USERNAME=
export GIT_CC_CLEARCASE_PASSWORD=
```

## **Deploy**
```
java -jar build/libs/git-cc-bridge-0.0.1-SNAPSHOT.jar
```

## **Configuration**
1. Update clearcase-vob-mapper.yml file with relevant vob mapping. Make sure to adhere yaml standards.
