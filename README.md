# pdf-encrypt
Try with Spring WebFlux

# Build Jar
./mvnw package && java -jar target/pdf-encrypt.jar

# Build and tag Docker file
docker build -t vitor/pdf-encrypt-docker .

# Run
docker run -p 8080:8080 vitor/pdf-encrypt-docker
