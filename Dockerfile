# Use uma imagem base com Java 17 compatível (baseada em Ubuntu para melhor estabilidade)
FROM eclipse-temurin:17-jdk-focal

# Defina o diretório de trabalho no container
WORKDIR /app

# Copie os arquivos Java da pasta backend/src para o container
COPY backend/src/*.java /app/

# Copie a pasta page1 (com HTML, CSS, etc.) para o container
COPY page1 /app/page1

# Compile todos os arquivos Java
RUN javac *.java

# Exponha a porta 8080 (usada pelo seu servidor)
EXPOSE 8080

# Comando para rodar o servidor
CMD ["java", "Server"]