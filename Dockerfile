FROM openjdk:11-jre-slim
WORKDIR /app
COPY build/libs/crypto-recommendations-1.0.0.RELEASE.jar /app/crypto-recommendations.jar
EXPOSE 8080
CMD ["java", "-jar", "crypto-recommendations.jar"]
