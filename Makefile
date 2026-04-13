MVN = mvnw

.PHONY: build-apps local native down

build-apps:
	@echo "--- Building JAR files with Maven ---"
	docker compose build

local: build-apps
	@echo "--- Starting Docker containers ---"
	docker-compose up -d

down:
	docker-compose down

native:
	@echo "--- Building native images with Maven and Docker ---"
	$(MVN) clean package -Pnative -DskipTests=true "-Dquarkus.container-image.build=true" "-Dquarkus.native.container-build=true" -pl library-service
	docker build --no-cache -f api-gateway/Dockerfile.native -t api-gateway:1.0-SNAPSHOT .
	docker build --no-cache -f book-service/Dockerfile.native -t book-service:1.0-SNAPSHOT .
	$(MVN) clean package -DskipTests=true -pl eureka-server
	docker build ./eureka-server
	docker build ./next-app
	docker build ./comment-service
	docker-compose up -d