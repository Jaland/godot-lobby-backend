build:
	mvn clean install

start:
	mvn quarkus:dev -Dquarkus.profile=local

build-and-start: build start

create-keys:
	mkdir -p jktKeys
	openssl genrsa -out jktKeys/private.pem 2048
	openssl rsa -in jktKeys/private.pem -pubout -out jktKeys/public.pem
	openssl pkcs8 -topk8 -nocrypt -inform pem -in jktKeys/private.pem -outform pem -out jktKeys/privatePKCS.pem