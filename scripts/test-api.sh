#!/usr/bin/env bash
BASE="http://localhost:8000"

echo "Health"
curl "$BASE/api/health"
echo

echo "Restaurants"
curl "$BASE/api/restaurants"
echo

echo "Tables"
curl "$BASE/api/tables"
echo

echo "Reservations"
curl "$BASE/api/reservations"
echo
