#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
java -Djava.rmi.server.hostname=localhost -cp "build/classes:lib/*" sae.ServeurRestaurant 1099 ServiceRestaurant
