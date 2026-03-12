# Stage 1: Build Angular app with Node 22.14.0
FROM node:22.14.0 AS build

# Set working directory inside container
WORKDIR /usr/src/app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install -g @angular/cli && npm install

# Copy the rest of the source code
COPY . .

# Expose port for Angular dev server
EXPOSE 4200

# Start Angular dev server
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]
