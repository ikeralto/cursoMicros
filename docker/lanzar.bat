cd apoyo
docker-compose up -d
cd ..
timeout 30
docker run -d --rm -p 9411:9411 --name zipkin-curso --network curso-microservicios-network -e RABBIT_ADDRESSES=rabbitmq-curso -e STORAGE_TYPE=mysql -e MYSQL_USER=zipkin -e MYSQL_PASS=zipkin -e MYSQL_HOST=db-mysql-curso openzipkin/zipkin
cd microservicios
timeout 30
docker-compose up -d

cd ..

