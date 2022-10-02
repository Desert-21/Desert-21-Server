package com.github.maciejmalewicz.Desert21.service.email;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class AccountActivationMailSenderConfigSenderTest {

    public static final String expectedMessage = """
            Hi macior123456,
                         
            Dude from Desert 21 here! Your account is almost there.
            Click on the link below to activate your account and start
            enjoying probably the first non pay-to-win and not real time
            strategic game in the internet:
            http://www.desert21.com/activate-code/macior@gmail.com/AAABBBCCC
                         
            Enjoy,
            Dude from Desert 21
            """;

    @Test
    void sendActivationCode() throws Exception {

        var mockEmailSendingService = Mockito.mock(EmailSendingService.class);

        var tested = new AccountActivationEmailSender(mockEmailSendingService);
        ReflectionTestUtils.setField(tested, "baseUrl", "http://www.desert21.com");

        tested.sendActivationCode("macior@gmail.com", "macior123456", "AAABBBCCC");
        Mockito.verify(mockEmailSendingService, Mockito.times(1))
                .send("Desert 21 Account Activation", expectedMessage, "macior@gmail.com");
    }
}