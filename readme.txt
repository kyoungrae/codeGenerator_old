
* 핵심 클래스 설명
EntityXmlGenerator  --> table을 읽어서 Entity xml을 만드는 클래스
GenParser --> Entity xml을 기반으로  template 파일 내용에 맞게 source code를 generation 하는 클래스
 
 *처리 방식
 1. entity xml generator : 테이블 스키마를 읽어서 source code generator가 사용하는 entity xml을 생성
 2. source code generator : 
 