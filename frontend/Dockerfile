# stage: 1
FROM node:latest as frently-frontend
WORKDIR /app
COPY . ./
RUN npm install
RUN npm run build

# stage: 2 — the production environment
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=frently-frontend /app/build /usr/share/nginx/html
EXPOSE 80

CMD ["nginx","-g","daemon off;"]