package pro.crypto.service;

import lombok.AllArgsConstructor;
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

}
