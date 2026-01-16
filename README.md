# backend-proto (Spring Boot 3.5.4 + Maven + MyBatis)

이 프로젝트는 **Spring Boot 3.5.4 / Java 17** 기반의 백엔드 템플릿입니다.  
(MyBatis, springdoc-openapi, logback 프로파일(local/dev/prod), 예제 API 포함)

---

## 빠른 시작

### 1) 준비물
- **Java 17**
- Maven이 없으면 **Maven Wrapper(mvnw)** 로 실행하면 됩니다.

### 2) 빌드
#### macOS / Linux
```bash
chmod +x mvnw
./mvnw -DskipTests package
```

#### Windows
```bat
mvnw.cmd -DskipTests package
```

### 3) 실행
DB 설정이 아직 없으면 **local-nodb** 프로파일로 서버만 먼저 띄우는 것을 권장합니다.

#### DB 없이 실행 (권장)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local-nodb
```

#### DB 설정 후 실행
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 접속 정보
- 기본 포트: **8081**
- Swagger UI: `http://localhost:8081/swagger-ui.html`

예제 API:
- `GET /api/sample/select-dto`
- `GET /api/sample/error` (BusinessException 테스트)

---

## 프로파일 / 설정 파일
- `application.yml` : 기본 설정 (default profile = `local`)
- `application-local.yml` : 로컬 DB 설정(현재 예시 값이 들어있음)
- `application-local-nodb.yml` : **DB 자동설정 제외**(DataSource/MyBatis AutoConfig 제외) → DB 없이 기동 가능
- `application-dev.yml`, `application-prod.yml` : Oracle 예시 설정(필요 시 HOST/계정 수정)

---

## 로그 설정 (logback)
프로파일별 logback 설정을 사용합니다.
- local / local-nodb : `logback-local.xml` (console)
- dev : `logback-dev.xml` (file)
- prod : `logback-prod.xml` (file)

파일 로그 경로는 `app.logging.file-path` 로 조정합니다(기본 `./logs`).

---

## MyBatis
- mapper xml: `classpath:/mappers/**/*.xml`
- type aliases: `com.shinhan.spp`

※ 현재 예제 프로젝트에는 Mapper/DAO가 최소 구성일 수 있으니, 실제 사용 시 `@Mapper` 인터페이스와 `mappers/*.xml`을 추가해서 확장하세요.

---

## Nexus 사용 (선택) - POM에 repositories 방식

사내 Nexus를 사용하려면 `pom.xml` 하단에 있는 아래 블록을 **주석 해제**하고,
`https://YOUR_NEXUS/repository/maven-public/` 를 실제 Nexus 주소로 바꾸세요.

- `<repositories>` : 일반 의존성 다운로드 경로
- `<pluginRepositories>` : Maven 플러그인 다운로드 경로

> `snapshots`는 `-SNAPSHOT` 버전을 사용할 때만 `true`로 켜는 것을 권장합니다.

---

## 참고
- 외부 인터넷이 차단된 환경에서 Maven Wrapper가 Maven 배포본을 받지 못하면,
  `.mvn/wrapper/maven-wrapper.properties` 의 URL을 사내 저장소로 바꿔야 할 수 있습니다.
