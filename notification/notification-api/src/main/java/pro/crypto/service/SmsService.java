package pro.crypto.service;

import pro.crypto.request.SmsSendRequest;

public interface SmsService {

    void sendSms(SmsSendRequest request);

}
