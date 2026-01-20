#!/bin/bash

# Script para ejecutar el servidor BeUs Backend

# Activar entorno virtual
source .back/bin/activate

# Ejecutar servidor
echo "🚀 Iniciando BeUs Backend..."
echo "📚 Documentación: https://localhost:8443/docs"
echo "🔍 Health check: https://localhost:8443/health"
echo ""

python -m uvicorn app.main:app \
  --reload \
  --host 0.0.0.0 \
  --port 8443 \
  --ssl-keyfile=certs/key.pem \
  --ssl-certfile=certs/cert.pem
