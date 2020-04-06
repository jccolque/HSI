#!/bin/sh
set -o errexit
BASEDIR=$(dirname "$0")"/.."

# supone que ya se corrio build-pack
rm ci/docker/nginx-jar/*.jar || true
cp *.jar ci/docker/nginx-jar/
rm -r ci/docker/nginx-jar/front-end-dist || true
cp -r front-end/webapp/dist/ ci/docker/nginx-jar/front-end-dist
rm -r ci/docker/nginx-jar/backoffice-dist || true
cp -r front-end/backoffice/build/ ci/docker/nginx-jar/backoffice-dist

cd ci/docker/nginx-jar/
docker build . "$@"