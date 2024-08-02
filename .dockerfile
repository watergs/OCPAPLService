# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the fat JAR file to the container
COPY target/demo-0.0.1-SNAPSHOT.jar /app/app1.jar

# Expose the application port (change according to your app)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app1.jar", "-Dage-BALANCE-HOST=\"http://ocpageservice2-oc-test.apps-crc.testing\"", "-Dage-USER-HOST=\"http://ocpageservice1-oc-test.apps-crc.testing\""]
