# Stage 1: Build the application
FROM node:18-alpine AS build
# Security fix: upgrade Alpine packages to fix CVEs
RUN apk --no-cache upgrade && apk add --no-cache ca-certificates
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine
# Security fix: upgrade Alpine packages to fix CVEs
RUN apk --no-cache upgrade && apk add --no-cache ca-certificates
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
