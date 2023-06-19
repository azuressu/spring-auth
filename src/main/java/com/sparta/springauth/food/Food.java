package com.sparta.springauth.food;

public interface Food {

    // eat 메서드 하나 추가
    void eat();
    
    // 같은 Food 타입으로 두 개의 구현체로 만든 다음 Bean으로 등록
    // 등록 자체에는 문제가 없음
}
