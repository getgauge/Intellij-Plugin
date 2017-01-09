version=$(ls Gauge-Java-Intellij-*.zip | sed "s/^Gauge-Java-Intellij-\([^;]*\).zip/\1/")

curl -k -i --request POST -F userName="$username" -F password="$password" -F channel="$channel" -F pluginId=7535 -F file="@Gauge-Java-Intellij-$version.zip" https://plugins.jetbrains.com/plugin/uploadPlugin