sbt clean compile stage package universal:packageZipTarball
artefact=`ls -1 target/universal/*.tgz`
echo "deployable artefact: $artefact"
