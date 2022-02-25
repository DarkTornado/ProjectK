# Project Ki

© 2021-2022 Dark Tornado, All rights reserved.

* `Ki`는 `케이아이`라고 읽어요.
* 화면 최상단에서 항상 대기중인 버튼을 누르면 실행되는 음성인식을 통해 여러가지 동작을 수행할 수 있어요.

## To do

* [x] 메인 화면
* [x] 상시 대기 서비스
* [x] 설치된 앱 실행
* [x] 전화 걸기
* [x] 검색
  * 네이버
  * 다음
  * 구글
  * 사용자 지정
* [x] 길찾기
* [x] 날씨 정보
* [x] 블루투스 제어
* [x] 전철 노선도
* [x] 실시간 버스 운행정보
* [x] 수신된 카카오톡 내용 읽기
  * [ ] 알림 구조 변경 대응
* [x] 카카오톡 메시지가 수신된 채팅방으로 응답 전송
* [x] 맛집 정보
  * [x] 주변 맛집 정보
* [x] 코로나19 실시간 확진 정보
* [x] [Scriptable AI (with luaj)](ScriptableAI.md)
  * [x] 내부 API 구현
  * [x] 문자열 split 구현
  * [x] json 문자열 처리 구현

## 필요 라이브러리
* gradle 등에 추가하거나, .jar 파일을 받아서 직접 넣어줘야 하는 라이브러리들입니다.
* [jsoup](https://jsoup.org/) - MIT License
* [luaj](http://www.luaj.org/luaj/3.0/README.html) - MIT License

## 사용 라이브러리
* jsoup
* luaj
* SimpleRequester
* LicenseView
* Josa.java

## 라이선스

* [GPL 3.0](LICENSE)
* 상업적 이용을 금지하는 저작권자의 방침이 라이선스보다 우선적으로 적용됩니다.

## 기타 참고 사항
* 빌드할 때 `Target API` 버전을 `29` 이하로 설정해주세요.

## 관련 레포지토리
* [Project Ki Back-End](https://github.com/DarkTornado/ProjectK-BackEnd) - Project K에서 사용하는 Rest API 등 구현

