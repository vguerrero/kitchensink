# Use the official OpenJDK base image
  FROM openjdk:21-jdk
  
  # Set the working directory inside the container
  WORKDIR /app
  
  # Copy the built jar file into the container
  COPY target/kitchensink-1.0.0.jar app.jar
  
  # Expose port 8080
  EXPOSE 8080
  
  # Run the application
  ENTRYPOINT ["java", "-jar", "app.jar"]
