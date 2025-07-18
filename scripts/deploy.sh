#!/bin/bash
JAR_NAME="be-0.0.1-SNAPSHOT.jar"
APP_SERVICE_NAME="devblog-backend.service"

echo "> 현재 실행중인 애플리케이션 ($APP_SERVICE_NAME) 중지" >> /home/ubuntu/deploy.log
sudo systemctl stop $APP_SERVICE_NAME || true

echo "> 새 애플리케이션 배포 및 파라미터 로드" >> /home/ubuntu/deploy.log

export SPRING_DATASOURCE_URL=$(aws ssm get-parameter --name "/devblog/backend/db_url" --with-decryption --query Parameter.Value --output text)
export SPRING_DATASOURCE_USERNAME=$(aws ssm get-parameter --name "/devblog/backend/db_username" --with-decryption --query Parameter.Value --output text)
export SPRING_DATASOURCE_PASSWORD=$(aws ssm get-parameter --name "/devblog/backend/db_password" --with-decryption --query Parameter.Value --output text)
export JWT_SECRET_KEY=$(aws ssm get-parameter --name "/devblog/backend/jwt_secret_key" --with-decryption --query Parameter.Value --output text)
export AWS_ACCESS_KEY_ID=$(aws ssm get-parameter --name "/devblog/backend/aws_access_key_id" --with-decryption --query Parameter.Value --output text)
export AWS_SECRET_ACCESS_KEY=$(aws ssm get-parameter --name "/devblog/backend/aws_secret_access_key" --with-decryption --query Parameter.Value --output text)
export S3_BUCKET_NAME=$(aws ssm get-parameter --name "/devblog/backend/s3_bucket_name" --with-decryption --query Parameter.Value --output text)

ENV_FILE="/etc/default/devblog-backend-env"
echo "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}" > $ENV_FILE
echo "SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}" >> $ENV_FILE
echo "SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}" >> $ENV_FILE
echo "JWT_SECRET_KEY=${JWT_SECRET_KEY}" >> $ENV_FILE
echo "AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}" >> $ENV_FILE
echo "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}" >> $ENV_FILE
echo "S3_BUCKET_NAME=${S3_BUCKET_NAME}" >> $ENV_FILE

sudo chmod 600 $ENV_FILE
sudo chown root:root $ENV_FILE

sudo systemctl daemon-reload

echo "> 새로운 애플리케이션 버전으로 서비스 활성화 및 시작" >> /home/ubuntu/deploy.log
sudo systemctl enable $APP_SERVICE_NAME
sudo systemctl start $APP_SERVICE_NAME
echo "> $APP_SERVICE_NAME 서비스 시작됨" >> /home/ubuntu/deploy.log

sudo systemctl is-active $APP_SERVICE_NAME >> /home/ubuntu/deploy.log 2>&1
if [ $? -eq 0 ]; then
  echo "> $APP_SERVICE_NAME 서비스가 성공적으로 시작되었습니다." >> /home/ubuntu/deploy.log
else
  echo "> $APP_SERVICE_NAME 서비스 시작에 실패했습니다. 로그를 확인하세요." >> /home/ubuntu/deploy.log
  exit 1
fi