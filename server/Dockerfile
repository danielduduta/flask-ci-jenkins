FROM docker.io/jenkins/jenkins:lts

USER root
RUN apt-get update
RUN apt-get install -y ca-certificates curl apt-transport-https apt-utils software-properties-common make gnupg2
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt-get update
RUN apt-get install -y docker-ce
RUN usermod -aG docker jenkins

# local issue
RUN groupmod -g 1001 docker
# end local issue

RUN chown -R jenkins:jenkins /var/jenkins_home

VOLUME [ "/var/jenkins_home" ]

USER jenkins

ENV CASC_JENKINS_CONFIG /usr/share/jenkins/ref/jenkins.yaml
ENV _BUILDAH_STARTED_IN_USERNS="" BUILDAH_ISOLATION=chroot
COPY files/jenkins.yaml /usr/share/jenkins/ref/jenkins.yaml
COPY files/InitialConfig.groovy /usr/share/jenkins/ref/init.groovy.d/InitialConfig.groovy
COPY files/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt
