FROM azul/zulu-openjdk-alpine:8

COPY target/cache-demo.jar /

ENV CACHE_MODE = ""
ENV RUN_MODE = ""

CMD java -jar cache-demo.jar $CACHE_MODE $RUN_MODE