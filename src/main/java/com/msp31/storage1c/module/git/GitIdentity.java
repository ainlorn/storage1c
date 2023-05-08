package com.msp31.storage1c.module.git;

import lombok.Value;
import org.eclipse.jgit.lib.PersonIdent;

@Value
public class GitIdentity {
    String name;
    String email;

    PersonIdent toPersonIdent() {
        return new PersonIdent(name, email);
    }

    public static GitIdentity fromPersonIdent(PersonIdent ident) {
        return new GitIdentity(ident.getName(), ident.getEmailAddress());
    }
}
