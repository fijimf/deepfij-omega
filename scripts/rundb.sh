#!/bin/zsh

docker run -e POSTGRES_PASSWORD=p@ssw0rd -p 5432:5432 postgres:13-alpine