#!/bin/sh
set -e

# Default values
export SERVER_PORT="${SERVER_PORT:-8089}"

# Radius database configuration
export RADIUS_DB_HOST="${RADIUS_DB_HOST:-localhost}"
export RADIUS_DB_PORT="${RADIUS_DB_PORT:-3306}"
export RADIUS_DB_NAME="${RADIUS_DB_NAME:-radius}"
export RADIUS_DB_USER="${RADIUS_DB_USER:-radius}"
export RADIUS_DB_PASSWORD="${RADIUS_DB_PASSWORD:-radiuspass}"

# RadMan internal database configuration
export RADMAN_DB_HOST="${RADMAN_DB_HOST:-localhost}"
export RADMAN_DB_PORT="${RADMAN_DB_PORT:-3306}"
export RADMAN_DB_NAME="${RADMAN_DB_NAME:-radman}"
export RADMAN_DB_USER="${RADMAN_DB_USER:-radman}"
export RADMAN_DB_PASSWORD="${RADMAN_DB_PASSWORD:-radmanpass}"

# LDAP configuration (optional)
export LDAP_ENABLED="${LDAP_ENABLED:-false}"
export LDAP_URLS="${LDAP_URLS:-ldap://localhost:389/}"
export LDAP_MANAGER_DN="${LDAP_MANAGER_DN:-cn=admin,dc=example,dc=com}"
export LDAP_MANAGER_PASSWORD="${LDAP_MANAGER_PASSWORD:-admin}"
export LDAP_SEARCH_BASE_DN="${LDAP_SEARCH_BASE_DN:-dc=example,dc=com}"
export LDAP_USER_SEARCH_FILTER="${LDAP_USER_SEARCH_FILTER:-(uid={0})}"

# Liquibase configuration
export LIQUIBASE_ENABLED="${LIQUIBASE_ENABLED:-true}"

# Logging configuration
export LOG_FILE="${LOG_FILE:-/var/log/radman}"
export LOG_LEVEL_ROOT="${LOG_LEVEL_ROOT:-WARN}"
export LOG_LEVEL_SPRING="${LOG_LEVEL_SPRING:-INFO}"
export LOG_LEVEL_RADMAN="${LOG_LEVEL_RADMAN:-INFO}"

# Production mode
export VAADIN_PRODUCTION_MODE="${VAADIN_PRODUCTION_MODE:-true}"

echo "Generating radman.properties configuration file..."

# Use envsubst to substitute environment variables in the template
envsubst < /app/config/radman.properties.template > /app/config/radman.properties

echo "Configuration file generated successfully."
echo "Starting RadMan application..."

# Execute the main command
exec "$@"
