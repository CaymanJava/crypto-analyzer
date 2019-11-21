package pro.crypto.service;

import pro.crypto.request.RawEmailSendRequest;

public interface MailService {

    void sendRawEmail(RawEmailSendRequest request);

}
