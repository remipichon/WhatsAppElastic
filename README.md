
# Install


* install Docker and Docker Compose
````
docker-compose build 
docker-compose up -d
````

visit http://localhost:8080/static/index.html

# Dev 

volume is mounted

html is ready as it, just refresh page

for java, recompile with (40s)
docker-compose restart spring-app; docker-compose logs -f spring-app

# What is it 
Procuce stats from a WhatsApp discussion


## Whatsapp supported format

Currently following format are supported
````
DD/MM/YYYY, HH:mm - AUTHOR NAME: Stuff said by the author (with emoji)
annoying date format 
````



# Old stuff
docker run -d -p 8181:80  -p 5005:5005 --link ES:ES -v /home/docker/.gradle/:/root/.gradle/ -v /project:/project --name=WAE kiki/spring-jdk8
