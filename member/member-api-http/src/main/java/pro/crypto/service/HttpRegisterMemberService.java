package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpRegisterMemberProxy;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

@Service
@AllArgsConstructor
public class HttpRegisterMemberService implements RegisterMemberService {

    private final HttpRegisterMemberProxy registerMemberProxy;

    @Override
    public MemberSnapshot register(MemberRegisterRequest request) {
        return registerMemberProxy.register(request);
    }

    @Override
    public MemberSnapshot registerSocial(MemberRegisterRequest request) {
        throw new NotImplementedException("registerSocial() is not implemented in http service");
    }

}
