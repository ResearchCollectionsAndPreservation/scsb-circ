## SCSB-CIRC

The SCSB Middleware codebase and components are all licensed under the Apache 2.0 license, with the exception of a set of API design components (JSF, JQuery, and Angular JS), which are licensed under MIT X11. 

SCSB-CIRC is a microservice application that provides the core functionalities for request scenarios. All the requests received are handled and processed in this application. Other major processes handled are Submit Collection, Bulk Request Process, Accession Reconciliation process, Status Reconciliation process, Daily Reconciliation process. All the SIP calls to the ILS and IMS are made through this application.

## Software Required

  - Java 11
  - Docker 19.03.13   
  
## Prerequisite

1.**Cloud Config Server**

Dspring.cloud.config.uri=http://phase4-scsb-config-server:8888

## Build

Download the Project , navigate inside project folder and build the project using below command

**./gradlew clean build -x test**

## Docker Image Creation

Naviagte Inside project folder where Dockerfile is present and Execute the below command

**sudo docker build -t  phase4-scsb-circ .**

## Docker Run

User the below command to Run the Docker

**sudo docker run --name phase4-scsb-circ  -v /data:/recap-vol --label collect_logs_with_filebeat="true" --label decode_log_event_to_json_object="true"  -p 9095:9095 -e "ENV= -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/recap-vol/scsb-circ/heapdump/  -Dorg.apache.activemq.SERIALIZABLE_PACKAGES="*" -Dspring.cloud.config.uri=http://phase4-scsb-config-server:8888 "  --network=scsb  -d phase4-scsb-circ**
