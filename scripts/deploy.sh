#!/bin/bash
BUILD_JAR=$(ls /home/ubuntu/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

echo "> build 파일 복사" >> /home/ubuntu/deploy.log
DEPLOY_PATH=/home/ubuntu/
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> /home/ubuntu/app/deploy.log
  kill -15 $CURRENT_PID
  sleep 5
  if pgrep -f "$JAR_NAME" > /dev/null; then
    echo "> 프로세스가 5초 내에 종료되지 않았습니다. 강제 종료 (kill -9) 시도." >> /home/ubuntu/deploy.log
    kill -9 $CURRENT_PID
    sleep 3
  fi
fi

echo "> 새 애플리케이션 배포 및 파라미터 로드" >> /home/ubuntu/deploy.log

export SPRING_DATASOURCE_URL=$(aws ssm get-parameter --name "/devblog/backend/db_url" --with-decryption --query Parameter.Value --output text)
export SPRING_DATASOURCE_USERNAME=$(aws ssm get-parameter --name "/devblog/backend/db_username" --with-decryption --query Parameter.Value --output text)
export SPRING_DATASOURCE_PASSWORD=$(aws ssm get-parameter --name "/devblog/backend/db_password" --with-decryption --query Parameter.Value --output text)
export JWT_SECRET_KEY=$(aws ssm get-parameter --name "/devblog/backend/jwt_secret_key" --with-decryption --query Parameter.Value --output text)
export AWS_ACCESS_KEY_ID=$(aws ssm get-parameter --name "/devblog/backend/aws_access_key_id" --with-decryption --query Parameter.Value --output text)
export AWS_SECRET_ACCESS_KEY=$(aws ssm get-parameter --name "/devblog/backend/aws_secret_access_key" --with-decryption --query Parameter.Value --output text)
export S3_BUCKET_NAME=$(aws ssm get-parameter --name "/devblog/backend/s3_bucket_name" --with-decryption --query Parameter.Value --output text)


nohup java -jar \
-Dspring.datasource.url="${SPRING_DATASOURCE_URL}" \
-Dspring.datasource.username="${SPRING_DATASOURCE_USERNAME}" \
-Dspring.datasource.password="${SPRING_DATASOURCE_PASSWORD}" \
-Djwt.secret.key="${JWT_SECRET_KEY}" \
-Dcloud.aws.credentials.access-key="${AWS_ACCESS_KEY_ID}" \
-Dcloud.aws.credentials.secret-key="${AWS_SECRET_ACCESS_KEY}" \
-Dcloud.aws.s3.bucket="${S3_BUCKET_NAME}" \
$DEPLOY_PATH$JAR_NAME >> /home/ubuntu/deploy.log 2>/home/ubuntu/deploy_err.log &