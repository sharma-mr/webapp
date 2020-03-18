echo "Running after install script"
cd /home/ubuntu/webapp
sudo chown -R ubuntu:ubuntu /home/ubuntu/*
sudo chmod +x demo-0.0.1-SNAPSHOT.jar

#Kill application if already running
kill -9 $(ps -ef|grep demo-0.0.1 | grep -v grep | awk '{print $2}')

source /etc/profile.d/envvariable.sh
#Running application and appending logs
nohup java -jar demo-0.0.1-SNAPSHOT.jar > /home/ubuntu/Webapplog.log 2> /home/ubuntu/Webapplog.log < /home/ubuntu/Webapplog.log &
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/webapp/cloudwatch-agent-schema.json -s