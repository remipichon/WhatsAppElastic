Vagrant.configure("2") do |config|
  config.vm.define "boot2dockerVagrant"
  config.vm.box = "yungsang/boot2docker"

  #Dockerfiles location
  config.vm.synced_folder "docker" , "/Dockerservices"

  #Gradle cache
  config.vm.synced_folder "~/.gradle/" , "/home/docker/.gradle/"
  #Source code for SpringBoot
  config.vm.synced_folder "." , "/project"


  #Forwarding Elasticsearch
  config.vm.network :forwarded_port, guest: 19200, host: 19200
  config.vm.network :forwarded_port, guest: 19300, host: 19300

  #Forwarding WAE Spring
  config.vm.network :forwarded_port, guest: 8181, host: 8024






  #sudo chmod 777 /etc/resolv.conf
  #/etc/resolv.conf  => nameserver 8.8.8.8


  #test
  #config.vm.provision :docker do |d|
  #  d.pull_images "yungsang/busybox"
  #  d.run "simple-echo",
  #   image: "yungsang/busybox",
  #   args: "-p 8080:8080",
  #   cmd: "nc -p 8080 -l -l -e echo hello world!"
  #end
  #config.vm.network :forwarded_port, guest: 8080, host: 8989

  #config.vm.provision "docker" do |d|
  #  #Proxy
  #  d.run "proxy",
  #  image: "ncarlier/redsocks",
  #  args: "--privileged=true --net=host",
  #  cmd: "proxy.priv.atos.fr 3128",
  #  daemonize: true
  #end


  #Elasticsearch build image
  config.vm.provision "docker" do |d|
    d.build_image "/Dockerservices/elasticsearch-docker",
    args: "-t kiki/elasticsearch-marvel"
  end


  #jdk8 with gradle build image
  config.vm.provision "docker" do |d|
    d.build_image "/Dockerservices/spring-jdk8-docker",
    args: "-t kiki/spring-jdk8"
  end


  #Elasticsearch run container
  config.vm.provision "docker" do |d|
    d.run "ES",
      image: "kiki/elasticsearch-marvel",
      args: "-d -p 19200:9200 -p 19300:9300"
  end


  #SpringBoot WAR run container
  config.vm.provision "docker" do |d|
    d.run "WAE",
      image: "kiki/spring-jdk8",
      args: "-d --link ES:ES -p 8181:80 -v /home/docker/.gradle/:/root/.gradle/ -v /project:/project"
  end

end
