/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.demo.kitchensink.controller;

import com.demo.kitchensink.model.Member;
import com.demo.kitchensink.service.MemberService;
import com.demo.kitchensink.service.SequenceGeneratorService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Model;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;


// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://www.cdi-spec.org/faq/#accordion6
@Model
@Component("memberController")
public class MemberController {

    @Inject
    private FacesContext facesContext;

    @Inject
    private MemberService memberService;

    @Inject
    private SequenceGeneratorService sequenceGeneratorService;


    @Inject
    private Member newMember;

    public Member getNewMember() {
        return newMember;
    }

    public void setNewMember(Member newMember) {
        this.newMember = newMember;
    }

    private List<Member> members;

    public List<Member> getMembers() {
        members=memberService.getAllMembers();
        return members;
    }



    @PostConstruct
    public void initNewMember() {
        newMember = new Member();
    }

    public void register() throws Exception {
        try {
            newMember.setId(sequenceGeneratorService.generateSequence(Member.SEQUENCE_NAME));
            System.out.println("saving: " + newMember.getName());
            System.out.println("saving id: " + newMember.getId());
            memberService.saveMember(newMember);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, m);
            initNewMember();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, m);
        }
    }

    private String getRootErrorMessage(Exception e) {
        // Default to general error message that registration failed.
        String errorMessage = "Registration failed. See server log for more information";
        if (e == null) {
            // This shouldn't happen, but return the default messages
            return errorMessage;
        }

        // Start with the exception and recurse to find the root cause
        Throwable t = e;
        while (t != null) {
            // Get the message from the Throwable class instance
            errorMessage = t.getLocalizedMessage();
            t = t.getCause();
        }
        // This is the root cause message
        return errorMessage;
    }

}
