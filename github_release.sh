go get -v -u github.com/aktau/github-release

if [ -z "$GITHUB_TOKEN" ]; then
  echo "GITHUB_TOKEN is not set"
  exit 1
fi

version=$(ls Gauge-Java-Intellij-*.zip | sed "s/^Gauge-Java-Intellij-\([^;]*\).zip/\1/")
repoName="Intellij-Plugin"
releaseName="Gauge Intellij Plugin $version"
artifact="Gauge-Java-Intellij-$version.zip"
releaseDescription="## New Features: ## Enhancements: ## Bug Fixes:"

$GOPATH/bin/github-release release -u getgauge -r "$repoName" --draft -t "v$version" -d "$releaseDescription" -n "$releaseName"

$GOPATH/bin/github-release -v upload -u getgauge -r "$repoName" -t "v$version" -n "$artifact" -f "$artifact"
