# Quizgame_CN
Computer Networking Assignment

•	서버가 클라이언트에게 저장된 질문 중 임의의 한 질문을 전송한다. 
•	클라이언트는 서버에게 받은 문제를 클라이언트(UI)에 출력해서 사용자에게 보여주고, 사용자는 해당 질문에 대한 답을 UI에 입력한다. 
•	입력된 답은 서버로 전송되고 리스트에 저장된 값과 비교되어 서버에서 Correct인지 Incorrect인지에 대한 feedback이 결정되어 UI로 Correct나 Incorrrect 둘 중 하나가 출력된다. 정답일 경우 20점이 추가되며 오답인 경우 점수가 추가되지 않는다.
•	이 과정은 currentQuestion이 totalQuestion보다 -1 작을 때까지(5번) 각기 다른 질문의 형태로 반복된다. 
•	5개의 문제를 전송/피드백을 받으면 서버는 누적된 점수인 totalScore를 클라이언트에게 전송한다. 
•	클라이언트는 전송된 점수를 UI에 띄워주고 프로그램이 종료, 서버와 클라이언트의 연결도 종료된다

================================================================================================
•	서버 파일 QuizServer
-	configFile은 server_info.dat의 IP주소와 port 번호를 읽어온다. (dat파일이 존재하지 않으면 로컬주소 127.0.0.1와 포트넘버 1234로 기본 값을 설정한다.)
-	리스트로 10개의 질문과 답을 만든다.
-	Serversocket을 생성해서 클라이언트에게서 요청이 오기를 기다린다. (Waiting for a client connection... 이 출력) 
-	ExecutiveService pool로 최대 동시 접속 가능한 멀티스레드를 20으로 제한한다. 
-	각 클라이언트는 자신만의 QuizHandler 스레드에서 처리(질문 전달, 답변 처리, 점수 계산 등의 일 처리)된다.
•	클라이언트 파일 QuizClient
-	configFile은 server_info.dat의 IP주소와 port 번호를 읽어온다. (dat파일이 존재하지 않으면 로컬주소 127.0.0.1와 포트넘버 1234로 기본 값을 설정한다.)
-	Socket을 생성하고 서버에게 연결 요청을 보내 연결한다. 
