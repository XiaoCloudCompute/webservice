#!/bin/bash

systemctl stop web_service
rm -rf /home/ec2-user/web_spring.jar
rm -rf /opt/amazon-cloudwatch-agent.json