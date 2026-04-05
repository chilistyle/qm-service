#!/bin/bash
set -e

echo "Starting targeted restore to postgres..."

zcat /tmp/backups/02-keycloak-init.sql.gz | psql -U "$POSTGRES_USER" -d postgres

echo "Targeted restore finished!"