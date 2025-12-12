#!/bin/sh

# Calls the insert book endpoint.
#
# Usage: insert.sh <books>
# Example: insert.sh '[{"name":"John Doe","about":"The worlds best-selling author."}]'
#
# To change the url set the environment variable CLUSTER_URL
# Example: export CLUSTER_URL=https://my-url.com

if [ -z "$1" ]; then
  echo "Please specify an author list. Example: $0 '[{\"isbn\":\"2864330083\",\"about\":\"The worlds best-selling author.\"}]'" 2>&1
  exit 1
fi

url=${CLUSTER_URL:=http://localhost:8080}
curl -H "Content-Type:application/json" -d "$1" "$url/author"
