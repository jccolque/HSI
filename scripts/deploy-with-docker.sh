#!/bin/sh
set -o errexit ; set -o nounset

### Este script despliega un ambiente docker con las especificaciones necesarias en los ambientes docker
### Configura: 
###    Traefik y su red, Glowroot, Nombre de ambiente segun convencion de ReviewApps, 
###    marca el ambiente como temporal o no, volumen de mocks cache, restart automatico, properties externalizadas
export ENV_NAME=$1
export DOCKER_IMAGE=$2
export REVIEW_APP_URL=$3
# optional parameter with default value
export TEMPORAL=${4:-true}
GLOWROOT_PROJECT="sgh"

if [ -z $ENV_NAME ] || [ -z $DOCKER_IMAGE ] || 
   [ -z $REVIEW_APP_URL ] || [ -z $TEMPORAL ] ; then
  echo 'Deploy script: one or more parameters are undefined'
  exit 1
fi

# Download new image
docker pull ${DOCKER_IMAGE} || 
    (echo "ERROR: La imagen de docker no existe, corra el \"build docker\" de nuevo y reintente" && exit 1)
# Kill old name convention environment
docker rm -f reviewapp-${ENV_NAME} 2> /dev/null || true
# Rename previous environment (if exists), avoid deadlocks killing multiple olds environments 
docker rm -f ${ENV_NAME}_old 2> /dev/null || true
docker rename ${ENV_NAME} ${ENV_NAME}_old 2> /dev/null || true
# Start environment
echo Crea environment con hash: $(
    docker create -P  \
        -l traefik.frontend.rule=Host:${REVIEW_APP_URL} \
        -l traefik.port=8280 \
        -l temporal=${TEMPORAL} \
        --network=web \
        --restart=always \
        -e GLOWROOT_PROJECT=${GLOWROOT_PROJECT} \
        -e GLOWROOT_AGENT_ID="${ENV_NAME}" \
        --name ${ENV_NAME} \
        ${DOCKER_IMAGE} #docker image to run
)
docker cp /tmp/${ENV_NAME}.properties ${ENV_NAME}:/app/env.properties 
echo Inicia environment con name: $(
    docker start ${ENV_NAME}
)
# Stop previous environment (if exists) 
echo Destruye ambiente anterior con name: $(
    docker rm -f ${ENV_NAME}_old 2> /dev/null || true
)