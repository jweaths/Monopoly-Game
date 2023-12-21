# Compile the project using Maven
mvn package

# Check if the build was successful
if [ $? -eq 0 ]
then
  echo "Maven build successful."
  # Run the Game class
  java -jar target/gs-maven-0.1.0.jar
else
  echo "Maven build failed."
fi
