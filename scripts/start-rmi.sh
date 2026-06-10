#!/usr/bin/env bash
java -Djava.rmi.server.hostname=localhost -cp "build/classes:lib/*" sae.ServeurRestaurant 1099 ServiceRestaurant
