FROM amazoncorretto:11

RUN mkdir -p /opt/application

WORKDIR /opt/application

COPY build/applicance-control-service-1.0.0-SNAPSHOT-runner.jar appliance-control-service.jar

ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-XshowSettings:vm", "-showversion", "-Daws.roleSessionName=appliance-control-service", "-Dsoftware.amazon.awssdk.http.service.impl=software.amazon.awssdk.http.apache.ApacheSdkHttpService", "-Dsoftware.amazon.awssdk.http.async.service.impl=software.amazon.awssdk.http.nio.netty.NettySdkAsyncHttpService", "-jar", "appliance-control-service.jar"]

CMD []