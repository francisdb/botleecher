cd ..
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
java -jar target/botleecher-1.0-SNAPSHOT-jar-with-dependencies.jar