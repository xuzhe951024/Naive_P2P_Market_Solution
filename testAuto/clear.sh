docker image rm $(docker images | awk '{print $1}' | grep cs677.lab1)
rm -rf ./cs677.lab*.peer*.com
rm docker-compose.yml args host