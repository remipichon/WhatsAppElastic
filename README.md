
Docker
=============
Install Docker

from /elasticsearch-docker :

docker build -t kiki/spring-jdk8 .

./restart_es


from /spring-jdk8-docker :

docker build -t kiki/elasticsearch-marvel .

./restart_WAE



=> 192.168.59.103:8181/static/index.html

(change elasticsearch.host = 192.168.59.103 to localhost if you use Docker on a Linux, leave it if you use Boot2Docker for OsX)



With Vagrant (easiest)
===============

Install Vagrant from http://www.vagrantup.com/downloads

checkout this repo, with a CLI from the root of the repo (where the Vagrantfile is)

vagrant up

(if asked : ssh pwd : tcuser)

Once all is downloaded (a bit long the very first time)

localhost:8024/static/index.html



Whatsapp format
======================

Currently only one format is supported :

DD/MM/YYYY, HH:mm - AUTHOR NAME: Stuff said by the author (with emoji)




spring-data-elasticsearch-sample
================================

spring boot elasticsearch sample


  1. Install Elasticsearch

  2. Install Gradle

  3. IDE Eclipse or Intellij  IDEA

Step by Step Coding


En:  
      http://java.dzone.com/articles/first-step-spring-boot-and

Vi:   
      http://javadanang.com/497/spring-boot-ket-hop-voi-elasticsearch.html
