
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
`````
docker-compose restart spring-app; docker-compose logs --tail 100 -f spring-app
`````

````
docker-compose build
docker stack deploy -c docker-compose.yml wae
````

## Mock mail 

````
cd test
cat whatspam_extra_light.txt | base64 > temp
curl  -F "attachment=@temp" -F "mailinMsg=$(cat mailinMsg.json)"  "localhost:8080/api/conversation/mailhook"
curl  -F TestFile=@"temp" -F mailinMsg=@"mailinMsg.json"  "localhost:8080/api/conversation/mailhook"
rm temp
````

"attachment" refers to mailinMsg.json 

## Attach to websocket

TODO


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
