FROM node:22-alpine

WORKDIR /app

# Remove existing node_modules to avoid architecture conflicts
COPY . .
RUN npm install

EXPOSE 4200

CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
