# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

RUN npm run build -- --output-path=./dist/virtual-time-capsule-ui --configuration production


FROM nginx:alpine

# Copy the built Angular app from the build stage to Nginx's html directory
COPY --from=build /app/dist/virtual-time-capsule-ui /usr/share/nginx/html

# Copy a custom Nginx configuration (optional, but recommended for single-page applications)
# If your app uses client-side routing, this is crucial to handle direct URL access
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
