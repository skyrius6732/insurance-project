FROM jenkins/jenkins:lts

USER root

RUN apt-get update &&     apt-get install -y openjdk-17-jdk docker.io &&     apt-get clean &&     rm -rf /var/lib/apt/lists/*

USER jenkins

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

ENTRYPOINT ["/usr/local/bin/jenkins.sh"]

