#!/bin/sh

cd ..
if [ ! -d "server" ]
then
	mkdir server
	cd server
	wget http://mirror.neu.edu.cn/eclipse/virgo/release/VP/3.6.3.RELEASE/virgo-tomcat-server-3.6.3.RELEASE.zip
	unzip virgo-tomcat-server-3.6.3.RELEASE.zip 
	rm virgo-tomcat-server-3.6.3.RELEASE.zip 
	cd -
else
	cd server/virgo-tomcat-server-3.6.3.RELEASE/bin
	./shutdown.sh
    cd -
fi
cd ${JOB_NAME}

cd dependencies
mvn clean install -s ../config/maven/settings.xml
mvn dependency:copy-dependencies -DexcludeTransitive=true -s ../config/maven/settings.xml -U
cd ..
cp -rf ./dependencies/target/dependency/* ../server/virgo-tomcat-server-3.6.3.RELEASE/repository/usr
cp -rf ./config/usr/* ../server/virgo-tomcat-server-3.6.3.RELEASE/repository/usr


cp -rf ./config/migrate/* ./database/
cd database

mvn migration:pending 
mvn migration:up  

cd ..
mvn clean install -Dmaven.test.skip=true -s ./config/maven/settings.xml
cp -rf kernel/com.onboard.*/target/*.jar ../server/virgo-tomcat-server-3.6.3.RELEASE/repository/usr
cp -rf plugins/com.onboard.*/target/*.jar ../server/virgo-tomcat-server-3.6.3.RELEASE/repository/usr
cp -rf test/com.onboard.test.*/target/*.jar  ../server/virgo-tomcat-server-3.6.3.RELEASE/repository/usr

cp -rf ./config/usr/org.eclipse.virgo.kernel.userregion.properties ../server/virgo-tomcat-server-3.6.3.RELEASE/configuration
cp -rf ./config/usr/tomcat-server.xml ../server/virgo-tomcat-server-3.6.3.RELEASE/configuration

cd ../server/virgo-tomcat-server-3.6.3.RELEASE/bin
export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=dev-email" 
./startup.sh &

cd -
kill $(ps aux | grep 'com.onboard.web.frontend' | awk '{print $2}')
cp -rf ./config/spring-boot/application.properties ./OnboardWeb/src/main/resources
cd OnboardWeb
mvn clean install -s ../config/maven/settings.xml
cd target
java -jar com.onboard.web.frontend-1.0-SNAPSHOT.jar &

ls
