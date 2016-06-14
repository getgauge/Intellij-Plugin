go get -v -u github.com/aktau/github-release

if [ -z "$GITHUB_TOKEN" ]; then
  echo "GITHUB_TOKEN is not set"
  exit 1
fi

cd artifacts

version=$(ls Gauge-Java-Intellij-*.zip | sed "s/^Gauge-Java-Intellij-\([^;]*\).zip/\1/")
repoName="Intellij-Plugin"
releaseName="Gauge Intellij Plugin $version"
artifact="Gauge-Java-Intellij-$version.zip"

releaseDescription=$(ruby -e "$(curl -sSfL https://github.com/getgauge/gauge/raw/master/build/create_release_text.rb)" $repoName)

$GOPATH/bin/github-release release -u getgauge -r "$repoName" --draft -t "v$version" -d "$releaseDescription" -n "$releaseName"

$GOPATH/bin/github-release -v upload -u getgauge -r "$repoName" -t "v$version" -n "$artifact" -f "$artifact"
