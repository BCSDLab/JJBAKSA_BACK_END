package com.jjbacsa.jjbacsabackend.etc.annotations;

import javax.validation.groups.Default;

public final class ValidationGroups {
    private ValidationGroups() {
    }

    public interface Create extends Default {};
    public interface Update extends Default {};

    public interface Login extends Default {};

    public interface Get extends Default {};
}
