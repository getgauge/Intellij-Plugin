go get -v -u github.com/aktau/github-release

if [ -z "$GITHUB_TOKEN" ]; then
  echo "GITHUB_TOKEN is not set"
  exit 1
fi

version=$(ls Gauge-Java-Intellij-*.zip | sed "s/^Gauge-Java-Intellij-\([^;]*\).zip/\1/")

$GOPATH/bin/github-release release -u getgauge -r "Intellij-Plugin" --draft -t "v$version" -d "## New Features: ## Enhancements: ## Bug Fixes:" -n "Gauge-Java-Intellij $version"

cd artifacts

$GOPATH/bin/github-release -v upload -u getgauge -r "Intellij-Plugin" -t "v$version" -n "Gauge Intellij Plugin $version" -f "Gauge-Java-Intellij-$version.zip"
