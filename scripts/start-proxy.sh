#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
java -cp "build/classes:lib/*" sae.ProxyServer 8000