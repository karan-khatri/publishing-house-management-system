#!/bin/bash
# filepath: /home/dev/projects/swam/getting-started/run.sh

# PostgreSQL Database Configuration
export POSTGRESQL_USER=swamuser
export POSTGRESQL_PASSWORD=swampass
export POSTGRESQL_URL=jdbc:postgresql://localhost:5432/swamdb
export POSTGRESQL_DATABASE=swamdb

# Alternative environment variable names that WildFly might look for
export OPENSHIFT_POSTGRESQL_DB_USERNAME=swamuser
export OPENSHIFT_POSTGRESQL_DB_PASSWORD=swampass

echo "✅ PostgreSQL environment variables exported successfully!"
echo "Database: $POSTGRESQL_DATABASE"
echo "User: $POSTGRESQL_USER"
echo "Host: localhost:5432"

echo ""
echo "🔨 Building application with Maven..."
mvn clean package wildfly:package -DskipTests

# Check if the server was created successfully
if [ -f "./target/server/bin/standalone.sh" ]; then
    echo "✅ WildFly server provisioned successfully!"
    echo "🚀 Starting WildFly server..."
    echo "📍 Server will be available at: http://localhost:8080"
    echo "🔧 Management console: http://localhost:9990"
    echo "🛑 Press Ctrl+C to stop the server"
    echo ""
    
    # Start the WildFly server
    ./target/server/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
else
    echo "❌ Error: WildFly server was not created. Check Maven build logs above."
    echo "💡 Make sure you have the WildFly Maven plugin configured in your pom.xml"
    exit 1
fi