set -e

# --- 변수 설정 ---
PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE=""
APP_NAME="devblog"
SERVICE_NAME="${APP_NAME}-backend.service"
ENV_FILE="/etc/default/${APP_NAME}-backend-env"
LOG_FILE="${PROJECT_ROOT}/deploy.log"

log() {
  echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a $LOG_FILE
}

stop_process() {
  log "========== 기존 프로세스 종료 시작 =========="
  
  JAR_FILE=$(find $PROJECT_ROOT -maxdepth 1 -name "*.jar" | head -n 1)
  if [ -z "$JAR_FILE" ]; then
    log "> 실행할 JAR 파일을 찾을 수 없습니다."
    exit 1
  fi
  log "> 타겟 JAR 파일: $JAR_FILE"

  CURRENT_PID=$(pgrep -f "$(basename "$JAR_FILE")" || true)

  if [ -z "$CURRENT_PID" ]; then
    log "> 현재 실행 중인 애플리케이션이 없습니다."
  else
    log "> 실행 중인 프로세스(PID: $CURRENT_PID) 종료"
    kill -15 $CURRENT_PID
    sleep 5
  fi
}

load_parameters_and_create_env() {
  log "========== AWS SSM 파라미터 로드 및 환경 파일 생성 시작 =========="
  
  TEMP_ENV_FILE=$(mktemp)

  PARAMS=(
    "db_url:SPRING_DATASOURCE_URL"
    "db_username:SPRING_DATASOURCE_USERNAME"
    "db_password:SPRING_DATASOURCE_PASSWORD"
    "jwt_secret_key:JWT_SECRET_KEY"
    "aws_access_key_id:AWS_ACCESS_KEY_ID"
    "aws_secret_access_key:AWS_SECRET_ACCESS_KEY"
    "s3_bucket_name:S3_BUCKET_NAME"
  )

  for item in "${PARAMS[@]}"; do
    SSM_KEY="${item%%:*}"
    ENV_VAR="${item##*:}"
    log "> 로딩: /${APP_NAME}/backend/${SSM_KEY}"
    VALUE=$(aws ssm get-parameter --name "/${APP_NAME}/backend/${SSM_KEY}" --with-decryption --query Parameter.Value --output text)
    if [ -z "$VALUE" ]; then
      log "오류: SSM에서 ${SSM_KEY} 값을 가져오지 못했습니다."
      rm $TEMP_ENV_FILE
      exit 1
    fi
    echo "${ENV_VAR}=${VALUE}" >> $TEMP_ENV_FILE
  done
  
  sudo mv $TEMP_ENV_FILE $ENV_FILE
  sudo chown root:root $ENV_FILE
  sudo chmod 600 $ENV_FILE
  log "> 환경 파일 생성 완료: ${ENV_FILE}"
}

start_and_validate() {
  log "========== 애플리케이션 시작 및 검증 시작 =========="
  
  sudo systemctl daemon-reload
  sudo systemctl start $SERVICE_NAME
  sudo systemctl enable $SERVICE_NAME

  log "> 서비스 시작 명령 실행 완료. 15초 후 상태 확인..."
  sleep 15

  STATUS=$(sudo systemctl is-active $SERVICE_NAME)
  if [ "${STATUS}" = "active" ]; then
    log "> 서비스가 성공적으로 시작되었습니다. (상태: ${STATUS})"
  else
    log "> 서비스 시작 실패. (상태: ${STATUS})"
    log "> journalctl 로그 확인:"
    sudo journalctl -u $SERVICE_NAME -n 50 --no-pager >> $LOG_FILE
    exit 1
  fi
}

# --- 메인 실행 로직 ---
log "#####################################################"
log "############### 배포 스크립트 시작 ###############"
log "#####################################################"

cd $PROJECT_ROOT
stop_process
load_parameters_and_create_env
start_and_validate

log "#####################################################"
log "############### 배포 스크립트 성공 ###############"
log "#####################################################"