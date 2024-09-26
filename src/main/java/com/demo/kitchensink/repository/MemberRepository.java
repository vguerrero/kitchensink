package com.demo.kitchensink.repository;

import com.demo.kitchensink.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  MemberRepository  extends MongoRepository<Member, Long> {

    Member findByEmail(String email);
}
