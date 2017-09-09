
# Kesako
Produce stats from a WhatsApp chat. 


# Install

## build deploy
````
docker-compose build
docker stack deploy -c docker-compose.yml wae
````

## configure mailin mediation (VOC)
```` 
MAILDEST=whatstat
WEBHOOK=wae_spring_app/api/conversation/mailhook
TYPE=file
docker service update voc_mailin_mediation --env-add $MAILDEST=$WEBHOOK --env-add ${MAILDEST}_TYPE=$TYPE
docker kill $(docker ps -q --filter "name=voc_mailin_mediation*")
docker service logs -f voc_mailin_mediation
````

visit http://localhost:8080/static/index.html

# Dev 

Add following to spring_app service
```` 
volumes:
 - '~/.gradle/:/root/.gradle'
 - './app/:/project/'
````

* Html is ready as it, just refresh page
* for java, recompile with 
`````
docker kill $(docker ps -q --filter "name=wae_spring_app*") 
`````


* get the logs
```` 
docker service logs -f wae_spring_app
````

* debug 
Add following to spring_app service
```` 
ports:
  - '5005:5005'
````

## Mock mail 

````
cd test
cat whatspam_extra_light.txt | base64 > temp
curl  -F TestFile=@"temp" -F mailinMsg=@"mailinMsg.json"  "localhost:8080/api/conversation/mailhook"
rm temp
````

