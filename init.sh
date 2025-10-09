#!/bin/bash

echo "🚀 Iniciando setup do projeto Barbearia..."

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker primeiro."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado. Por favor, instale o Docker Compose primeiro."
    exit 1
fi

# Copiar arquivo de ambiente se não existir
if [ ! -f .env ]; then
    echo "📝 Criando arquivo .env a partir de .env.example..."
    cp .env.example .env
    echo "⚠️  Por favor, edite o arquivo .env com suas configurações antes de continuar."
    exit 1
fi

# Parar serviços conflitantes
if [ -f stop-local-services.sh ]; then
    chmod +x stop-local-services.sh
    ./stop-local-services.sh
fi

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose down

# Remover containers, networks e volumes
echo "🧹 Limpando ambiente Docker..."
docker-compose down -v --remove-orphans

# Build e execução com Docker
echo "🔨 Construindo e iniciando containers..."
docker-compose up --build -d

echo "⏳ Aguardando serviços inicializarem..."
sleep 10

# Verificar se os serviços estão saudáveis
echo "🔍 Verificando status dos serviços..."
for i in {1..30}; do
    if docker-compose ps | grep -q "Up (healthy)"; then
        echo "✅ Todos os serviços estão saudáveis!"
        break
    elif [ $i -eq 30 ]; then
        echo "❌ Timeout aguardando serviços ficarem saudáveis"
        docker-compose logs
        exit 1
    else
        echo "⏳ Aguardando serviços... ($i/30)"
        sleep 5
    fi
done

echo "📊 Status final dos containers:"
docker-compose ps

echo ""
echo "🎉 Setup concluído com sucesso!"
echo "📚 API disponível em: http://localhost:8080"
echo "🔑 MySQL disponível em: localhost:3307"
echo "🗄️  Redis disponível em: localhost:6380"
echo ""
echo "📝 Para ver os logs: docker-compose logs -f"
echo "🛑 Para parar: docker-compose down"
echo ""