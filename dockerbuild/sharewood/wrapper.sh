#!/bin/sh

while ! `nc -z $POSTGRESQLSERVER_HOST $POSTGRESQLSERVER_PORT`; do 
    echo "*********************************************************************"
    echo "Waiting for $POSTGRESQLSERVER_HOST server to start on port $POSTGRESQLSERVER_PORT"
    echo "*********************************************************************"
    sleep 10; 
done

echo `nc -z $POSTGRESQLSERVER_HOST $POSTGRESQLSERVER_PORT`
echo "Config Server $POSTGRESQLSERVER_HOST up and running at $POSTGRESQLSERVER_PORT"


./cnb/process/web


