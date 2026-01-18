# ============================================
# Makefile cho GradPro
# ============================================

.PHONY: help build run stop clean logs dev prod

# Mặc định
help:
	@echo "GradPro - Hệ thống Quản lý Đồ án Tốt nghiệp"
	@echo ""
	@echo "Các lệnh có sẵn:"
	@echo "  make dev          - Chạy PostgreSQL cho development"
	@echo "  make dev-stop     - Dừng PostgreSQL development"
	@echo "  make build        - Build Docker image"
	@echo "  make run          - Chạy toàn bộ hệ thống (app + db)"
	@echo "  make run-tools    - Chạy với pgAdmin"
	@echo "  make stop         - Dừng toàn bộ containers"
	@echo "  make logs         - Xem logs của app"
	@echo "  make logs-db      - Xem logs của database"
	@echo "  make clean        - Xóa containers và volumes"
	@echo "  make mvn-run      - Chạy app local với Maven"
	@echo "  make mvn-build    - Build JAR file"

# ============================================
# Development (chỉ chạy DB, app chạy local)
# ============================================
dev:
	docker-compose -f docker-compose.dev.yml up -d
	@echo "PostgreSQL đã sẵn sàng tại localhost:5432"
	@echo "Chạy: ./mvnw spring-boot:run"

dev-stop:
	docker-compose -f docker-compose.dev.yml down

# ============================================
# Production (chạy cả app và db)
# ============================================
build:
	docker-compose build

run:
	docker-compose up -d
	@echo "GradPro đang chạy tại http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"

run-tools:
	docker-compose --profile tools up -d
	@echo "GradPro đang chạy tại http://localhost:8080"
	@echo "pgAdmin: http://localhost:5050 (admin@gradpro.edu.vn / admin123)"

stop:
	docker-compose down

# ============================================
# Logs
# ============================================
logs:
	docker-compose logs -f app

logs-db:
	docker-compose logs -f postgres

logs-all:
	docker-compose logs -f

# ============================================
# Clean up
# ============================================
clean:
	docker-compose down -v --remove-orphans
	docker-compose -f docker-compose.dev.yml down -v --remove-orphans

clean-images:
	docker rmi gradpro-app 2>/dev/null || true

# ============================================
# Maven commands (chạy local)
# ============================================
mvn-run:
	./mvnw spring-boot:run

mvn-build:
	./mvnw clean package -DskipTests

mvn-test:
	./mvnw test

# ============================================
# Database
# ============================================
db-shell:
	docker exec -it gradpro-db psql -U postgres -d gradpro

db-backup:
	docker exec gradpro-db pg_dump -U postgres gradpro > backup_$$(date +%Y%m%d_%H%M%S).sql

# ============================================
# Status
# ============================================
status:
	docker-compose ps

health:
	curl -s http://localhost:8080/actuator/health | python3 -m json.tool
