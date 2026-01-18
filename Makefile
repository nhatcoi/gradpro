# GradPro - Makefile
# Quản lý Docker và Development

.PHONY: help dev-db dev-server dev-client dev start stop logs clean build

# Default
help:
	@echo "GradPro - Hệ thống Quản lý Đồ án Tốt nghiệp"
	@echo ""
	@echo "Development:"
	@echo "  make dev-db       - Khởi động PostgreSQL"
	@echo "  make dev-server   - Chạy Spring Boot server"
	@echo "  make dev-client   - Chạy React frontend"
	@echo "  make dev          - Chạy tất cả (DB + Server + Client)"
	@echo ""
	@echo "Docker Production:"
	@echo "  make build        - Build tất cả Docker images"
	@echo "  make start        - Khởi động tất cả containers"
	@echo "  make stop         - Dừng tất cả containers"
	@echo "  make logs         - Xem logs"
	@echo "  make clean        - Xóa containers và images"
	@echo ""
	@echo "Database:"
	@echo "  make db-shell     - Truy cập PostgreSQL shell"
	@echo "  make pgadmin      - Khởi động pgAdmin"

# ============================================
# Development
# ============================================

# Khởi động PostgreSQL cho development
dev-db:
	docker compose -f docker-compose.dev.yml up -d
	@echo "PostgreSQL đang chạy tại localhost:5432"

# Chạy Spring Boot server (cần có Java và Maven)
dev-server:
	cd server && ./mvnw spring-boot:run

# Chạy React frontend
dev-client:
	cd client && npm run dev

# Chạy tất cả cho development
dev: dev-db
	@echo "Đợi database khởi động..."
	@sleep 5
	@echo "Database đã sẵn sàng. Chạy server và client..."
	@echo "Mở terminal mới chạy: make dev-server"
	@echo "Mở terminal mới chạy: make dev-client"

# ============================================
# Docker Production
# ============================================

# Build tất cả images
build:
	docker compose build

# Khởi động tất cả containers
start:
	docker compose up -d
	@echo ""
	@echo "Đang khởi động..."
	@sleep 10
	@docker compose ps
	@echo ""
	@echo "Frontend: http://localhost"
	@echo "Backend API: http://localhost:8080"
	@echo "pgAdmin: http://localhost:5050 (make pgadmin)"

# Dừng containers
stop:
	docker compose down

# Xem logs
logs:
	docker compose logs -f

logs-server:
	docker compose logs -f server

logs-client:
	docker compose logs -f client

# Xóa tất cả
clean:
	docker compose down -v --rmi local
	docker system prune -f

# ============================================
# Database
# ============================================

# Truy cập PostgreSQL shell
db-shell:
	docker exec -it gradpro-db psql -U postgres -d gradpro

# Khởi động pgAdmin
pgadmin:
	docker compose --profile tools up -d pgadmin
	@echo "pgAdmin: http://localhost:5050"
	@echo "Email: admin@gradpro.edu.vn"
	@echo "Password: admin123"

# ============================================
# Utilities
# ============================================

# Kiểm tra trạng thái
status:
	docker compose ps

# Restart server
restart-server:
	docker compose restart server

# Restart client
restart-client:
	docker compose restart client
