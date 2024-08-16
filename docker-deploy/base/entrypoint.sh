#!/bin/bash
# all container start the sshd
sudo service ssh start

# check for prerequisite servers
function check_service()
{
  local server=$1
  local retry_seconds=5
  local max_try=50
  let cur_try=1

  curl -s $server > /dev/null
  result=$?

  until [ $result -eq 0 ]; do
    echo "[$cur_try/$max_try] check for $server..."
    if [ $cur_try -eq $max_try ]; then
      echo "[$cur_try/$max_try] $server is still not available, exit...."
      exit 1
    fi

    sleep $retry_seconds
    let "cur_try++"

    host=$(echo "$server" | sed 's/:.*//')
    ip=$(echo "$server" | sed 's/.*://')
    nc -z $host $ip
    result=$?
  done
  echo "[$cur_try/$max_try] $server is available."
}

# check all prerequisite server
prerequisite_servers=$(echo $PREREQUISITE_SERVERS | sed 's/:/ /g')
prerequisite_servers=$(echo $prerequisite_servers | sed 's/-/:/g')

for prerequisite_server in ${prerequisite_servers[@]}
do
  check_service $prerequisite_server
done

exec $@
