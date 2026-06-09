#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

RMI_PORT="${RMI_PORT:-1099}"
RMI_NAME="${RMI_NAME:-restaurants}"
RMI_HOSTNAME="${RMI_HOSTNAME:-localhost}"

ant compile

exec java \
  -Djava.rmi.server.hostname="$RMI_HOSTNAME" \
  -cp "build/classes:lib/*" \
  com.example.server.ServeurRestaurant "$RMI_PORT" "$RMI_NAME"