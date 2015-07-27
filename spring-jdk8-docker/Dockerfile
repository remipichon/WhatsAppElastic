edFROM java:8

# Gradle
ENV GRADLE_VERSION 2.5
WORKDIR /usr/bin
RUN wget "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" && \
    unzip "gradle-${GRADLE_VERSION}-bin.zip" && \
    ln -s "gradle-${GRADLE_VERSION}" gradle && \
    rm "gradle-${GRADLE_VERSION}-bin.zip"

# Set Appropriate Environmental Variables
ENV GRADLE_HOME /usr/bin/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

# Caches
VOLUME ["~/.gradle/caches"]

#TODO
#il faut soit chach les depencies du project, soit les embarquer dans l'image (je vais prendre cette option)


CMD cd /project && gradle build && java -jar build/libs/spring-data-elasticsearch-sample-1.0.jar 
#TODO pas ouf de devoir demarrer le .jar avec ce nom la, comment a fait Guillaume ?

# create image
#docker build -t kiki/spring-jdk8 .
# create container
#see restart_WAE...
# logs tail
#docker logs -f WAE

