
chmod +x ./gradlew
chmod +x ./jar/genJar.sh
./gradlew  assembleRelease --no-daemon

./jar/genJar.sh

