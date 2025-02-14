FROM node:22

WORKDIR /app

# package.json과 package-lock.json 복사
COPY vue-app/package*.json ./

# 종속성 설치
RUN npm install --legacy-peer-deps

# 전체 소스 코드 복사
COPY vue-app/ .

# Vite 글로벌 설치
RUN npm install -g vite

# 개발 서버 실행
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

EXPOSE 5173