file="./gradle.properties"

if [ -f "$file" ]
then
  while IFS='=' read -r key value
  do
    key=$(echo $key | tr '.' '_')
    eval "${key}='${value}'"
  done < "$file"
    if [ "$channel" == "Nightly" ]
    then
      version=${version}.nightly-`date +%Y-%m-%d`
    fi

  text=`sed -e "/{username}/ s/=.*/=${username}/" -e "/{password}/ s/=.*/=${password}/" -e "/{channel}/ s/=.*/=${channel}/" -e "/version=/ s/=.*/=${version}/" gradle.properties`
  `echo "$text" > gradle.properties`

else
  echo "$file not found."
fi

