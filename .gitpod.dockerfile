FROM gitpod/workspace-full-vnc:latest

RUN dpkg --add-architecture i386
RUN apt-get update && apt-get -y install libxext6 libxext6:i386 libfreetype6 libfreetype6:i386


USER gitpod

# activate java 11. It's already installed in the base image.
RUN  bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java"
