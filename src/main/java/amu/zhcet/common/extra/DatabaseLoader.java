package amu.zhcet.common.extra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseLoader implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Running DatabaseLoader");
        log.debug("Currently, no task to run");
    }
}
