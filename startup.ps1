$ErrorActionPreference = 'Stop'

try {
    docker info | Out-Null
}
catch {
    throw 'Docker daemon is not running. Please start Docker Desktop or the Docker service first.'
}

docker compose --env-file .env up --build -d
