package com.sparta.springauth;

import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BeanTest {
    // Autowired가 기본적으로 Bean의 타입을 기준으로 DI를 지원함
    // 그런데 연결이 되지 않으면 그 때는 Bean의 이름으로 찾음

    @Autowired // interface도 받아올 수 있음
    // Food food; // Could not autowire. There is more than one bean of 'Food' type.
    // 1. 등록된 이름 직접 명시
//    Food pizza;
//
//    @Autowired
//    Food chicken;
    // 2. 클래스 하나에 Primary 추가해주기
//    Food food;  // Primary가 추가되면, 같은 타입의 bean이 여러개 있어도 primary가 있는 bean을 추가해줌

    // 3. Qualifier 추가해주기
    @Qualifier("pizza")
    Food food;

    // 예를 들어, 우리 식당에서 Chicken이 95%의 주문비율을 차지하고 있음
    // 그렇다면 Chicken 쪽에 Primary를 걸어주는 것이 좋음
    // Pizza는 5% 주문비율임. 지역적으로 Qualifier 어노테이션을 붙여주는 것으로 함
    // Spring은 큰 범위의 우선순위가 더 낮음 (즉 chicken이 더 낮음) = 좁은 범위 설정이 우선순위가 더 높음
    @Test
    @DisplayName("Primary와 Qualifier 우선순위 확인") // Qualifier의 우선순위가 더 높음
    void test1() {
//        pizza.eat();
//        chicken.eat();
        food.eat();
    } // test1()


}
