#!/bin/bash

echo " Iniciando setup do projeto Barbearia..."

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo " Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Copiar arquivo de ambiente se não existir
if [ ! -f .env ]; then
    echo " Criando arquivo .env a partir de .env.example..."
    cp .env.example .env
    echo "  Por favor, edite o arquivo .env com suas configurações antes de continuar."
    exit 1
fi

# Verificar se as variáveis de ambiente estão definidas
if ! grep -q "JWT_SECRET" .env || [ -z "$(grep "JWT_SECRET" .env | cut -d '=' -f2)" ]; then
    echo " JWT_SECRET não está definido no arquivo .env"
    exit 1
fi

# Parar containers existentes
echo " Parando containers existentes..."
docker-compose down

# Build e execução com Docker
echo " Construindo e iniciando containers..."
docker-compose up --build -d

echo " Aguardando serviços inicializarem..."
sleep 15

# Verificar se os serviços estão saudáveis
echo "🔍 Verificando status dos serviços..."
for i in {1..30}; do
    if docker-compose ps | grep -q "Up" && curl -s http://localhost:8080/actuator/health > /dev/null; then
        echo " Aplicação Spring Boot está respondendo!"
        break
    elif [ $i -eq 30 ]; then
        echo " Timeout aguardando aplicação ficar disponível"
        docker-compose logs app
        exit 1
    else
        echo " Aguardando aplicação... ($i/30)"
        sleep 5
    fi
done

echo "📊 Status final dos containers:"
docker-compose ps

echo ""
echo " Setup concluído com sucesso!"
echo " API disponível em: http://localhost:8080"
echo " MySQL disponível em: localhost:3307"
echo "  Redis disponível em: localhost:6380"
echo ""
echo " Para ver os logs: docker-compose logs -f"
echo " Para parar: docker-compose down"
echo ""