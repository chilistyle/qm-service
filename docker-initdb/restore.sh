#!/bin/bash
set -e

echo "Starting targeted restore to keycloak_db..."

zcat /tmp/backups/keycloak-init.sql.gz | psql -U "$POSTGRES_USER" -d keycloak_db

echo "Targeted restore finished!"