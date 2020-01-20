FROM azul/zulu-openjdk-alpine:8

COPY target/cache-demo.jar /

CMD java -jar cache-demo.jar