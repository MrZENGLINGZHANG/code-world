package com.sh.fbs.basic.user.infra;

import com.sh.fbs.basic.user.domain.CaptchaCheckParam;
import com.sh.fbs.basic.user.domain.CaptchaGenParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CaptchaMgmtServiceImplTest {
    @Autowired
    CaptchaMgmtServiceImpl captchaMgmtService;

    @Test
    void generateCaptcha() {
        CaptchaGenParam param = CaptchaGenParam.builder().build();
        param.setUniqKey("3L");
        param.setChannelType(1);
        param.setChannelCode("15019272742");
        captchaMgmtService.generateCaptcha(param);
    }

    @Test
    void validateCaptcha() {
        String captchaCode = "731986";
        CaptchaCheckParam param = CaptchaCheckParam.builder().build();
        param.setUniqKey("3L");
        param.setCaptcha(captchaCode);
        captchaMgmtService.validateCaptcha(param);
    }
}