package com.demo.kitchensink.service;

import com.demo.kitchensink.model.Member;
import com.demo.kitchensink.repository.MemberRepository;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.enterprise.event.Event;
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    //@Inject
    //private Event<Member> memberEventSrc;

    public List<Member> getAllMembers(){
        return memberRepository.findAll();
    }
    public Member saveMember(Member member){
        return memberRepository.save(member);

    }
}
