MVN = mvnw

.PHONY: build-apps local down

build-apps:
	@echo "--- Building JAR files with Maven ---"
	$(MVN) clean package -DskipTests

local: build-apps
	@echo "--- Starting Docker containers ---"
	docker-compose up -d --build

down:
	docker-compose down