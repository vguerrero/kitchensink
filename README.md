## Author
Victor Guerrero
## dependencies
* java 21
* docker and docker-compose

## Compile
go to root project and execute next command
```.
mvn clean install
```
then we need to download  docker images and start the spring-boot app
```.
docker-compose up --build

```
once app is running we can test it, 
open the explorer open http://localhost:8080/index.xhtml

controller and restservice work correctly as we can see in 
evidence folder
[evidence](evidence) <br>
Thank you