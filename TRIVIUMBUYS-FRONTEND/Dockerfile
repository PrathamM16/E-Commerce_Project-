# Step 1: Build the Angular app
FROM node:20-alpine AS build
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build --prod

# Step 2: Serve using NGINX
FROM nginx:alpine
COPY --from=build /app/dist/* /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
