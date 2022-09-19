package org.maoxiaoxu.starter;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author maoxiaoxu
 * @version 1.0
 */
@Slf4j
@SpringBootTest
class SpringBootStarterTest {

  @Test
  public void base(){
    log.info("========== base start ==========");
    log.info("========== base end ==========");
  }

}