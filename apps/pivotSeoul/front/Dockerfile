FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
ENV NEXT_PUBLIC_BASE_PATH=/pivot
ENV NEXT_PUBLIC_API_BASE_URL=/pivot
RUN npm run build

# react-router 클라이언트 라우팅 SPA이므로 모든 경로가 동일한 셸(index.html)을
# 받아야 한다. Next의 옵셔널 catch-all export 결과물을 index.html로도 둔다.
RUN cp "out/[[...slug]].html" out/index.html

FROM nginx:alpine

COPY --from=build /app/out /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
