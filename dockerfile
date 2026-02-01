FROM eclipse-temurin:21-jdk

# Installer Docker CLI pour permettre "docker exec", "docker start", etc.
RUN apt update && apt install -y docker.io && apt clean

# Créer dossier du bot
WORKDIR /bot

# Copier ton JAR
ADD https://github.com/maxWTRS2/Bot-Discord/releases/latest/download/botDiscord-1.0-all.jar /bot/botDiscord-1.0-all.jar

# Lancer le bot
CMD ["java", "-jar", "/bot/botDiscord-1.0-all.jar"]
