version = $(ls Gauge-Java-Intellij-*.zip | sed "s/^Gauge-Java-Intellij-\([^;]*\).zip/\1/")

# create draft release on github
draft_release_test="## New Features: ## Enhancements: ## Bug Fixes:"
$GOPATH/bin/github-release release -u getgauge -r "Intellij-Plugin" --draft $draft_release_test -t "v$version" -d  -n "Gauge Intellij Plugin $version"

#upload artifacts
file_name="Gauge-Java-Intellij-"$version".zip"
$GOPATH/bin/github-release -v upload -u getgauge -r "Intellij-Plugin" -t "v$version" -n $file_name -f $file_name