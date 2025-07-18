set -x

JAR_NAME="be-0.0.1-SNAPSHOT.jar"

DEPLOY_PATH="/home/ubuntu/"

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/deploy.log
else
  echo "> kill -15 $CURRENT_PID" >> /home/ubuntu/deploy.log
  kill -15 $CURRENT_PID
  sleep 5
  if pgrep -f "$JAR_NAME" > /dev/null; then
    echo "> 프로세스가 5초 내에 종료되지 않았습니다. 강제 종료 \(kill -9\) 시도." >> /home/ubuntu/deploy.log
    kill -9 $CURRENT_PID
    sleep 3
  fi
fi

echo "> 새 애플리케이션 배포 및 파라미터 로드" >> /home/ubuntu/deploy.log

export SPRING_DATASOURCE_URL=$(aws ssm get-parameter --name "/devblog/backend/db_url" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}" >> /home/ubuntu/deploy.log

export SPRING_DATASOURCE_USERNAME=$(aws ssm get-parameter --name "/devblog/backend/db_username" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}" >> /home/ubuntu/deploy.log

export SPRING_DATASOURCE_PASSWORD=$(aws ssm get-parameter --name "/devblog/backend/db_password" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}" >> /home/ubuntu/deploy.log

export JWT_SECRET_KEY=$(aws ssm get-parameter --name "/devblog/backend/jwt_secret_key" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "JWT_SECRET_KEY: ${JWT_SECRET_KEY}" >> /home/ubuntu/deploy.log

export AWS_ACCESS_KEY_ID=$(aws ssm get-parameter --name "/devblog/backend/aws_access_key_id" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "AWS_ACCESS_KEY_ID: ${AWS_ACCESS_KEY_ID}" >> /home/ubuntu/deploy.log

export AWS_SECRET_ACCESS_KEY=$(aws ssm get-parameter --name "/devblog/backend/aws_secret_access_key" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "AWS_SECRET_ACCESS_KEY: ${AWS_SECRET_ACCESS_KEY}" >> /home/ubuntu/deploy.log

export S3_BUCKET_NAME=$(aws ssm get-parameter --name "/devblog/backend/s3_bucket_name" --with-decryption --query Parameter.Value --output text 2>>/home/ubuntu/deploy_err.log)
echo "S3_BUCKET_NAME: ${S3_BUCKET_NAME}" >> /home/ubuntu/deploy.log

ENV_FILE="/etc/default/devblog-backend-env"
sudo sh -c "echo 'SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}' > $ENV_FILE"
sudo sh -c "echo 'SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}' >> $ENV_FILE"
sudo sh -c "echo 'SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}' >> $ENV_FILE"
sudo sh -c "echo 'JWT_SECRET_KEY=${JWT_SECRET_KEY}' >> $ENV_FILE"
sudo sh -c "echo 'AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}' >> $ENV_FILE"
sudo sh -c "echo 'AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}' >> $ENV_FILE"
sudo sh -c "echo 'S3_BUCKET_NAME=${S3_BUCKET_NAME}' >> $ENV_FILE"

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