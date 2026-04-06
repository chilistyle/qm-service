MVN = mvnw

.PHONY: build-apps local native down

native:
	@echo "--- Building native images with Maven and Docker ---";\
    $(MVN) clean package -Pnative -DskipTests=true "-Dquarkus.container-image.build=true" "-Dquarkus.native.container-build=true" -pl library-service;\
    $(MVN) clean spring-boot:build-image -Pnative -DskipTests=true -pl api-gateway,book-service;\
    $(MVN) clean package -DskipTests=true -pl eureka-server;\
    docker build ./eureka-server;\
    docker-compose up -d
build-apps:
	@echo "--- Building JAR files with Maven ---"
	$(MVN) clean package -DskipTests

local: build-apps
	@echo "--- Starting Docker containers ---"
	docker-compose up -d --build

down:
	docker-compose down