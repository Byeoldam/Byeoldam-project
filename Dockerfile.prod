# Java 17 버전으로 변경
FROM eclipse-temurin:17-jdk

# 작업 디렉터리 설정
WORKDIR /app

# Gradle 래퍼와 빌드 스크립트 복사
COPY ./gradlew . 
COPY ./gradle gradle
COPY ./build.gradle . 
COPY ./settings.gradle .

# Gradle 래퍼에 실행 권한 부여
RUN chmod +x gradlew

# 소스 코드 복사
COPY ./src src

# JAVA_HOME 환경변수 설정
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# wait-for-it 설치 (MySQL 준비 확인용)
RUN apt-get update && apt-get install -y wait-for-it

# Gradle 빌드 실행
RUN ./gradlew build -x test --no-daemon --warning-mode all

# 컨테이너 시작 시 실행할 명령 설정
CMD wait-for-it mysql:3306 -- java -jar /app/build/libs/byeoldam-0.0.1-SNAPSHOT.jar
