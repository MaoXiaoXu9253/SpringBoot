package org.maoxiaoxu.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author maoxiaoxu
 * @version 1.0
 */
@Slf4j
@SpringBootApplication
public class SpringBootStarter {

  public static void main(String[] args) {
    log.info("========== SpringBootStarter start ========= ");
    SpringApplication.run(SpringBootStarter.class, args);
    log.info("========== SpringBootStarter end ========= ");
  }

}
