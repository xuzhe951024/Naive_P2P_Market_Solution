sh clear.sh
java -jar profileGenerator.jar ./config.yml
echo ./cs677.lab*.peer*.com | xargs -n 1 cp app.jar
docker-compose up -d
