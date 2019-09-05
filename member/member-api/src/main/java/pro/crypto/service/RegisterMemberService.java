package pro.crypto.service;

import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

public interface RegisterMemberService {

    MemberSnapshot register(MemberRegisterRequest request);

}
