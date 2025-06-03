docker-compose rm -fsv jeecg-boot-system
docker images | grep 'jeecg-boot-system' | awk '{print $3}' | xargs -I {} docker rmi -f {}
# 重新启动服务
docker-compose up -d
