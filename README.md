# Pilling

### OCR 기반 처방정보 조회 및 복약 알람 APP



<img src="https://github.com/ungyuun/Pilling/assets/95204319/75bb43fe-4e20-4b3a-9833-9d70a3ee35f4" width="20%" align="left"/>



## 제작 의도

환자들의 복약 관리를 향상시키고 의료 시스템의 디지털화에 기여 하고자 함. 
이 APP을 통해 환자들의 처방정보 확인, 복약알람 설정을 기반으로 의료 서비스의 효율
을 향상시킬 수 있는 기회를 제공하려함.



## 주요 기능

처방전을 OCR하여 처방받은 약을 스캔, 처방전을 저장함. 처방전을 누르면 해당 처방전의 약품들을 확인할수 있음. 
사용자가 알람을 추가해 지정된 시간에 알람을 울리게 함.



### 약품 정보 조회

- 처방전을 OCR하여 처방받은 약을 스캔, 처방전을 저장함. 처방전을 누르면 해당 처방전의 약품들을 확인할수 있음. 
  약품을 누르면 네이버 지식백과 api를 통해 상세 정보를 조회한다.

  <img src="https://github.com/ungyuun/Pilling/assets/95204319/0709babd-e7cb-40d4-a5ab-40b4f086f504" width="23%" />  <img src="https://github.com/ungyuun/Pilling/assets/95204319/c9aa39f4-073b-4bab-b12e-58468279c7ae" width="23%" /> <img src="https://github.com/ungyuun/Pilling/assets/95204319/f07d5e81-3b7c-432c-a4e4-8aec64efb0c3" width="23%" />  

  

### 처방전 등록 삭제

- 디바이스의 기본 카메라를 통해 처방전을 촬영하면, 이미지 전처리과정을 거친뒤, 
  Feature Matching을 하여 처방전을 등록한다.



​      <img src="https://github.com/ungyuun/Pilling/assets/95204319/b686fee3-6677-45cb-aee4-b77d422cf713" width="23%" />  <img src="https://github.com/ungyuun/Pilling/assets/95204319/25b7836f-d30f-4e0e-ac27-4583bdf266fa" width="23%" />



### 알람 설정

- 사용자가 알람을 추가해 지정된 시간에 알람을 울리게 함



​     <img src="https://github.com/ungyuun/Pilling/assets/95204319/646f87c7-dfdc-4f61-b511-79fe11093386" width="23%" /><img src="https://github.com/ungyuun/Pilling/assets/95204319/78078de9-ecc4-4503-9b20-4760602ab5aa" width="23%" /><img src="https://github.com/ungyuun/Pilling/assets/95204319/028a6c66-718e-4e02-8d07-be48c23d9483" width="23%" /><img src="https://github.com/ungyuun/Pilling/assets/95204319/585e59eb-4446-4eab-89b1-ab2fdc0fc95a" width="23%" />



### OCR 과정

- OpenCV의 Feature Matching 기술을 사용함.  이미지에서 특징적인 지점들을 찾아내고, 
  서로 다른 두 이미지 간의 해당 특징점들을 매칭하는 작업을 수행한다.

  

  <img src="https://github.com/ungyuun/Pilling/assets/95204319/a0e6b81a-1cd6-4a66-bf5e-ed597daee708" align="center" />

<img src="https://github.com/ungyuun/Pilling/assets/95204319/82609a4d-18b4-41e1-99bc-13f89e25c8e8" align="center" />



- **훈련이미지**(공통영역) 과 **쿼리이미지** (처방전)을 **서로 매칭**하여 **공통된 객체를 찾는다**. 
  만약 객체 매칭이 10개 미만이라면 False를 반환. 아니라면 **처방전의 약품명 영역을 추출**한다.

  

  <img src="https://github.com/ungyuun/Pilling/assets/95204319/57769816-1dc3-4369-9b31-5a117a5d437b" align="center" />

    

## 시스템 구조

- OCR과 DB서버를 연결하기 위한 Flask 서버
- Client의 Android App



### 서버 구동 방법

1. cd myvenv/scripts 
2. source activate (파이썬 가상환경실행)
3. python run app
