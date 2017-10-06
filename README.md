Android Programing
----------------------------------------------------
### 2017.09.25 10일차

#### 공부정리
____________________________________________________

##### __Android Permission__

- Android Permission 이란?

  > 시스템 무결성과 사용자의 개인정보를 보호하기 위해 Android는 액세스가 제한된 샌드박스에서 각각의 APP을 실행한다. APP이 샌드박스 바깥의 리소스나 정보를 사용하고자 할 경우, 권한을 명확히 요청해야 한다. APP이 요청하는 권한 유형에 따라 시스템은 자동으로 권한을 부여하거나 사용자에게 권한 부여를 요청한다. Android M 버전 이전에는 Permission 을 Manifest.xml 에 정의만 하면 모두 사용할 수 있었지만, Android M 버전 이후에는 위험권한에 대한 추가적인 요청이 필요하기 때문에 개발자의 수고가 많아 졌다.

- Permssion 종류

    - Permission 종류는 실행 시간 권한인 `Runtime Permission` 과 설치 권한인 `BuildTime Permission` 이 있다.

    - Permission 처리는 `정상 권한 처리` 와  `위험 권한 처리` 가 존재한다. `정상 권한 처리`는 Manifest.xml 에 등록하면 시스템이 자동으로 권한을 APP에 부여하지만, `위험 권한 처리` 는 APP에서 위험 권한을 사용할 경우, 직접 사용자의 동의를 얻어야 하기 때문에 개발자가 코드로 직접 처리해야 한다.

    - 참조 : [시스템 권한](https://developer.android.com/guide/topics/security/permissions.html?hl=ko#normal-dangerous)

- Permission 처리 순서

  1. Manifest.xml 파일에 `<uses-permission>` 추가

  2. APP Version Check

  3. 권한 유무 확인

      3-1. 권한이 승인되어 있으면 `init()` 과 같이 초기화 함수 실행

      3-2. 승인되지 않은 권한이 있다면 권한 승인 요청

  4. 권한 승인 요청

      4-1. 권한 승인을 허가하면 `init()` 실행

      4-2. 권한 승인을 거절하면 `finish()` 호출하여 APP 종료

- Permission 예제

  1. Manifest.xml 파일에 `<uses-permission>` 추가

      ```xml
      <!-- 실행 시간 권한(Runtime Permission) -->
      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

      <!-- 설치 권한(BuildTime Permission) -->
      <uses-permission android:name="android.permission.INTERNET" />
      ```

  2. APP Version Check

      ```java
      // 0. App 버전 체크
      // 마시멜로우는 version_code 가 이니셜로 되어 있어야 한다.
      // 마시멜로우 이후에만 Permission 정책이 바뀌었기 때문에
      // 현재 버전이 마시멜로우 이상이라면 Permission Check 를 하라고 알려주는 것이다.
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          checkPermission();
      } else {
          init();
      }
      ```

  3. 권한 유무 확인

      - 직접 권한 목록 처리

      ```JAVA
      // @RequiresApi 애너테이션으로 API 의 메서드에 최소 API 레벨을 나타내는 예다.
      // 컴파일러에게 현재 버전이 마시멜로우 이상일 때 메소드를 실행하는 것이라고 알려주는 Annotation
      @RequiresApi(api = Build.VERSION_CODES.M)
      private void checkPermission() {
        // 1. 권한 유무 확인
        // 호환성 처리를 수동으로 해줘야 한다.
        // checkSelfPermission(Permission String)
        // RETURN : Integer (PackageManager.PERMISSION_GRANTED , PackageManager.PERMISSION_DENIED)
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            // 1-1. 이미 승인이 되어 있는 경우
            // 초기화 실행
            init();
        }else{
            // 1-2. 권한이 승인이 되지 않으면
            // 2. 권한 승인
            // 2-1. 요청할 권한을 정의
            // 2-2. 권한 요청
            requestPermissions(permissions, REQ_CODE);
        }
      }
      ```

      - 반복문으로 권한 목록 처리 (효율적이고 유지보수 편하다.)

      ```JAVA
      // @RequiresApi 애너테이션으로 API 의 메서드에 최소 API 레벨을 나타내는 예다.
      // 컴파일러에게 현재 버전이 마시멜로우 이상일 때 메소드를 실행하는 것이라고 알려주는 Annotation
      @RequiresApi(api = Build.VERSION_CODES.M)
      private void checkPermission() {
        // 1. 권한 유무 확인
        // 호환성 처리를 수동으로 해줘야 한다.
        // checkSelfPermission(Permission String)
        // RETURN : Integer (PackageManager.PERMISSION_GRANTED , PackageManager.PERMISSION_DENIED)
        List<String> requires = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // READ_EXTERNAL_STORAGE 이 없다면
                requires.add(permission);
            }
        }

        if (requires.size() < 0) {
            // 1-1. 이미 승인이 되어 있는 경우
            // 초기화 실행
            init();

        } else {
            // 1-2. 권한이 승인이 되지 않으면
            // 2. 권한 승인
            // 2-1. 요청할 권한을 정의
            // 2-2. 권한 요청
            String perms[] = requires.toArray(new String[requires.size()]);
            requestPermissions(perms, REQ_CODE);
        }
      }
      ```

  4. 권한 승인 요청

      ```JAVA
      @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          switch (requestCode){
              case REQ_CODE:
                  boolean flag = true;
                  for(int grantResult : grantResults){
                      if(grantResult != PackageManager.PERMISSION_GRANTED){
                          flag = false;
                          break;
                      }
                  }
                  if(flag){
                      init();
                  }else{
                      Toast.makeText(this, "권한 승인을 하지 않으면 APP 을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                      finish();
                  }
                  break;
          }
      }
      ```

- 참조 : [시스템 권한 사용](https://developer.android.com/training/permissions/index.html?hl=ko),[런타임에 권한 요청](https://developer.android.com/training/permissions/requesting.html?hl=ko)
