FROM openjdk:latest

WORKDIR /app

COPY ./src /app

RUN javac PersonalNotesApp.java

ENTRYPOINT ["java", "PersonalNotesApp"]