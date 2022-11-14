rm -rf ./cs677.lab*.peer*.com
rm docker-compose.yml
java -jar profileGenerator.jar ./config.yml
echo ./cs677*.com | xargs -n 1 cp app.jar
docker-compose up -d
