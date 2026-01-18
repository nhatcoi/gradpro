# ============================================
# Stage 1: Build
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml và download dependencies trước (để tận dụng Docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================
# Stage 2: Run
# ============================================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Tạo user không phải root để chạy ứng dụng (bảo mật)
RUN groupadd -r gradpro && useradd -r -g gradpro gradpro

# Copy file JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục uploads
RUN mkdir -p /app/uploads && chown -R gradpro:gradpro /app

# Chuyển sang user không phải root
USER gradpro

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
